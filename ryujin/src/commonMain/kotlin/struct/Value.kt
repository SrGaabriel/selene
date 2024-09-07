package me.gabriel.ryujin.struct

sealed interface Value {
    val type: DragonType

    fun llvm(): String
}

data object Void : Value {
    override val type: DragonType = DragonType.Int1

    override fun llvm(): String = "void"
}

abstract class Constant(override val type: DragonType): Value {
    abstract override fun llvm(): kotlin.String

    class Number(
        val value: kotlin.String,
        type: DragonType
    ): Constant(type) {
        override fun llvm(): kotlin.String = "$value"
    }

    class FunctionPtr(
        val name: kotlin.String,
    ): Constant(
        type = DragonType.Ptr
    ) {
        override fun llvm(): kotlin.String = "@$name"
    }

    data class String(
        val value: kotlin.String
    ): Constant(
        type = DragonType.Array(DragonType.Int8, value.length + 1)
    ) {
        override fun llvm(): kotlin.String = "c\"$value\\00\""
    }

    data class VirtualTable(
        val values: Collection<Value>
    ): Constant(
        type = DragonType.VirtualTable(values.map { it.type })
    ) {
        override fun llvm(): kotlin.String = """
        |<{
        ${values.joinToString(",\n") { "|  ${it.type.llvm} ${it.llvm()}" }}
        |}>
    """.trimMargin()
    }
}

sealed class Memory(
    val register: Int,
    override val type: DragonType
): Value {
    class Sized(register: Int, type: DragonType, val size: Int) : Memory(register, type)

    class Unsized(register: Int, type: DragonType) : Memory(register, type)

    override fun llvm(): String = "%$register"
}