package me.gabriel.gwydion.llvm

import me.gabriel.gwydion.llvm.struct.*
import kotlin.math.min

class LLVMCodeAssembler(val generator: ILLVMCodeGenerator): ILLVMCodeAssembler {
    private val ir = mutableListOf<String>()
    private var register = 0
    private var label = 0

    override fun addDependency(dependency: String) {
        ir.add(0, dependency)
    }

    override fun addDependencies() {
        generator.getGeneratedDependencies().reversed().forEach {
            addDependency(it)
        }
    }

    override fun finish(): String = ir.joinToString("\n")

    override fun instruct(instruction: String) {
        ir.add(instruction)
    }

    fun saveToRegister(register: Int, expression: String) {
        instruct("%$register = $expression")
    }

    override fun allocateStackMemory(type: LLVMType, alignment: Int): MemoryUnit {
        val register = nextRegister()
        saveToRegister(register, generator.stackMemoryAllocation(type, alignment))
        return MemoryUnit.Sized(register, type, type.size)
    }

    override fun allocateHeapMemory(size: Int): MemoryUnit {
        val register = nextRegister()
        saveToRegister(register, generator.heapMemoryAllocation(LLVMType.I8, size))
        val value = MemoryUnit.Sized(register, LLVMType.Pointer(LLVMType.I8), size)
        instruct(generator.heapMemoryDefinition(size, value))
        return value
    }

    override fun declareFunction(name: String, returnType: LLVMType, arguments: List<MemoryUnit>) {
        instruct(generator.functionDeclaration(name, returnType, arguments))
    }

    override fun createBranch(label: String) {
        instruct(generator.createBranch(label))
    }

    override fun conditionalBranch(condition: MemoryUnit, trueLabel: String, falseLabel: String) {
        instruct(generator.conditionalBranch(condition, trueLabel, falseLabel))
    }

    override fun compareAndBranch(condition: Value, trueLabel: String, falseLabel: String) {
        if (condition.type !== LLVMType.I1) {
            error("Condition must be of type i1")
        }
        val comparison = MemoryUnit.Sized(
            register = nextRegister(),
            type = LLVMType.I1,
            size = 1
        )
        saveToRegister(
            comparison.register,
            generator.signedIntegerComparison(condition, LLVMConstant(1, LLVMType.I1))
        )
        instruct(generator.conditionalBranch(comparison, trueLabel, falseLabel))
    }

    override fun unconditionalBranchTo(label: String) {
        instruct(generator.unconditionalBranchTo(label))
    }

    override fun closeBrace() {
        instruct("}")
    }

    override fun addNumber(type: LLVMType, left: Value, right: Value): MemoryUnit {
        val register = nextRegister()
        saveToRegister(register, generator.addNumber(type, left, right))
        return MemoryUnit.Sized(
            register = register,
            type = type,
            size = type.size
        )
    }

    override fun dynamicMemoryUnitAllocation(unit: MemoryUnit) {
        when (unit) {
            is MemoryUnit.Unsized -> saveToRegister(unit.register, generator.heapMemoryAllocation(
                size = min(unit.type.size, 64),
                type = unit.type
            ))
            is MemoryUnit.Sized -> {
                if (unit.size > 1024) {
                    saveToRegister(unit.register, generator.heapMemoryAllocation(
                        size = unit.size,
                        type = unit.type
                    ))
                }
                saveToRegister(unit.register, generator.stackMemoryAllocation(
                    type = unit.type,
                    alignment = unit.type.defaultAlignment
                ))
            }
            is MemoryUnit.Structure -> saveToRegister(unit.register, generator.heapMemoryAllocation(
                size = min(unit.allocation.size, 64),
                type = unit.allocation.type
            ))
            NullMemoryUnit -> error("Tried to store null memory unit")
        }
    }

    override fun callFunction(name: String, arguments: Collection<Value>, assignment: Value) {
        val call = generator.functionCall(
            name = name,
            arguments = arguments,
            returnType = assignment.type
        )

        when (assignment) {
            NullMemoryUnit -> instruct(call)
            is MemoryUnit -> saveToRegister(assignment.register, call)
            else -> instruct(call)
        }
    }

    override fun createArray(type: LLVMType, size: Int, elements: List<Value>): MemoryUnit {
        val unit = MemoryUnit.Sized(
            register = nextRegister(),
            type = LLVMType.Array(type, size),
            size = size
        )
        dynamicMemoryUnitAllocation(unit)

        var firstPointer: MemoryUnit? = null
        elements.forEachIndexed { index, element ->
            val reference = setStructElementTo(
                value = element,
                struct = unit,
                type = type,
                index = LLVMConstant(value = index, type = LLVMType.I32)
            )
            if (firstPointer == null) {
                firstPointer = reference
            }
        }
        println("firstPointer: ${firstPointer?.type} ${firstPointer?.register}")
        if (firstPointer == null) {
            return unit
        }

        return MemoryUnit.Structure(
            pointer = firstPointer!! as MemoryUnit.Sized,
            allocation = unit
        )
    }

    override fun getElementFromStructure(
        struct: Value,
        type: LLVMType,
        index: Value,
        total: Boolean
    ): MemoryUnit {
        val reading = if (total) {
            generator.unsafeSubElementAddressTotalReading(
                struct = struct,
                index = index
            )
        } else {
            generator.unsafeSubElementAddressDirectReading(
                struct = struct,
                index = index
            )
        }
        val unit = MemoryUnit.Sized(
            register = nextRegister(),
            type = LLVMType.Pointer(type),
            size = type.size
        )
        saveToRegister(
            register = unit.register,
            expression = reading
        )
        return unit
    }

