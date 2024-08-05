package me.gabriel.gwydion.llvm

import me.gabriel.gwydion.llvm.struct.*
import kotlin.math.min

class LLVMCodeAssembler(val generator: ILLVMCodeGenerator): ILLVMCodeAssembler {
    private val ir = mutableListOf<String>()
    private var register = 0

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

    override fun allocateHeapMemory(type: LLVMType): MemoryUnit {
        val register = nextRegister()
        saveToRegister(register, generator.heapMemoryAllocation(type, type.size))
        return MemoryUnit.Sized(register, type, type.size)
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

    override fun getElementFromStructAt(
        struct: Value,
        type: LLVMType,
        index: Value
    ): MemoryUnit {
        val reading = generator.unsafeSubElementAddressReading(
            struct = struct,
            index = index
        )
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

    override fun setStructElementTo(
        value: Value,
        struct: Value,
        type: LLVMType,
        index: Value
    ): MemoryUnit {
        val reference = getElementFromStructAt(
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

    override fun concatenateStrings(left: MemoryUnit, right: MemoryUnit): MemoryUnit {
        addDependency("declare i8* @strcat(i8*, i8*)")
        val leftLength = calculateStringLength(left)
        val rightLength = calculateStringLength(right)

        val totalLengthWithoutNull = addNumber(
            type = LLVMType.I32,
            left = LLVMConstant(value = leftLength, type = LLVMType.I32),
            right = LLVMConstant(value = rightLength, type = LLVMType.I32)
        )
        val totalLength = addNumber(
            type = LLVMType.I32,
            left = totalLengthWithoutNull,
            right = LLVMConstant(value = 1, type = LLVMType.I32)
        )
        val totalString = allocateHeapMemory(LLVMType.I32)

    }

    override fun calculateStringLength(string: MemoryUnit): MemoryUnit {
        addDependency("declare i64 @strlen(i8*)")
        val register = nextRegister()
        saveToRegister(register, generator.stringLengthCalculation(string))
        return MemoryUnit.Sized(register, LLVMType.I32, 8)
    }

    override fun nextRegister(): Int = register++
}