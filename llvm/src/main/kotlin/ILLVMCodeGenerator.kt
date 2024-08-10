package me.gabriel.gwydion.llvm

import me.gabriel.gwydion.llvm.struct.*

interface ILLVMCodeGenerator {
    fun stackMemoryAllocation(type: LLVMType, alignment: Int = type.defaultAlignment): String

    fun heapMemoryAllocation(type: LLVMType, size: Int): String

    fun heapMemoryDefinition(size: Int, value: Value): String

    fun addition(left: Value, right: Value, type: LLVMType): String

    fun stringLengthCalculation(value: Value): String

    fun strictStringLengthCalculation(argument: Value): String

    fun stringComparison(left: Value, right: Value): String

    fun functionCall(name: String, returnType: LLVMType, arguments: Collection<Value>): String

    fun signedIntegerComparison(left: Value, right: Value): String

    fun signedIntegerNotEqualComparison(left: Value, right: Value): String

    fun memoryCopy(source: MemoryUnit, destination: MemoryUnit, size: Value): String

    fun stringCopy(source: Value, destination: Value): String

    fun unsafeSubElementAddressTotalReading(struct: Value, index: Value): String

    fun unsafeSubElementAddressDirectReading(struct: Value, index: Value): String

    fun getGeneratedDependencies(): Set<String>

    fun concatenateStrings(left: Value, right: Value): String

    fun createBranch(label: String): String

    fun unconditionalBranchTo(label: String): String

    fun addNumber(type: LLVMType, left: Value, right: Value): String

    fun storage(value: Value, address: MemoryUnit): String

    fun conditionalBranch(condition: Value, trueLabel: String, falseLabel: String): String

    fun loadPointer(type: LLVMType, value: MemoryUnit): String

    fun functionDeclaration(name: String, returnType: LLVMType, arguments: List<MemoryUnit>): String

    fun returnInstruction(type: LLVMType, value: Value): String
}