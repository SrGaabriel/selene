package me.gabriel.gwydion.parsing

import me.gabriel.gwydion.exception.ParsingError
import me.gabriel.gwydion.lexing.Token
import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.lexing.TokenStream
import me.gabriel.gwydion.util.Either

class Parser(private val tokens: TokenStream) {
    private var position: Int = 0

    fun parse(): Either<ParsingError, SyntaxTree> {
        val tree = SyntaxTree()
        val statements = parseStatementSequence()
        if (statements.isLeft()) {
            return Either.Left(statements.getLeft())
        }
        tree.addAllNodes(statements.getRight())
        return Either.Right(tree)
    }

    fun parseStatementSequence(): Either<ParsingError, List<SyntaxTreeNode>> {
        val statements = mutableListOf<SyntaxTreeNode>()
        while (position < tokens.count()) {
            val statement = parseStatement()
            if (statement.isLeft()) {
                return Either.Left(statement.getLeft())
            }
            statements.add(statement.getRight())
        }
        return Either.Right(statements)
    }

    fun parseStatement(): Either<ParsingError, SyntaxTreeNode> {
        val token = tokens[position]
        return when (token.kind) {
            TokenKind.RETURN -> {
                position++
                val expression = parseExpression()
                if (expression.isLeft()) {
                    return Either.Left(expression.getLeft())
                }
                consume(TokenKind.SEMICOLON)
                Either.Right(ReturnNode(expression.getRight()))
            }
            else -> Either.Left(ParsingError.UnexpectedToken(token.value))
        }
    }

    fun parseExpression(): Either<ParsingError, SyntaxTreeNode> {
        val token = tokens[position]
        return when (token.kind) {
            TokenKind.NUMBER -> {
                position++
                val left = NumberNode(token.value.toInt())
                if (peek().kind == TokenKind.PLUS) {
                    position++
                    val right = parseFactor()
                    if (right.isLeft()) {
                        return Either.Left(right.getLeft())
                    }
                    Either.Right(BinaryOperatorNode(left, TokenKind.PLUS, right.getRight()))
                } else {
                    Either.Right(left)
                }
            }
            else -> Either.Left(ParsingError.UnexpectedToken(token.value))
        }
    }

    fun parseFactor(): Either<ParsingError, SyntaxTreeNode> {
        return when (tokens[position].kind) {
            TokenKind.OPENING_PARENTHESES -> {
                position++
                val node = parseExpression()
                consume(TokenKind.CLOSING_PARENTHESES)
                node
            }
            TokenKind.NUMBER -> Either.Right(NumberNode(consume().value.toInt()))
            else -> throw IllegalArgumentException("Unexpected token: ${peek()}")
        }
    }

    private fun peek(): Token = if (position < tokens.count()) tokens[position] else tokens.last()

    private fun consume(): Token = tokens[position++]

    private fun consume(expected: TokenKind) {
        if (peek().kind == expected) {
            position++
        } else {
            throw IllegalArgumentException("Expected $expected, found ${peek().kind}")
        }
    }
}