package me.gabriel.ryujin.struct

sealed interface Value {
    val type: DragonType

    fun llvm(): String
}

data object Void : Value {
    override val type: DragonType = DragonType.Int1

    override fun llvm(): String = "void"
}

data class Constant<T : Any>(val value: T, override val type: DragonType): Value {
    override fun llvm(): String = value.toString()
}

data class VirtualTable(
    val values: List<Value>
): Value {
    override val type: DragonType = DragonType.Dynamic(listOf())

    override fun llvm(): String = """
        |<{
        ${values.joinToString(",\n") { "|${it.type.llvm} ${it.llvm()}" }}
        |}>
    """.trimMargin()
}

sealed class Memory(
    val register: Int,
    override val type: DragonType
): Value {
    class Sized(register: Int, type: DragonType, val size: Int) : Memory(register, type)

    class Unsized(register: Int, type: DragonType) : Memory(register, type)

    override fun llvm(): String = "%$register"
}