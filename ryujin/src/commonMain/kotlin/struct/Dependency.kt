package me.gabriel.ryujin.struct

sealed interface Dependency {
    data class Function(
        val name: String,
        val returnType: DragonType,
        val parameters: Collection<DragonType>
    ): Dependency {
        override fun asType(): DragonType = DragonType.Void
    }

    data class Struct(
        val name: String,
        val types: Collection<DragonType>
    ): Dependency {
        override fun asType(): DragonType = DragonType.Struct(name, types)
    }

    data class Constant(
        val name: String,
        val value: Value
    ): Dependency, Value {
        override val type: DragonType
            get() = value.type

        override fun llvm(): String = "@$name"

        override fun asType(): DragonType = value.type
    }

    fun asType(): DragonType
}