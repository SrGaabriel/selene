package me.gabriel.gwydion.frontend

import kotlinx.serialization.Serializable
import me.gabriel.gwydion.frontend.lexing.Token
import me.gabriel.gwydion.frontend.lexing.TokenKind

@Serializable
sealed class Type(
    val id: kotlin.String,
    val signature: kotlin.String = id
) {
    @Serializable
    data object Any : Type("any")
    @Serializable
    data object Int8 : Type("int8")
    @Serializable
    data object Int16 : Type("int16")
    @Serializable
    data object Int32 : Type("int32")
    @Serializable
    data object Int64 : Type("int64")
    @Serializable
    data object UInt8 : Type("uint8")
    @Serializable
    data object UInt16 : Type("uint16")
    @Serializable
    data object UInt32 : Type("uint32")
    @Serializable
    data object UInt64 : Type("uint64")
    @Serializable
    data object Float32 : Type("float32")
    @Serializable
    data object Float64 : Type("float64")
    @Serializable
    data object String : Type("string")
    @Serializable
    data object Self: Type("self")
    @Serializable
    data object Void : Type("void")
    @Serializable
    data object Boolean : Type("bool")
    data object Unknown : Type("unknown")
    data class UnknownReference(val reference: kotlin.String, val mutable: kotlin.Boolean): Type("unknown", reference)
    @Serializable
    data class DynamicArray(
        val baseType: Type,
    ): Type("fixed array", "${baseType.signature}[]")
    @Serializable
    data class FixedArray(
        val baseType: Type,
        val length: Int
    ): Type("fixed array", "${baseType.signature}[$length]")

    @Serializable
    data class Struct(
        val identifier: kotlin.String,
        val fields: Map<kotlin.String, Type>
    ): Type("struct", identifier)

    @Serializable
    data class Trait(
        val identifier: kotlin.String,
        val functions: List<VirtualFunction>
    ): Type("trait", identifier)

    @Serializable
    data class VirtualFunction(
        val name: kotlin.String,
        val returnType: Type,
        val parameters: List<Type>
    )

    @Serializable
    data class Mutable(
        val baseType: Type
    ): Type("mutate", "mut ${baseType.signature}") {
        override val base: Type
            get() = baseType
    }

    open val base: Type
        get() = this

    override fun toString(): kotlin.String = id
}

fun tokenKindToType(token: Token, mutable: Boolean) = when (token.kind) {
    TokenKind.ANY_TYPE -> Type.Any
    TokenKind.INT8_TYPE -> Type.Int8
    TokenKind.INT16_TYPE -> Type.Int16
    TokenKind.INT32_TYPE -> Type.Int32
    TokenKind.INT64_TYPE -> Type.Int64
    TokenKind.UINT8_TYPE -> Type.UInt8
    TokenKind.UINT16_TYPE -> Type.UInt16
    TokenKind.UINT32_TYPE -> Type.UInt32
    TokenKind.UINT64_TYPE -> Type.UInt64
    TokenKind.FLOAT32_TYPE -> Type.Float32
    TokenKind.FLOAT64_TYPE -> Type.Float64
    TokenKind.STRING_TYPE -> Type.String
    TokenKind.BOOL_TYPE -> Type.Boolean
    TokenKind.IDENTIFIER -> Type.UnknownReference(token.value, mutable)
    else -> error("Unknown token kind ${token.kind}")
}

fun Type.isNumeric(): Boolean = when (this) {
    is Type.Int8, is Type.Int16, is Type.Int32, is Type.Int64,
    is Type.UInt8, is Type.UInt16, is Type.UInt32, is Type.UInt64,
    is Type.Float32, is Type.Float64 -> true
    else -> false
}

fun Type.workingBase(): Type = when (this) {
    is Type.Mutable -> base.workingBase()
    else -> this
}