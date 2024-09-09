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
        override fun llvm(): kotlin.String = value

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Number) return false

            if (type != other.type) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + value.hashCode()
            result = 31 * result + type.hashCode()
            return result
        }
    }

    data class DeclaredConstantPtr(
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
    class Sized(register: Int, type: DragonType, val size: Int) : Memory(register, type) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Sized) return false

            if (register != other.register) return false
            if (type != other.type) return false
            if (size != other.size) return false

            return true
        }

        override fun hashCode(): Int {
            var result = register
            result = 31 * result + type.hashCode()
            result = 31 * result + size
            return result
        }
    }

    class Unsized(register: Int, type: DragonType) : Memory(register, type)

    override fun llvm(): String = "%$register"
}

data object NullMemory : Memory(-1, DragonType.Void) {
    override fun llvm(): String = error("NullMemory should not be used in LLVM IR")
}