package me.gabriel.selene.frontend.lexing.lexers

import me.gabriel.selene.frontend.lexing.TokenStream
import me.gabriel.selene.frontend.lexing.error.LexingError
import me.gabriel.selene.tools.Either

interface Lexer {
    fun tokenize(): Either<LexingError, TokenStream>
}