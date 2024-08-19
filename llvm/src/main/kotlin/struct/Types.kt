package me.gabriel.gwydion.llvm.struct

sealed class LLVMType(
    val llvm: String,
    val defaultAlignment: Int,
    val size: Int
) {
    data object Void : LLVMType("void", 0, 0)
    data object I1 : LLVMType("i1", 1, 1)
    data object I8 : LLVMType("i8", 1, 1)
    data object I16 : LLVMType("i16", 2, 2)
    data object I32 : LLVMType("i32", 4, 4)
    data object I64 : LLVMType("i64", 8, 8)

    data object F32 : LLVMType("float", 4, 4)
    data object F64 : LLVMType("double", 8, 8)

    data class Array(val type: LLVMType, val length: Int) : LLVMType("[$length x ${type.llvm}]", type.defaultAlignment, type.size * length)

    data object Ptr : LLVMType("ptr", 1, 1)
    data class Pointer(val type: LLVMType) : LLVMType(if (type == Void) type.llvm else "${type.llvm}*", 8, 8)

    data class Function(
        val parameterTypes: List<LLVMType>,
        val returnType: LLVMType
    ) : LLVMType("void", 0, 0)

    data class Struct(
        val name: String,
        val fields: Map<String, LLVMType>
    ) : LLVMType("%$name", 8, fields.values.sumOf { it.size })

    data class Trait(
        val name: String,
        val functions: Int
    ) : LLVMType("%$name", 8, 0)

    data class Dynamic(
        val types: List<LLVMType>
    ): LLVMType(types.joinToString(
        prefix = "<{",
        postfix = "}>",
        separator = ", "
    ) { it.llvm }, 8, types.sumOf { it.size })

    override fun toString(): String = llvm
}

fun LLVMType.extractPrimitiveType() = when (this) {
    is LLVMType.Array -> this.type
    is LLVMType.Pointer -> this.type
    else -> this
}

fun LLVMType.descendOneLevel(): LLVMType = when (this) {
    is LLVMType.Array -> this.type
    is LLVMType.Pointer -> this.type
    else -> error("Cannot descend one level on non-array or non-pointer type")
}