package me.gabriel.gwydion.llvm

import me.gabriel.gwydion.llvm.struct.LLVMType
import me.gabriel.gwydion.llvm.struct.MemoryUnit
import me.gabriel.gwydion.llvm.struct.Value

interface ILLVMCodeAssembler {
    fun addDependency(dependency: String)

    fun addDependencies()

    fun instruct(instruction: String)

    fun allocateStackMemory(type: LLVMType, alignment: Int): MemoryUnit

    fun allocateHeapMemory(size: Int): MemoryUnit

    fun declareFunction(name: String, returnType: LLVMType, arguments: List<MemoryUnit>)

    fun createBranch(label: String)

    fun conditionalBranch(condition: MemoryUnit, trueLabel: String, falseLabel: String)

    fun compareAndBranch(
        condition: Value,
        trueLabel: String,
        falseLabel: String
    )

    fun unconditionalBranchTo(label: String)

    fun dynamicMemoryUnitAllocation(unit: MemoryUnit)

    fun createArray(
        type: LLVMType,
        size: Int?,
        elements: List<Value>
    ): MemoryUnit

    fun getElementFromStructure(
        struct: Value,
        type: LLVMType,
        index: Value,
        total: Boolean = true
    ): MemoryUnit

    fun loadPointer(value: MemoryUnit): MemoryUnit

    fun setStructElementTo(
        value: Value,
        struct: Value,
        type: LLVMType,
        index: Value
    ): MemoryUnit

    fun closeBrace()

    fun functionDsl(
        name: String,
        returnType: LLVMType,
        arguments: List<MemoryUnit>,
        callback: (ILLVMCodeAssembler) -> Unit
    ) {
        declareFunction(
            name, returnType, arguments
        )
        callback(this)
        closeBrace()
    }

    fun returnVoid()

    fun returnValue(value: Value)

    fun callFunction(
        name: String,
        arguments: Collection<Value>,
        assignment: Value
    )

    fun declareStruct(
        fields: Map<String, LLVMType>
    ): MemoryUnit

    fun addNumber(
        type: LLVMType,
        left: Value,
        right: Value
    ): MemoryUnit

    fun buildString(text: String): MemoryUnit

    fun storeTo(value: Value, address: MemoryUnit)

    fun returnValue(type: LLVMType, value: MemoryUnit)

    fun copySourceToDestinationString(source: MemoryUnit, destination: MemoryUnit)

    fun addSourceToDestinationString(source: MemoryUnit, destination: MemoryUnit)

    fun calculateStringLength(string: MemoryUnit): MemoryUnit

    fun handleComparison(left: MemoryUnit, right: MemoryUnit, type: LLVMType): MemoryUnit

    fun compareStrings(left: MemoryUnit, right: MemoryUnit): MemoryUnit

    fun isTrue(value: Value): MemoryUnit

    fun isNotTrue(value: Value): MemoryUnit

    fun isFalse(value: Value): MemoryUnit

    fun nextRegister(): Int

    fun nextLabel(): String

    fun finish(): String
}