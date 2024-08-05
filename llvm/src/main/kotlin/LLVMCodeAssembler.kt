package me.gabriel.gwydion.llvm

import me.gabriel.gwydion.llvm.struct.LLVMType
import me.gabriel.gwydion.llvm.struct.MemoryUnit

class LLVMCodeAssembler(val generator: ILLVMCodeGenerator): ILLVMCodeAssembler {
    private val ir = mutableListOf<String>()
    private var register = 0

    override fun setup() {
        generator.getGeneratedDependencies().forEach { instruct(it) }
    }

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
        TODO("Not yet implemented")
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

    override fun returnValue(type: LLVMType, value: MemoryUnit) {
        instruct(generator.returnInstruction(type, value))
    }

    override fun nextRegister(): Int = register++
}