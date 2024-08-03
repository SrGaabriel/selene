package me.gabriel.gwydion.exception

sealed class ParsingError(val message: String) {
    class UnexpectedToken(token: String) : ParsingError("Unexpected token: $token")

    class UnexpectedIdentifier(identifier: String) : ParsingError("Unexpected identifier: $identifier")
}