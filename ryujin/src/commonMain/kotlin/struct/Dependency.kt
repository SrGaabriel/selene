package me.gabriel.ryujin.struct

sealed interface Dependency {
    data class Function(
        val name: String,
        val returnType: DragonType,
        val parameters: List<DragonType>
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
        val type: DragonType,
        val value: Value
    ): Dependency {
        override fun asType(): DragonType = type
    }

    fun asType(): DragonType
}