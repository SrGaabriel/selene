package me.gabriel.gwydion.exception

import me.gabriel.gwydion.lexing.Token
import me.gabriel.gwydion.lexing.TokenKind

sealed class ParsingError(val message: String, val token: Token) {
    class UnexpectedToken(token: Token) : ParsingError("unexpected token: ${token.kind}", token)

    class IncorrectToken(token: Token, expected: TokenKind) : ParsingError("expected $expected, got ${token.kind}", token)

    class InvalidModifier(token: Token) : ParsingError("invalid macro modifier: ${token.value}", token)

    class UnexpectedIdentifier(token: Token) : ParsingError("unexpected identifier: ${token.value}", token)

    class ParameterMissingTypeDeclaration(token: Token) : ParsingError("parameter missing type declaration", token)
}