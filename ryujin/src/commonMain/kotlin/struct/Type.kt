package me.gabriel.ryujin.struct

sealed class DragonType(
    val llvm: String,
    val defaultAlignment: Int,
    val size: Int
) {
    data object Void : DragonType("void", 0, 0)
    data object Int1 : DragonType("i1", 1, 1)
    data object Int8 : DragonType("i8", 1, 1)
    data object Int16 : DragonType("i16", 2, 2)
    data object Int32 : DragonType("i32", 4, 4)
    data object Int64 : DragonType("i64", 8, 8)

    data object Float32 : DragonType("float", 4, 4)
    data object Float64 : DragonType("double", 8, 8)

    data class Array(val type: DragonType, val length: Int) : DragonType("[$length x ${type.llvm}]", type.defaultAlignment, type.size * length)

    data object Ptr : DragonType("ptr", 1, 1)
    data class Pointer(val type: DragonType) : DragonType(if (type == Void) type.llvm else "${type.llvm}*", 8, 8)

    data class Struct(
        val name: String,
        val types: Collection<DragonType>
    ) : DragonType("%$name", 8, types.sumOf { it.size })

    data class Trait(
        val name: String,
        val types: Collection<DragonType>
    ) : DragonType("%$name", 8, 0)

    data class VirtualTable(
        val types: List<DragonType>
    ): DragonType(types.joinToString(
        prefix = "<{ ",
        postfix = " }>",
        separator = ", "
    ) { it.llvm }, 8, types.sumOf { it.size })

    data object Vararg : DragonType("...", 0, 0)

    override fun toString(): String = llvm
}

fun DragonType.extractPrimitiveType() = when (this) {
    is DragonType.Pointer -> this.type
    else -> this
}

fun DragonType.descendOneLevel(): DragonType = when (this) {
    is DragonType.Array -> this.type
    is DragonType.Pointer -> this.type
    else -> error("Cannot descend one level on type $this")
}