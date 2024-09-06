package me.gabriel.selene.frontend.lexing.error

// I won't use exceptions because they generate too much overhead by default, and I'm not going to use them for control flow.
sealed class LexingError(val message: String, val position: Int, val length: Int) {
    class UnknownToken(token: String, position: Int, length: Int)
        : LexingError("unknown token '$token'", position, length)

    class UnterminatedStringVariableReference(position: Int, length: Int)
        : LexingError("unterminated string variable reference", position, length)

    class UnclosedString(position: Int, length: Int)
        : LexingError("unclosed string", position, length)
}