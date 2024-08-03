package me.gabriel.gwydion.lexing

import me.gabriel.gwydion.lexing.lexers.StringLexer

enum class TokenKind {
    NUMBER,
    PLUS,
    RETURN,
    SEMICOLON,
    OPENING_PARENTHESES,
    CLOSING_PARENTHESES,
}



data class Token(
    val kind: TokenKind,
    val value: String
)

