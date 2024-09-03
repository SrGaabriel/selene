package me.gabriel.gwydion.frontend

import kotlinx.serialization.Serializable
import me.gabriel.gwydion.frontend.lexing.Token
import me.gabriel.gwydion.frontend.lexing.TokenKind

@Serializable
sealed class GwydionType(
    val id: kotlin.String,
    val signature: kotlin.String
) {
    // Overloaded constructor for convenience when signature equals id
    constructor(id: kotlin.String) : this(id, id)

    // Replacing `data object` with `object` to avoid Kotlin/Native issues
    @Serializable
    object Any : GwydionType("any")
    @Serializable
    object Int8 : GwydionType("int8")
    @Serializable
    object Int16 : GwydionType("int16")
    @Serializable
    object Int32 : GwydionType("int32")
    @Serializable
    object Int64 : GwydionType("int64")
    @Serializable
    object UInt8 : GwydionType("uint8")
    @Serializable
    object UInt16 : GwydionType("uint16")
    @Serializable
    object UInt32 : GwydionType("uint32")
    @Serializable
    object UInt64 : GwydionType("uint64")
    @Serializable
    object Float32 : GwydionType("float32")
    @Serializable
    object Float64 : GwydionType("float64")
    @Serializable
    object String : GwydionType("string")
    @Serializable
    object Self: GwydionType("self")
    @Serializable
    object Void : GwydionType("void")
    @Serializable
    object Boolean : GwydionType("bool")
    @Serializable
    object Unknown : GwydionType("unknown") // Marking this as @Serializable

    @Serializable
    data class UnknownReference(val reference: kotlin.String, val mutable: kotlin.Boolean): GwydionType("unknown", reference)

    @Serializable
    data class DynamicArray(
        val baseType: GwydionType,
    ): GwydionType("fixed array", "${baseType.signature}[]")

    @Serializable
    data class FixedArray(
        val baseType: GwydionType,
        val length: Int
    ): GwydionType("fixed array", "${baseType.signature}[$length]")

    @Serializable
    data class Struct(
        val identifier: kotlin.String,
        val fields: Map<kotlin.String, GwydionType>
    ): GwydionType("struct", identifier)

    @Serializable
    data class Trait(
        val identifier: kotlin.String,
        val functions: List<VirtualFunction>
    ): GwydionType("trait", identifier)

    @Serializable
    data class VirtualFunction(
        val name: kotlin.String,
        val returnType: GwydionType,
        val parameters: List<GwydionType>
    )

    @Serializable
    data class Mutable(
        val baseType: GwydionType
    ): GwydionType("mutate", "mut ${baseType.signature}") {
        override val base: GwydionType
            get() = baseType
    }

    @Serializable
    data class Lambda(
        val parameters: List<GwydionType>,
        val returnType: GwydionType
    ): GwydionType("lambda", "(${parameters.joinToString(", ") { it.signature }}) -> ${returnType.signature}")

    open val base: GwydionType
        get() = this

    override fun toString(): kotlin.String = id
}

fun tokenKindToType(token: Token, mutable: Boolean) = when (token.kind) {
    TokenKind.ANY_TYPE -> GwydionType.Any
    TokenKind.INT8_TYPE -> GwydionType.Int8
    TokenKind.INT16_TYPE -> GwydionType.Int16
    TokenKind.INT32_TYPE -> GwydionType.Int32
    TokenKind.INT64_TYPE -> GwydionType.Int64
    TokenKind.UINT8_TYPE -> GwydionType.UInt8
    TokenKind.UINT16_TYPE -> GwydionType.UInt16
    TokenKind.UINT32_TYPE -> GwydionType.UInt32
    TokenKind.UINT64_TYPE -> GwydionType.UInt64
    TokenKind.FLOAT32_TYPE -> GwydionType.Float32
    TokenKind.FLOAT64_TYPE -> GwydionType.Float64
    TokenKind.STRING_TYPE -> GwydionType.String
    TokenKind.BOOL_TYPE -> GwydionType.Boolean
    TokenKind.IDENTIFIER -> GwydionType.UnknownReference(token.value, mutable)
    else -> error("Unknown token kind ${token.kind}")
}

fun GwydionType.isNumeric(): Boolean = when (this) {
    is GwydionType.Int8, is GwydionType.Int16, is GwydionType.Int32, is GwydionType.Int64,
    is GwydionType.UInt8, is GwydionType.UInt16, is GwydionType.UInt32, is GwydionType.UInt64,
    is GwydionType.Float32, is GwydionType.Float64 -> true
    else -> false
}

fun GwydionType.workingBase(): GwydionType = when (this) {
    is GwydionType.Mutable -> base.workingBase()
    else -> this
}

fun GwydionType.mapBase(mapper: (GwydionType) -> GwydionType): GwydionType = when (this) {
    is GwydionType.Mutable -> GwydionType.Mutable(base.mapBase(mapper))
    is GwydionType.DynamicArray -> GwydionType.DynamicArray(baseType.mapBase(mapper))
    is GwydionType.FixedArray -> GwydionType.FixedArray(baseType.mapBase(mapper), length)
    else -> mapper(this)
}
