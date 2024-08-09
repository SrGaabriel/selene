package me.gabriel.gwydion.llvm.struct

sealed class LLVMType(
    val llvm: String,
    val defaultAlignment: Int,
    val size: Int
) {
    data object Void : LLVMType("void", 0, 0)
    data object I1 : LLVMType("i1", 1, 1)
    data object I8 : LLVMType("i8", 1, 1)
    data object I32 : LLVMType("i32", 4, 4)
    data object I64 : LLVMType("i64", 8, 8)

    data class Array(val type: LLVMType, val length: Int) : LLVMType("[$length x ${type.llvm}]", type.defaultAlignment, type.size * length)

    data class Pointer(val type: LLVMType) : LLVMType("${type.llvm}*", 8, 8)

    override fun toString(): String = llvm
}

fun LLVMType.extractPrimitiveType() = when (this) {
    is LLVMType.Array -> this.type
    is LLVMType.Pointer -> this.type
    else -> this
}