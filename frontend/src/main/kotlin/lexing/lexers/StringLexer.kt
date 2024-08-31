package me.gabriel.gwydion.frontend.lexing.lexers

import me.gabriel.gwydion.frontend.lexing.Token
import me.gabriel.gwydion.frontend.lexing.TokenKind
import me.gabriel.gwydion.frontend.lexing.TokenStream
import me.gabriel.gwydion.frontend.lexing.error.LexingError
import me.gabriel.gwydion.tools.Either

class StringLexer(private val data: String): Lexer {
    private var position = 0

    override fun tokenize(): Either<LexingError, TokenStream> {
        val tokens = mutableListOf<Token>()
        while (position < data.length) {
            when (val token = data[position]) {
                ' ', '\n', '\t', '\r' -> position++
                ';' -> tokens.add(Token(TokenKind.SEMICOLON, ";", position)).also { position++ }
                '{' -> tokens.add(Token(TokenKind.OPENING_BRACES, "{", position)).also { position++ }
                '}' -> tokens.add(Token(TokenKind.CLOSING_BRACES, "}", position)).also { position++ }
                '(' -> tokens.add(Token(TokenKind.OPENING_PARENTHESES, "(", position)).also { position++ }
                ')' -> tokens.add(Token(TokenKind.CLOSING_PARENTHESES, ")", position)).also { position++ }
                ',' -> tokens.add(Token(TokenKind.COMMA, ",", position)).also { position++ }
                '[' -> tokens.add(Token(TokenKind.OPENING_BRACKETS, "[", position)).also { position++ }
                ']' -> tokens.add(Token(TokenKind.CLOSING_BRACKETS, "]", position)).also { position++ }
                '@' -> tokens.add(Token(TokenKind.INSTANTIATION, "@", position)).also { position++ }
                '"' -> return Either.left(lexString(tokens) ?: continue)
                in '0'..'9' -> tokens.add(number())
                in 'a'..'z', in 'A'..'Z', '_' -> {
                    val identifier = identifier(token.toString())
                    if (identifier.isLeft()) {
                        return Either.Left(identifier.getLeft())
                    }
                    tokens.add(identifier.getRight())
                }
                '.' -> {
                    if (data[position + 1] == '.') {
                        tokens.add(Token(TokenKind.RANGE, "..", position)).also { position += 2 }
                    } else {
                        tokens.add(Token(TokenKind.DOT, ".", position)).also { position++ }
                    }
                }
                ':' -> {
                    if (data[position + 1] == '=') {
                        tokens.add(Token(TokenKind.DECLARATION, ":=", position)).also { position += 2 }
                    } else if (data[position + 1] == ':') {
                        tokens.add(Token(TokenKind.RETURN_TYPE_DECLARATION, "->", position)).also { position += 2 }
                    } else {
                        tokens.add(Token(TokenKind.TYPE_DECLARATION, ":", position)).also { position++ }
                    }
                }
                '+' -> {
                    if (data[position + 1] == '=') {
                        tokens.add(Token(TokenKind.PLUS_ASSIGN, "++", position)).also { position += 2 }
                    } else {
                        tokens.add(Token(TokenKind.PLUS, "+", position)).also { position++ }
                    }
                }
                '-' -> {
                    if (data[position + 1] == '=') {
                        tokens.add(Token(TokenKind.MINUS_ASSIGN, "--", position)).also { position += 2 }
                    } else {
                        tokens.add(Token(TokenKind.MINUS, "-", position)).also { position++ }
                    }
                }
                '*' -> {
                    if (data[position + 1] == '=') {
                        tokens.add(Token(TokenKind.TIMES_ASSIGN, "**", position)).also { position += 2 }
                    } else {
                        tokens.add(Token(TokenKind.TIMES, "*", position)).also { position++ }
                    }
                }
                '/' -> {
                    if (data[position + 1] == '=') {
                        tokens.add(Token(TokenKind.DIVIDE_ASSIGN, "//", position)).also { position += 2 }
                    } else if (data[position + 1] == '/') {
                        while (position < data.length && data[position] != '\n') {
                            position++
                        }
                    } else {
                        tokens.add(Token(TokenKind.DIVIDE, "/", position)).also { position++ }
                    }
                }
                '=' -> {
                    if (data[position + 1] == '=') {
                        tokens.add(Token(TokenKind.EQUALS, "==", position)).also { position += 2 }
                    } else {
                        tokens.add(Token(TokenKind.MUTATION, "=", position)).also { position++ }
                    }
                }
                else -> return Either.Left(LexingError.UnknownToken(token.toString(), position))
            }
        }
        return Either.Right(TokenStream(tokens))
    }

