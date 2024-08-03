package me.gabriel.gwydion.parsing

import me.gabriel.gwydion.lexing.TokenKind

sealed class Type(val name: kotlin.String) {
    data object Int8 : Type("int8")
    data object Int16 : Type("int16")
    data object Int32 : Type("int32")
    data object Int64 : Type("int64")
    data object UInt8 : Type("uint8")
    data object UInt16 : Type("uint16")
    data object UInt32 : Type("uint32")
    data object UInt64 : Type("uint64")
    data object Float32 : Type("float32")
    data object Float64 : Type("float64")
    data object String : Type("string")
    data object Void : Type("void")
    data object Boolean : Type("bool")
    data object Unknown : Type("unknown")

    override fun toString(): kotlin.String = name
}

fun tokenKindToType(kind: TokenKind) = when (kind) {
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
    TokenKind.IDENTIFIER -> Type.Unknown
    else -> error("Unknown token kind $kind")
}

