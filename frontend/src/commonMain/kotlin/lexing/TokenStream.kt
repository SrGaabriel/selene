package me.gabriel.selene.frontend.lexing

class TokenStream(private val tokens: List<Token>) : Iterator<Token> by tokens.iterator() {
    fun count(): Int = tokens.size

    operator fun get(index: Int): Token = tokens[index]

    fun last(): Token = tokens.last()

    fun toList(): List<Token> = tokens
}