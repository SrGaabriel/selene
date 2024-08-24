package me.gabriel.gwydion.llvm.struct

sealed interface Value {
    val type: LLVMType

    fun llvm(): String
}

data object LLVMVoid : Value {
    override val type: LLVMType = LLVMType.I1

    override fun llvm(): String = "void"
}

data class LLVMConstant<T : Any>(val value: T, override val type: LLVMType): Value {
    override fun llvm(): String = value.toString()
}

sealed class MemoryUnit(
    val register: Int,
    override val type: LLVMType
): Value {
    class Sized(register: Int, type: LLVMType, val size: Int) : MemoryUnit(register, type)

    class Unsized(register: Int, type: LLVMType) : MemoryUnit(register, type)

    class TraitData(
        val vtable: MemoryUnit,
        val data: MemoryUnit,
        override val type: LLVMType.Trait,
        var loadedData: MemoryUnit? = null
    ) : MemoryUnit(vtable.register, type)

    class ArrayData(

        val pointer: MemoryUnit,
    )

    override fun llvm(): String = "%$register"
}

data object NullMemoryUnit: MemoryUnit(-1, LLVMType.I1) {
    override fun llvm(): String = error("Null memory unit")
}