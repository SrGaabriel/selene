package me.gabriel.gwydion.lexing.lexers

import me.gabriel.gwydion.util.Either
import me.gabriel.gwydion.exception.LexingError
import me.gabriel.gwydion.lexing.Token
import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.lexing.TokenStream

class StringLexer(private val data: String): Lexer {
    private var position = 0

    fun tokenize(): Either<LexingError, TokenStream> {
        val tokens = mutableListOf<Token>()
        while (position < data.length) {
            when (val token = data[position]) {
                ' ', '\n', '\t', '\r' -> position++
                '+' -> tokens.add(Token(TokenKind.PLUS, "+")).also { position++ }
                '-' -> tokens.add(Token(TokenKind.MINUS, "-")).also { position++ }
                '*' -> tokens.add(Token(TokenKind.TIMES, "*")).also { position++ }
                '/' -> tokens.add(Token(TokenKind.DIVIDE, "/")).also { position++ }
                ';' -> tokens.add(Token(TokenKind.SEMICOLON, ";")).also { position++ }
                '{' -> tokens.add(Token(TokenKind.OPENING_BRACES, "{")).also { position++ }
                '}' -> tokens.add(Token(TokenKind.CLOSING_BRACES, "}")).also { position++ }
                '(' -> tokens.add(Token(TokenKind.OPENING_PARENTHESES, "(")).also { position++ }
                ')' -> tokens.add(Token(TokenKind.CLOSING_PARENTHESES, ")")).also { position++ }
                ',' -> tokens.add(Token(TokenKind.COMMA, ",")).also { position++ }
                in '0'..'9' -> tokens.add(number())
                in 'a'..'z' -> {
                    val identifier = identifier(token.toString())
                    if (identifier.isLeft()) {
                        return Either.Left(identifier.getLeft())
                    }
                    tokens.add(identifier.getRight())
                }
                ':' -> {
                    if (data[position + 1] == '=') {
                        tokens.add(Token(TokenKind.ASSIGN, ":=")).also { position += 2 }
                    } else {
                        return Either.Left(LexingError.UnknownToken(token.toString()))
                    }
                }
                else -> return Either.Left(LexingError.UnknownToken(token.toString()))
            }
        }
        return Either.Right(TokenStream(tokens))
    }

    fun number(): Token {
        val start = position
        while (position < data.length && data[position].isDigit()) {
            position++
        }
        return Token(TokenKind.NUMBER, data.substring(start, position))
    }

    fun identifier(value: String): Either<LexingError, Token> {
        val start = position
        while (position < data.length && data[position].isLetterOrDigit()) {
            position++
        }
        val value = data.substring(start, position)
        return when (value) {
            "fn" -> Either.Right(Token(TokenKind.FUNCTION, value))
            "return" -> Either.Right(Token(TokenKind.RETURN, value))
            else -> Either.Right(Token(TokenKind.IDENTIFIER, value))
        }
    }
}