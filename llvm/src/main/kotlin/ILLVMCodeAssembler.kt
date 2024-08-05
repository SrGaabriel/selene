package me.gabriel.gwydion.llvm

import me.gabriel.gwydion.llvm.struct.LLVMType
import me.gabriel.gwydion.llvm.struct.MemoryUnit
import me.gabriel.gwydion.llvm.struct.Value

interface ILLVMCodeAssembler {
    fun addDependency(dependency: String)

    fun addDependencies()

    fun instruct(instruction: String)

    fun allocateStackMemory(type: LLVMType, alignment: Int): MemoryUnit

    fun allocateHeapMemory(type: LLVMType): MemoryUnit

    fun declareFunction(name: String, returnType: LLVMType, arguments: List<MemoryUnit>)

    fun createBranch(label: String)

    fun conditionalBranch(condition: MemoryUnit, trueLabel: String, falseLabel: String)

    fun unconditionalBranchTo(label: String)

    fun dynamicMemoryUnitAllocation(unit: MemoryUnit)

    fun getElementFromStructAt(
        struct: Value,
        type: LLVMType,
        index: Value
    ): MemoryUnit

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

    fun addNumber(
        type: LLVMType,
        left: Value,
        right: Value
    ): MemoryUnit

    fun buildString(text: String): MemoryUnit

    fun storeTo(value: Value, address: MemoryUnit)

    fun returnValue(type: LLVMType, value: MemoryUnit)

    fun concatenateStrings(left: MemoryUnit, right: MemoryUnit): MemoryUnit

    fun calculateStringLength(string: MemoryUnit): MemoryUnit

    fun nextRegister(): Int

    fun finish(): String
}