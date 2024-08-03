package me.gabriel.gwydion.exception

sealed class ParsingError(val message: String) {
    class UnexpectedToken(token: String) : ParsingError("Unexpected token: $token")
}