package me.gabriel.gwydion.lexing

import me.gabriel.gwydion.lexing.lexers.StringLexer

enum class TokenKind {
    NUMBER,
    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    PLUS_ASSIGN,
    MINUS_ASSIGN,
    TIMES_ASSIGN,
    DIVIDE_ASSIGN,
    RETURN,
    SEMICOLON,
    ASSIGN,
    COMMA,
    OPENING_PARENTHESES,
    CLOSING_PARENTHESES,
    FUNCTION,
    IDENTIFIER,
    OPENING_BRACES,
    CLOSING_BRACES,
    INTRINSIC,
    STRING
}

data class Token(
    val kind: TokenKind,
    val value: String,
    val position: Int
)