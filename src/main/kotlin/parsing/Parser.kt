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
            val node = statement.getRight()
            if (node != null) {
                statements.add(node)
            } else {
                break
            }
        }
        return Either.Right(statements)
    }

    fun parseBlock(): Either<ParsingError, BlockNode> {
        consume(TokenKind.OPENING_BRACES).ifLeft {
            return Either.Left(it)
        }
        val statements = parseStatementSequence()
        if (statements.isLeft()) {
            return Either.Left(statements.getLeft())
        }
        consume(TokenKind.CLOSING_BRACES).ifLeft {
            return Either.Left(it)
        }
        return Either.Right(BlockNode(statements.getRight().toMutableList()))
    }

    fun parseStatement(): Either<ParsingError, SyntaxTreeNode?> {
        val token = tokens[position]
        return when (token.kind) {
            TokenKind.RETURN -> {
                position++
                val expression = parseExpression()
                if (expression.isLeft()) {
                    return Either.Left(expression.getLeft())
                }
                consume(TokenKind.SEMICOLON).ifLeft {
                    return Either.Left(it)
                }
                Either.Right(ReturnNode(expression.getRight()))
            }
            TokenKind.FUNCTION -> {
                val function = parseFunctionNode()
                if (function.isLeft()) {
                    return Either.Left(function.getLeft())
                }
                Either.Right(function.getRight())
            }
            TokenKind.CLOSING_BRACES -> {
                // We are probably reading the parent's closing braces
                // Don't know if this is the right way to handle this
                Either.Right(null)
            }
            TokenKind.INTRINSIC -> {
                position++
                val function = parseWholeFunction(listOf(TokenKind.INTRINSIC))
                if (function.isLeft()) {
                    return Either.Left(function.getLeft())
                }
                Either.Right(function.getRight())
            }
            TokenKind.IDENTIFIER -> {
                position++;
                val peek = peek()
                if (peek.kind == TokenKind.ASSIGN) {
                    position++
                    val expression = parseExpression()
                    if (expression.isLeft()) {
                        return Either.Left(expression.getLeft())
                    }
                    consume(TokenKind.SEMICOLON).ifLeft {
                        return Either.Left(it)
                    }
                    Either.Right(AssignmentNode(token.value, expression.getRight()))
                } else if (peek.kind == TokenKind.PLUS_ASSIGN || peek.kind == TokenKind.MINUS_ASSIGN || peek.kind == TokenKind.TIMES_ASSIGN || peek.kind == TokenKind.DIVIDE_ASSIGN) {
                    val compoundAssignment = parseCompoundAssignment(token.value)
                    if (compoundAssignment.isLeft()) {
                        return Either.Left(compoundAssignment.getLeft())
                    }
                    consume(TokenKind.SEMICOLON).ifLeft {
                        return Either.Left(it)
                    }
                    Either.Right(compoundAssignment.unwrap())
                } else if (peek.kind == TokenKind.OPENING_PARENTHESES) {
                    val parameters = parseCallParameters()
                    if (parameters.isLeft()) {
                        return Either.Left(parameters.getLeft())
                    }
                    consume(TokenKind.SEMICOLON).ifLeft {
                        return Either.Left(it)
                    }
                    Either.Right(CallNode(token.value, parameters.unwrap()))
                } else {
                    Either.Left(ParsingError.UnexpectedIdentifier(token))
                }
            }
            else -> Either.Left(ParsingError.UnexpectedToken(token))
        }
    }

    fun parseWholeFunction(modifiers: List<TokenKind>): Either<ParsingError, FunctionNode> {
        val next = peek()
        return when (next.kind) {
            TokenKind.FUNCTION -> {
                val function = parseFunctionNode()
                if (function.isLeft()) {
                    return Either.Left(function.getLeft())
                }
                function.getRight().modifiers.addAll(modifiers)
                Either.Right(function.getRight())
            }
            else -> Either.Left(ParsingError.InvalidModifier(next))
        }
    }

    fun parseFunctionNode(): Either<ParsingError, FunctionNode> {
        position++
        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        val parameters = parseParameters()
        if (parameters.isLeft()) {
            return Either.Left(parameters.getLeft())
        }
        val block = parseBlock()
        if (block.isLeft()) {
            return Either.Left(block.getLeft())
        }
        val function = FunctionNode(identifier.unwrap(), parameters.unwrap(), block.unwrap(), mutableListOf())
        return Either.Right(function)
    }

    fun parseCompoundAssignment(token: String): Either<ParsingError, SyntaxTreeNode> {
        val operation = peek().kind
        if (operation != TokenKind.PLUS_ASSIGN && operation != TokenKind.MINUS_ASSIGN && operation != TokenKind.TIMES_ASSIGN && operation != TokenKind.DIVIDE_ASSIGN) {
            return Either.Left(ParsingError.UnexpectedToken(peek()))
        }
        position++
        val expression = parseExpression()
        if (expression.isLeft()) {
            return Either.Left(expression.getLeft())
        }

        return Either.Right(CompoundAssignmentNode(token, operation, expression.getRight()))
    }

    fun parseParameters(): Either<ParsingError, ParametersNode> {
        consume(TokenKind.OPENING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        val parameters = mutableListOf<ParameterNode>()
        while (peek().kind != TokenKind.CLOSING_PARENTHESES) {
            val parameter = parseParameter()
            if (parameter.isLeft()) {
                return Either.Left(parameter.getLeft())
            }
            parameters.add(parameter.getRight())
            if (peek().kind == TokenKind.COMMA) {
                position++
            }
            if (position >= tokens.count()) {
                throw IllegalArgumentException("Unexpected end of file")
            }
        }
        consume(TokenKind.CLOSING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        return Either.Right(ParametersNode(parameters))
    }

    fun parseParameter(): Either<ParsingError, ParameterNode> {
        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        return Either.Right(ParameterNode(identifier.unwrap()))
    }

    fun parseIdentifier(): Either<ParsingError, String> {
        val token = tokens[position]
        if (token.kind == TokenKind.IDENTIFIER) {
            position++
            return Either.Right(token.value)
        }
        return Either.Left(ParsingError.UnexpectedToken(token))
    }

    fun parseExpression(): Either<ParsingError, SyntaxTreeNode> {
        val token = tokens[position]
        println('-')
        println(tokens[position-1])
        println(token)
        println(tokens[position+1])
        println('-')
        return when (token.kind) {
            TokenKind.NUMBER -> {
                parseNumericExpression()
            }
            TokenKind.IDENTIFIER -> {
                if (peekNext().kind == TokenKind.OPENING_PARENTHESES) {
                    position ++
                    val parameters = parseCallParameters()
                    if (parameters.isLeft()) {
                        return Either.Left(parameters.getLeft())
                    }
                    Either.Right(CallNode(token.value, parameters.unwrap()))
                } else {
                    parseNumericExpression()
                }
            }
            else -> Either.Left(ParsingError.UnexpectedToken(token))
        }
    }

    fun parseCallParameters(): Either<ParsingError, CallParametersNode> {
        consume(TokenKind.OPENING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        val parameters = mutableListOf<SyntaxTreeNode>()
        while (peek().kind != TokenKind.CLOSING_PARENTHESES) {
            val expression = parseExpression()
            if (expression.isLeft()) {
                return Either.Left(expression.getLeft())
            }
            parameters.add(expression.getRight())
            if (peek().kind == TokenKind.COMMA) {
                position++
            }
            if (position >= tokens.count()) {
                throw IllegalArgumentException("Unexpected end of file")
            }
        }
        consume(TokenKind.CLOSING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        return Either.Right(CallParametersNode(parameters))
    }

    fun parseNumericExpression(): Either<ParsingError, SyntaxTreeNode> {
        var left = parseTerm()
        if (left.isLeft()) {
            return Either.Left(left.getLeft())
        }

        while (peek().kind == TokenKind.PLUS || peek().kind == TokenKind.MINUS) {
            val operator = peek().kind
            position++
            val right = parseTerm()
            if (right.isLeft()) {
                return Either.Left(right.getLeft())
            }
            left = Either.Right(BinaryOperatorNode(left.unwrap(), operator, right.unwrap()))
        }

        return left
    }

    fun parseTerm(): Either<ParsingError, SyntaxTreeNode> {
        var left = parseFactor()
        if (left.isLeft()) {
            return Either.Left(left.getLeft())
        }

        while (peek().kind == TokenKind.TIMES || peek().kind == TokenKind.DIVIDE) {
            val operator = peek().kind
            position++
            val right = parseFactor()
            if (right.isLeft()) {
                return Either.Left(right.getLeft())
            }
            left = Either.Right(BinaryOperatorNode(left.unwrap(), operator, right.unwrap()))
        }

        return left
    }

    fun parseFactor(): Either<ParsingError, SyntaxTreeNode> {
        return when (tokens[position].kind) {
            TokenKind.OPENING_PARENTHESES -> {
                position++
                val node = parseExpression()
                consume(TokenKind.CLOSING_PARENTHESES).ifLeft {
                    return Either.Left(it)
                }
                node
            }
            TokenKind.NUMBER -> Either.Right(NumberNode(consume().value.toInt()))
            TokenKind.IDENTIFIER -> Either.Right(VariableNode(consume().value))
            else -> throw IllegalArgumentException("Unexpected token: ${peek()}")
        }
    }

    private fun peek(): Token = if (position < tokens.count()) tokens[position] else tokens.last()

    private fun peekNext(): Token = if (position + 1 < tokens.count()) tokens[position + 1] else tokens.last()

    private fun consume(): Token = tokens[position++]

    private fun consume(expected: TokenKind): Either<ParsingError, Token> {
        val token = peek()
        if (token.kind == expected) {
            position++
        } else {
            return Either.left(ParsingError.IncorrectToken(token, expected))
        }
        return Either.right(token)
    }
}