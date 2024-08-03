package me.gabriel.gwydion.exception

import me.gabriel.gwydion.lexing.Token

// I won't use exceptions because they generate too much overhead by default, and I'm not going to use them for control flow.
sealed class LexingError(val message: String) {
    class UnknownToken(token: String) : LexingError("Unknown token: $token")
}