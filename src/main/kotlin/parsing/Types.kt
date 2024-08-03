package me.gabriel.gwydion.parsing

import me.gabriel.gwydion.lexing.TokenKind

object Types {
}

fun tokenKindToType(kind: TokenKind) = when (kind) {
    TokenKind.INT8_TYPE -> Type.INT8
    TokenKind.INT16_TYPE -> Type.INT16
    TokenKind.INT32_TYPE -> Type.INT32
    TokenKind.INT64_TYPE -> Type.INT64
    TokenKind.UINT8_TYPE -> Type.UINT8
    TokenKind.UINT16_TYPE -> Type.UINT16
    TokenKind.UINT32_TYPE -> Type.UINT32
    TokenKind.UINT64_TYPE -> Type.UINT64
    TokenKind.FLOAT32_TYPE -> Type.FLOAT32
    TokenKind.FLOAT64_TYPE -> Type.FLOAT64
    TokenKind.STRING_TYPE -> Type.STRING
    TokenKind.IDENTIFIER -> Type.UNKNOWN
    else -> error("Unknown token kind $kind")
}

