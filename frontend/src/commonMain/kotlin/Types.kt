package me.gabriel.selene.frontend

import kotlinx.serialization.Serializable
import me.gabriel.selene.frontend.lexing.Token
import me.gabriel.selene.frontend.lexing.TokenKind

@Serializable
sealed class SeleneType(
    val id: kotlin.String,
    val signature: kotlin.String
) {
    // Overloaded constructor for convenience when signature equals id
    constructor(id: kotlin.String) : this(id, id)

    // Replacing `data object` with `object` to avoid Kotlin/Native issues
    @Serializable
    object Any : SeleneType("any")
    @Serializable
    object Int8 : SeleneType("int8")
    @Serializable
    object Int16 : SeleneType("int16")
    @Serializable
    object Int32 : SeleneType("int32")
    @Serializable
    object Int64 : SeleneType("int64")
    @Serializable
    object UInt8 : SeleneType("uint8")
    @Serializable
    object UInt16 : SeleneType("uint16")
    @Serializable
    object UInt32 : SeleneType("uint32")
    @Serializable
    object UInt64 : SeleneType("uint64")
    @Serializable
    object Float32 : SeleneType("float32")
    @Serializable
    object Float64 : SeleneType("float64")
    @Serializable
    object String : SeleneType("string")
    @Serializable
    object Self: SeleneType("self")
    @Serializable
    object Void : SeleneType("void")
    @Serializable
    object Boolean : SeleneType("bool")
    @Serializable
    object Undefined : SeleneType("unknown")

    @Serializable
    data class UnknownReference(val reference: kotlin.String, val mutable: kotlin.Boolean): SeleneType("unknown", reference)

    @Serializable
    data class DynamicArray(
        val baseType: SeleneType,
    ): SeleneType("fixed array", "${baseType.signature}[]")

    @Serializable
    data class FixedArray(
        val baseType: SeleneType,
        val length: Int
    ): SeleneType("fixed array", "${baseType.signature}[$length]")

    @Serializable
    data class Struct(
        val identifier: kotlin.String,
        val fields: Map<kotlin.String, SeleneType>
    ): SeleneType("struct", identifier)

    @Serializable
    data class Trait(
        val identifier: kotlin.String,
        val functions: List<VirtualFunction>
    ): SeleneType("trait", identifier)

    @Serializable
    data class VirtualFunction(
        val name: kotlin.String,
        val returnType: SeleneType,
        val parameters: List<SeleneType>
    )

    @Serializable
    data class Mutable(
        val baseType: SeleneType
    ): SeleneType("mutate", "mut ${baseType.signature}") {
        override val base: SeleneType
            get() = baseType
    }

    @Serializable
    data class Lambda(
        val parameters: List<SeleneType>,
        val returnType: SeleneType
    ): SeleneType("lambda", "(${parameters.joinToString(", ") { it.signature }}) -> ${returnType.signature}")

    open val base: SeleneType
        get() = this

    override fun toString(): kotlin.String = id
}

fun tokenKindToType(token: Token, mutable: Boolean) = when (token.kind) {
    TokenKind.ANY_TYPE -> SeleneType.Any
    TokenKind.INT8_TYPE -> SeleneType.Int8
    TokenKind.INT16_TYPE -> SeleneType.Int16
    TokenKind.INT32_TYPE -> SeleneType.Int32
    TokenKind.INT64_TYPE -> SeleneType.Int64
    TokenKind.UINT8_TYPE -> SeleneType.UInt8
    TokenKind.UINT16_TYPE -> SeleneType.UInt16
    TokenKind.UINT32_TYPE -> SeleneType.UInt32
    TokenKind.UINT64_TYPE -> SeleneType.UInt64
    TokenKind.FLOAT32_TYPE -> SeleneType.Float32
    TokenKind.FLOAT64_TYPE -> SeleneType.Float64
    TokenKind.STRING_TYPE -> SeleneType.String
    TokenKind.BOOL_TYPE -> SeleneType.Boolean
    TokenKind.IDENTIFIER -> SeleneType.UnknownReference(token.value, mutable)
    else -> error("Unknown token kind ${token.kind}")
}

fun SeleneType.isNumeric(): Boolean = when (this) {
    is SeleneType.Int8, is SeleneType.Int16, is SeleneType.Int32, is SeleneType.Int64,
    is SeleneType.UInt8, is SeleneType.UInt16, is SeleneType.UInt32, is SeleneType.UInt64,
    is SeleneType.Float32, is SeleneType.Float64 -> true
    else -> false
}

fun SeleneType.workingBase(): SeleneType = when (this) {
    is SeleneType.Mutable -> base.workingBase()
    else -> this
}

fun SeleneType.isUndefined(): Boolean = when (this) {
    is SeleneType.Undefined -> true
    is SeleneType.FixedArray -> baseType.isUndefined()
    is SeleneType.DynamicArray -> baseType.isUndefined()
    is SeleneType.Mutable -> baseType.isUndefined()
    else -> false
}

fun SeleneType.mapBase(mapper: (SeleneType) -> SeleneType): SeleneType = when (this) {
    is SeleneType.Mutable -> SeleneType.Mutable(base.mapBase(mapper))
    is SeleneType.DynamicArray -> SeleneType.DynamicArray(baseType.mapBase(mapper))
    is SeleneType.FixedArray -> SeleneType.FixedArray(baseType.mapBase(mapper), length)
    else -> mapper(this)
}