    fun number(): Token {
        val start = position
        while (position < data.length && data[position].isDigit()) {
            position++
        }
        return Token(TokenKind.NUMBER, data.substring(start, position), position)
    }

    fun identifier(value: String): Either<LexingError, Token> {
        val start = position
        while (position < data.length && (data[position].isLetterOrDigit() || data[position] == '_')) {
            position++
        }
        return when (val value = data.substring(start, position)) {
            "func" -> Either.Right(Token(TokenKind.FUNCTION, value, start))
            "return" -> Either.Right(Token(TokenKind.RETURN, value, start))
            "string" -> Either.Right(Token(TokenKind.STRING_TYPE, value, start))
            "bool" -> Either.Right(Token(TokenKind.BOOL_TYPE, value, start))
            "true", "false" -> Either.Right(Token(TokenKind.BOOL_TYPE, value, start))
            "if" -> Either.Right(Token(TokenKind.IF, value, start))
            "else" -> Either.Right(Token(TokenKind.ELSE, value, start))
            "any" -> Either.Right(Token(TokenKind.ANY_TYPE, value, start))
            "int8" -> Either.Right(Token(TokenKind.INT8_TYPE, value, start))
            "int16" -> Either.Right(Token(TokenKind.INT16_TYPE, value, start))
            "int32" -> Either.Right(Token(TokenKind.INT32_TYPE, value, start))
            "int64" -> Either.Right(Token(TokenKind.INT64_TYPE, value, start))
            "uint8" -> Either.Right(Token(TokenKind.UINT8_TYPE, value, start))
            "uint16" -> Either.Right(Token(TokenKind.UINT16_TYPE, value, start))
            "uint32" -> Either.Right(Token(TokenKind.UINT32_TYPE, value, start))
            "uint64" -> Either.Right(Token(TokenKind.UINT64_TYPE, value, start))
            "float32" -> Either.Right(Token(TokenKind.FLOAT32_TYPE, value, start))
            "float64" -> Either.Right(Token(TokenKind.FLOAT64_TYPE, value, start))
            "intrinsic" -> Either.Right(Token(TokenKind.INTRINSIC, value, start))
            "internal" -> Either.Right(Token(TokenKind.INTERNAL, value, start))
            "for" -> Either.Right(Token(TokenKind.FOR, value, start))
            "in" -> Either.Right(Token(TokenKind.IN, value, start))
            "data" -> Either.Right(Token(TokenKind.DATA, value, start))
            "trait" -> Either.Right(Token(TokenKind.TRAIT, value, start))
            "make" -> Either.Right(Token(TokenKind.MAKE, value, start))
            "into" -> Either.Right(Token(TokenKind.INTO, value, start))
            "self" -> Either.Right(Token(TokenKind.SELF, value, start))
            "mut" -> Either.Right(Token(TokenKind.MUT, value, start))
            "void" -> Either.Right(Token(TokenKind.VOID, value, start))
            else -> Either.Right(Token(TokenKind.IDENTIFIER, value, start))
        }
    }

    fun lexString(tokens: MutableList<Token>): LexingError? {
        var startPosition = position
        position++ // Skip the opening quote

        tokens.add(Token(TokenKind.STRING_START, "\"", startPosition))

        while (position < data.length && data[position] != '"') {
            when (data[position]) {
                '$' -> {
                    if (position > startPosition + 1) {
                        tokens.add(Token(TokenKind.STRING_TEXT, data.substring(startPosition + 1, position), startPosition + 1))
                    }
                    position++
                    if (data[position] == '{') {
                        position++
                        val variableStart = position
                        while (position < data.length && data[position] != '}') {
                            position++
                        }
                        if (position == data.length) {
                            return LexingError.UnterminatedStringVariableReference(variableStart)
                        }
                        tokens.add(Token(TokenKind.STRING_EXPRESSION_REFERENCE, data.substring(variableStart, position), variableStart))
                        position++ // Skip the }
                    } else {
                        val variableStart = position
                        while (position < data.length && (data[position].isLetterOrDigit() || data[position] == '_')) {
                            position++
                        }
                        tokens.add(Token(TokenKind.STRING_EXPRESSION_REFERENCE, data.substring(variableStart, position), variableStart))
                    }
                    startPosition = position - 1
                }
                else -> position++
            }
        }

        if (position == data.length) {
            return LexingError.UnclosedString(startPosition)
        }

        if (position > startPosition + 1) {
            tokens.add(Token(TokenKind.STRING_TEXT, data.substring(startPosition + 1, position), startPosition + 1))
        }

        tokens.add(Token(TokenKind.STRING_END, "\"", position))
        position++
        return null
    }
}