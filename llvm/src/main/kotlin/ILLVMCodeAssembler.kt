package me.gabriel.gwydion.llvm

import me.gabriel.gwydion.llvm.struct.LLVMType
import me.gabriel.gwydion.llvm.struct.MemoryUnit

interface ILLVMCodeAssembler {
    fun setup()

    fun instruct(instruction: String)

    fun allocateStackMemory(type: LLVMType, alignment: Int): MemoryUnit

    fun allocateHeapMemory(type: LLVMType): MemoryUnit

    fun declareFunction(name: String, returnType: LLVMType, arguments: List<MemoryUnit>)

    fun createBranch(label: String)

    fun conditionalBranch(condition: MemoryUnit, trueLabel: String, falseLabel: String)

    fun unconditionalBranchTo(label: String)

    fun returnValue(type: LLVMType, value: MemoryUnit)

    fun nextRegister(): Int
}