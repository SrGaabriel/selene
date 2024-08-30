package me.gabriel.gwydion.frontend.lexing.lexers

import me.gabriel.gwydion.frontend.lexing.TokenStream
import me.gabriel.gwydion.frontend.lexing.error.LexingError
import me.gabriel.gwydion.tools.Either

interface Lexer {
    fun tokenize(): Either<LexingError, TokenStream>
}