    //  load i8*, i8** X
    override fun loadPointer(value: MemoryUnit): MemoryUnit {
        if (value.type !is LLVMType.Pointer) {
            error("Expected pointer type, got ${value.type}")
        }
        val pointerType = value.type as LLVMType.Pointer
        val unit = MemoryUnit.Sized(
            register = nextRegister(),
            type = pointerType.type,
            size = pointerType.size
        )
        saveToRegister(
            register = unit.register,
            expression = generator.loadPointer(value)
        )
        return unit
    }

    override fun smartGetElementFromStructure(struct: MemoryUnit.Structure, index: Value, total: Boolean): MemoryUnit {
        val reading = if (total) {
            generator.unsafeSubElementAddressTotalReading(
                struct = struct.allocation,
                index = index
            )
        } else {
            generator.unsafeSubElementAddressDirectReading(
                struct = struct.allocation,
                index = index
            )
        }
        val unit = MemoryUnit.Sized(
            register = nextRegister(),
            type = struct.pointer.type,
            size = struct.allocation.size
        )
        saveToRegister(
            register = unit.register,
            expression = reading
        )
        return unit
    }

    override fun setStructElementTo(
        value: Value,
        struct: Value,
        type: LLVMType,
        index: Value
    ): MemoryUnit {
        val reference = getElementFromStructure(
            struct, type, index
        )
        instruct(generator.storage(value, reference))
        return reference
    }

    override fun storeTo(value: Value, address: MemoryUnit) {
        instruct(generator.storage(value, address))
    }

    override fun returnValue(type: LLVMType, value: MemoryUnit) {
        instruct(generator.returnInstruction(type, value))
    }

    override fun buildString(text: String): MemoryUnit {
        val unit = MemoryUnit.Sized(
            register = nextRegister(),
            type = LLVMType.Array(LLVMType.I8, text.length + 1),
            size = text.length + 1
        )
        dynamicMemoryUnitAllocation(unit)

        var firstPointer: MemoryUnit? = null
        text.forEachIndexed { index, char ->
            val reference = setStructElementTo(
                value = LLVMConstant(value = char.code, type = LLVMType.I8),
                struct = unit,
                type = LLVMType.I8,
                index = LLVMConstant(value = index, type = LLVMType.I32)
            )
            if (firstPointer == null) {
                firstPointer = reference
            }
        }
        val nullTerminator = setStructElementTo(
            value = LLVMConstant(value = 0, type = LLVMType.I8),
            struct = unit,
            type = LLVMType.I8,
            index = LLVMConstant(value = text.length, type = LLVMType.I32)
        )
        if (firstPointer == null) {
            return nullTerminator
        }

        return firstPointer!!
    }

    override fun returnVoid() {
        instruct("ret void")
    }

    override fun returnValue(value: Value) {
        instruct("ret ${value.llvm()}")
    }

    override fun copySourceToDestinationString(source: MemoryUnit, destination: MemoryUnit) {
        nextRegister()
        instruct(generator.stringCopy(source, destination))
    }

    override fun addSourceToDestinationString(source: MemoryUnit, destination: MemoryUnit) {
        nextRegister()
        instruct(generator.concatenateStrings(source, destination))
    }

    override fun calculateStringLength(string: MemoryUnit): MemoryUnit {
        val register = nextRegister()
        saveToRegister(register, generator.stringLengthCalculation(string))
        return MemoryUnit.Sized(register, LLVMType.I32, 8)
    }

    override fun isTrue(value: Value): MemoryUnit =
        isBooleanValue(value, expected = true)

    override fun isNotTrue(value: Value): MemoryUnit =
        isBooleanNotValue(value, expected = false)

    override fun isFalse(value: Value): MemoryUnit =
        isBooleanValue(value, expected = false)

    fun isBooleanValue(value: Value, expected: Boolean): MemoryUnit {
        val unit = MemoryUnit.Sized(
            register = nextRegister(),
            type = LLVMType.I1,
            size = 1
        )
        saveToRegister(
            register = unit.register,
            expression = generator.signedIntegerComparison(value, LLVMConstant(if (expected) 1 else 0, LLVMType.I1))
        )
        return unit
    }

    fun isBooleanNotValue(value: Value, expected: Boolean): MemoryUnit {
        val unit = MemoryUnit.Sized(
            register = nextRegister(),
            type = LLVMType.I1,
            size = 1
        )
        saveToRegister(
            register = unit.register,
            expression = generator.signedIntegerNotEqualComparison(value, LLVMConstant(if (expected) 1 else 0, LLVMType.I1))
        )
        return unit
    }

    override fun compareStrings(left: MemoryUnit, right: MemoryUnit): MemoryUnit {
        val unit = MemoryUnit.Sized(
            register = nextRegister(),
            type = LLVMType.I1,
            size = 1
        )
        saveToRegister(
            register = unit.register,
            expression = generator.stringComparison(left, right)
        )
        return unit
    }

    override fun handleComparison(left: MemoryUnit, right: MemoryUnit, type: LLVMType): MemoryUnit {
        when (type) {
            LLVMType.I1 -> {
                val comparison = addNumber(
                    type = type,
                    left = left,
                    right = right
                )
                return isTrue(comparison)
            }
            is LLVMType.Pointer -> {
                if (type.type == LLVMType.I8) {
                    val comparison = compareStrings(
                        left = left,
                        right = right
                    )
                    return isFalse(comparison)
                }
                error("Type ${type.type} not supported")
            }
            else -> error("Type $type not supported")
        }
    }

    override fun nextRegister(): Int = (register++ * 2)

    override fun nextLabel(): String = "label${label++}"
}