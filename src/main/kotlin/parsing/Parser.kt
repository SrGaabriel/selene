package me.gabriel.gwydion.parsing

import me.gabriel.gwydion.exception.ParsingError
import me.gabriel.gwydion.lexing.TYPE_TOKENS
import me.gabriel.gwydion.lexing.Token
import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.lexing.TokenStream
import me.gabriel.gwydion.util.Either

class Parser(private val tokens: TokenStream) {
    private var currentPosition: Int = 0

    fun parse(): Either<ParsingError, SyntaxTree> {
        val tree = SyntaxTree()
        return parseStatementSequence().mapRight { statements ->
            tree.addAllNodes(statements)
            tree
        }
    }

    private fun parseStatementSequence(): Either<ParsingError, List<SyntaxTreeNode>> {
        val statements = mutableListOf<SyntaxTreeNode>()
        while (hasMoreTokens()) {
            when (val statementResult = parseStatement()) {
                is Either.Left -> return Either.Left(statementResult.value)
                is Either.Right -> {
                    statementResult.value?.let { statements.add(it) } ?: break
                }
            }
        }
        return Either.Right(statements)
    }

    private fun parseStatement(): Either<ParsingError, SyntaxTreeNode?> {
        return when (peekToken().kind) {
            TokenKind.RETURN -> parseReturnStatement().mapInto()
            TokenKind.FUNCTION -> parseFunctionDefinition().mapInto()
            TokenKind.IF -> parseIfStatement().mapInto()
            TokenKind.DATA -> parseDataStructure().mapInto()
            TokenKind.TRAIT -> parseTrait().mapInto()
            TokenKind.MAKE -> parseTraitImplementation().mapInto()
            TokenKind.CLOSING_BRACES -> Either.Right(null)
            TokenKind.INTRINSIC -> parseIntrinsicFunction().mapInto()
            TokenKind.FOR -> parseForLoop().mapInto()
            TokenKind.MUT -> parseMutableDeclaration().mapInto()
            TokenKind.IDENTIFIER, TokenKind.SELF -> parseIdentifierStatement().mapInto()
            else -> Either.Left(ParsingError.UnexpectedToken(peekToken()))
        }
    }

    private fun parseBlock(): Either<ParsingError, BlockNode> {
        val openingBrace = consumeToken(TokenKind.OPENING_BRACES)
        if (openingBrace is Either.Left) return Either.Left(openingBrace.value)
        val statements = parseStatementSequence()
        val closingBrace = consumeToken(TokenKind.CLOSING_BRACES)

        return statements.flatMapRight { stmts ->
            closingBrace.mapRight {
                BlockNode(children = stmts, mark = openingBrace.unwrap())
            }
        }
    }

    private fun parseReturnStatement(): Either<ParsingError, ReturnNode> {
        val returnToken = consumeToken()
        return parseExpression().flatMapRight { expr ->
            consumeToken(TokenKind.SEMICOLON).mapRight { ReturnNode(expression = expr, mark = returnToken) }
        }
    }

    private fun parseFunctionDefinition(): Either<ParsingError, FunctionNode> {
        val functionToken = consumeToken(TokenKind.FUNCTION)
        if (functionToken is Either.Left) return Either.Left(functionToken.value)
        return parseIdentifier().flatMapRight { name ->
            parseParameters().flatMapRight { params ->
                parseReturnType().flatMapRight { returnType ->
                    parseBlock().mapRight { block ->
                        FunctionNode(
                            name = name.value,
                            parameters = params,
                            returnType = returnType,
                            body = block,
                            modifiers = mutableListOf(),
                            mark = functionToken.unwrap()
                        )
                    }
                }
            }
        }
    }

    private fun parseIfStatement(): Either<ParsingError, IfNode> {
        val ifToken = consumeToken(TokenKind.IF)
        if (ifToken is Either.Left) return Either.Left(ifToken.value)
        return parseExpression().flatMapRight { condition ->
            parseBlock().flatMapRight { thenBlock ->
                parseElseBlock().mapRight { elseBlock ->
                    IfNode(condition = condition, body = thenBlock, elseBody = elseBlock, mark = ifToken.unwrap())
                }
            }
        }
    }

    private fun parseElseBlock(): Either<ParsingError, BlockNode?> {
        return if (peekToken().kind == TokenKind.ELSE) {
            consumeToken()
            parseBlock().mapInto()
        } else {
            Either.Right(null)
        }
    }

    private fun parseDataStructure(): Either<ParsingError, DataStructureNode> {
        val dataToken = consumeToken()
        return parseIdentifier().flatMapRight { name ->
            parseDataFields().mapRight { fields ->
                consumeTokenIfPresent(TokenKind.SEMICOLON)
                DataStructureNode(name = name.value, fields = fields, mark = dataToken)
            }
        }
    }

    private fun parseDataFields(): Either<ParsingError, List<DataFieldNode>> {
        return consumeToken(TokenKind.OPENING_PARENTHESES).flatMapRight {
            parseCommaSeparatedList(::parseDataField, TokenKind.CLOSING_PARENTHESES)
        }
    }

    private fun parseDataField(): Either<ParsingError, DataFieldNode> {
        return parseIdentifier().flatMapRight { name ->
            consumeToken(TokenKind.TYPE_DECLARATION).flatMapRight {
                parseType().mapRight { type ->
                    DataFieldNode(name = name.value, type = type, mark = name)
                }
            }
        }
    }

    private fun parseTrait(): Either<ParsingError, TraitNode> {
        val traitToken = consumeToken(TokenKind.TRAIT)
        if (traitToken is Either.Left) return Either.Left(traitToken.value)
        return parseIdentifier().flatMapRight { name ->
            parseTraitFunctions().mapRight { functions ->
                TraitNode(name = name.value, functions = functions, mark = traitToken.unwrap())
            }
        }
    }

    private fun parseTraitFunctions(): Either<ParsingError, List<TraitFunctionNode>> {
        return consumeToken(TokenKind.OPENING_PARENTHESES).flatMapRight {
            parseCommaSeparatedList(::parseTraitFunction, TokenKind.CLOSING_PARENTHESES)
        }
    }

    private fun parseTraitFunction(): Either<ParsingError, TraitFunctionNode> {
        return parseIdentifier().flatMapRight { name ->
            parseParameters().flatMapRight { params ->
                parseReturnType().mapRight { returnType ->
                    TraitFunctionNode(name = name.value, parameters = params, returnType = returnType, mark = name)
                }
            }
        }
    }

    private fun parseTraitImplementation(): Either<ParsingError, TraitImplNode> {
        val make = consumeToken(TokenKind.MAKE)
        if (make is Either.Left) return Either.Left(make.value)
        return parseType().flatMapRight { type ->
            consumeToken(TokenKind.INTO).flatMapRight {
                parseIdentifier().flatMapRight { trait ->
                    parseBlock().mapRight { block ->
                        val functions = block.getChildren().filterIsInstance<FunctionNode>()
                        TraitImplNode(type = type, trait = trait.value, functions = functions, mark = make.unwrap())
                    }
                }
            }
        }
    }

    private fun parseIntrinsicFunction(): Either<ParsingError, FunctionNode> {
        consumeToken() // Consume 'intrinsic'
        return parseFunctionDefinition().mapRight { function ->
            function.modifiers.add(Modifiers.INTRINSIC)
            function
        }
    }

    private fun parseForLoop(): Either<ParsingError, ForNode> {
        val forToken = consumeToken()
        return parseIdentifier().flatMapRight { variable ->
            consumeToken(TokenKind.IN).flatMapRight {
                parseNumericExpression().flatMapRight { initial ->
                    consumeToken(TokenKind.RANGE).flatMapRight { range ->
                        parseNumericExpression().flatMapRight { end ->
                            parseBlock().mapRight { block ->
                                ForNode(
                                    variable = variable.value,
                                    iterable = RangeNode(initial, end, range),
                                    body = block,
                                    mark = forToken
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun parseMutableDeclaration(): Either<ParsingError, AssignmentNode> {
        val mutToken = consumeToken()
        return parseIdentifier().flatMapRight { name ->
            consumeToken(TokenKind.DECLARATION).flatMapRight {
                parseExpression().flatMapRight { expr ->
                    consumeToken(TokenKind.SEMICOLON).mapRight {
                        AssignmentNode(name.value, expr, true, Type.Unknown, mutToken)
                    }
                }
            }
        }
    }

    private fun parseIdentifierStatement(): Either<ParsingError, SyntaxTreeNode> {
        val identifierToken = consumeToken()
        return when (peekToken().kind) {
            TokenKind.DECLARATION -> parseDeclaration(identifierToken).mapInto()
            TokenKind.PLUS_ASSIGN, TokenKind.MINUS_ASSIGN, TokenKind.TIMES_ASSIGN, TokenKind.DIVIDE_ASSIGN ->
                parseCompoundAssignment(identifierToken).mapInto()

            TokenKind.OPENING_PARENTHESES -> parseCallExpression(identifierToken).mapInto()
            TokenKind.DOT -> parseAccessOrCall(identifierToken)
            else -> Either.Left(ParsingError.UnexpectedIdentifier(identifierToken))
        }
    }

    private fun parseDeclaration(identifierToken: Token): Either<ParsingError, AssignmentNode> {
        consumeToken() // Consume declaration token
        return parseExpression().flatMapRight { expr ->
            consumeToken(TokenKind.SEMICOLON).mapRight {
                AssignmentNode(identifierToken.value, expr, false, Type.Unknown, identifierToken)
            }
        }
    }

    private fun parseCompoundAssignment(identifierToken: Token): Either<ParsingError, CompoundAssignmentNode> {
        val operatorToken = consumeOneOf(*ValidCompoundAssignmentOperators)
        if (operatorToken is Either.Left) return Either.Left(operatorToken.value)
        return parseExpression().flatMapRight { expr ->
            consumeToken(TokenKind.SEMICOLON).mapRight {
                CompoundAssignmentNode(identifierToken.value, operatorToken.unwrap(), expr)
            }
        }
    }

    private fun parseCallExpression(identifierToken: Token): Either<ParsingError, CallNode> {
        return parseCallParameters().flatMapRight { params ->
            consumeToken(TokenKind.SEMICOLON).mapRight {
                CallNode(identifierToken.value, params, identifierToken)
            }
        }
    }

    private fun parseAccessOrCall(identifierToken: Token): Either<ParsingError, SyntaxTreeNode> {
        consumeToken(TokenKind.DOT)
        val member = consumeToken(TokenKind.IDENTIFIER)
        if (member is Either.Left) return Either.Left(member.value)
        val memberToken = member.unwrap()
        return when (peekToken().kind) {
            TokenKind.MUTATION -> parseMutation(identifierToken.value, memberToken).mapInto()
            TokenKind.OPENING_PARENTHESES -> parseTraitFunctionCall(
                identifierToken.value,
                memberToken
            ).mapInto()
            else -> Either.Right(StructAccessNode(identifierToken.value, memberToken.value, identifierToken))
        }
    }

    private fun parseMutation(struct: String, fieldToken: Token): Either<ParsingError, MutationNode> {
        val mutationToken = consumeToken()
        return parseExpression().flatMapRight { expr ->
            consumeToken(TokenKind.SEMICOLON).mapRight {
                MutationNode(struct, fieldToken.value, expr, mutationToken)
            }
        }
    }

    private fun parseTraitFunctionCall(
        trait: String,
        functionToken: Token
    ): Either<ParsingError, TraitFunctionCallNode> {
        return parseCallParameters().flatMapRight { params ->
            consumeToken(TokenKind.SEMICOLON).mapRight {
                TraitFunctionCallNode(trait, functionToken.value, params, functionToken)
            }
        }
    }

    private fun parseExpression(ignoreEquals: Boolean = false): Either<ParsingError, SyntaxTreeNode> {
        val left = parseNumericExpression()
        val current = peekToken()
        return if (!ignoreEquals && current.kind == TokenKind.EQUALS) {
            left.flatMapRight { leftExpr ->
                consumeToken()
                parseExpression().mapRight { rightExpr ->
                    EqualsNode(leftExpr, rightExpr, current)
                }
            }
        } else {
            left
        }
    }

    private fun parseNumericExpression(): Either<ParsingError, SyntaxTreeNode> {
        var left = parseTerm()
        while (peekToken().kind in listOf(TokenKind.PLUS, TokenKind.MINUS)) {
            val operator = consumeOneOf(TokenKind.PLUS, TokenKind.MINUS)
            if (operator is Either.Left) return Either.Left(operator.value)
            val right = parseTerm()
            left = left.flatMapRight { l ->
                right.mapRight { r ->
                    BinaryOperatorNode(l, operator.unwrap(), r)
                }
            }
        }
        return left
    }

    private fun parseTerm(): Either<ParsingError, SyntaxTreeNode> {
        var left = parseFactor()
        while (peekToken().kind in listOf(TokenKind.TIMES, TokenKind.DIVIDE)) {
            val operator = consumeOneOf(TokenKind.TIMES, TokenKind.DIVIDE)
            if (operator is Either.Left) return Either.Left(operator.value)
            val right = parseFactor()
            left = left.flatMapRight { l ->
                right.mapRight { r ->
                    BinaryOperatorNode(l, operator.unwrap(), r)
                }
            }
        }
        return left
    }

    private fun parseFactor(): Either<ParsingError, SyntaxTreeNode> {
        return when (peekToken().kind) {
            TokenKind.OPENING_PARENTHESES -> parseParenthesizedExpression()
            TokenKind.IDENTIFIER, TokenKind.SELF -> parseIdentifierExpression()
            TokenKind.NUMBER -> parseNumberLiteral().mapInto()
            TokenKind.STRING_START -> parseStringExpression().mapInto()
            TokenKind.BOOL_TYPE -> parseBooleanLiteral().mapInto()
            TokenKind.OPENING_BRACKETS, TokenKind.TIMES -> parseArray().mapInto()
            TokenKind.INSTANTIATION -> parseInstantiation().mapInto()
            else -> Either.Left(ParsingError.UnexpectedToken(peekToken()))
        }
    }

    private fun parseParenthesizedExpression(): Either<ParsingError, SyntaxTreeNode> {
        val openingToken = consumeToken(TokenKind.OPENING_PARENTHESES)
        if (openingToken is Either.Left) return Either.Left(openingToken.value)
        return parseExpression().flatMapRight { expr ->
            consumeToken(TokenKind.CLOSING_PARENTHESES).mapRight { expr }
        }
    }

    private fun parseIdentifierExpression(): Either<ParsingError, SyntaxTreeNode> {
        val identifier = consumeOneOf(TokenKind.IDENTIFIER, TokenKind.SELF)
        if (identifier is Either.Left) return Either.Left(identifier.value)
        val identifierToken = identifier.unwrap()
        return when (peekToken().kind) {
            TokenKind.OPENING_PARENTHESES -> parseCallParameters().mapRight { CallNode(identifierToken.value, it, identifierToken) }
            TokenKind.DOT -> parseStructAccess(identifierToken)
            TokenKind.OPENING_BRACKETS -> parseArrayAccess(identifierToken).mapInto()
            else -> Either.Right(VariableReferenceNode(identifierToken.value, identifierToken))
        }
    }

    private fun parseStructAccess(structToken: Token): Either<ParsingError, SyntaxTreeNode> {
        val dot = consumeToken(TokenKind.DOT)
        if (dot is Either.Left) return Either.Left(dot.value)
        return parseIdentifier().flatMapRight { field ->
            if (peekToken().kind == TokenKind.OPENING_PARENTHESES) {
                parseCallParameters().mapRight { params ->
                    TraitFunctionCallNode(structToken.value, field.value, params, dot.unwrap())
                }
            } else {
                Either.Right(StructAccessNode(structToken.value, field.value, dot.unwrap()))
            }
        }
    }

    private fun parseArrayAccess(arrayToken: Token): Either<ParsingError, ArrayAccessNode> {
        val openingBracket = consumeToken(TokenKind.OPENING_BRACKETS)
        if (openingBracket is Either.Left) return Either.Left(openingBracket.value)
        return parseExpression().flatMapRight { index ->
            consumeToken(TokenKind.CLOSING_BRACKETS).mapRight {
                ArrayAccessNode(arrayToken.value, index, openingBracket.unwrap())
            }
        }
    }

    private fun parseNumberLiteral(): Either<ParsingError, NumberNode> {
        val number = consumeToken(TokenKind.NUMBER)
        if (number is Either.Left) return Either.Left(number.value)
        val token = number.unwrap()
        return Either.Right(NumberNode(token.value, false, Type.Int32, token))
    }

    private fun parseStringExpression(): Either<ParsingError, StringNode> {
        val startToken = consumeToken()
        val segments = mutableListOf<StringNode.Segment>()
        while (peekToken().kind != TokenKind.STRING_END) {
            when (peekToken().kind) {
                TokenKind.STRING_TEXT -> segments.add(StringNode.Segment.Text(consumeToken().value))
                TokenKind.STRING_EXPRESSION_REFERENCE -> {
                    val refToken = consumeToken()
                    segments.add(StringNode.Segment.Reference(VariableReferenceNode(refToken.value, refToken)))
                }

                else -> return Either.Left(ParsingError.UnexpectedToken(peekToken()))
            }
        }
        consumeToken() // Consume STRING_END
        return Either.Right(StringNode("", segments, startToken))
    }

    private fun parseBooleanLiteral(): Either<ParsingError, BooleanNode> {
        val token = consumeToken()
        return Either.Right(BooleanNode(token.value.toBoolean(), token))
    }

    private fun parseArray(): Either<ParsingError, ArrayNode> {
        val isDynamic = peekToken().kind == TokenKind.TIMES
        if (isDynamic) consumeToken()
        val openingToken = consumeToken(TokenKind.OPENING_BRACKETS)
        if (openingToken is Either.Left) return Either.Left(openingToken.value)
        val elements = parseCommaSeparatedList(::parseExpression, TokenKind.CLOSING_BRACKETS)
        return elements.mapRight { ArrayNode(it, isDynamic, openingToken.unwrap()) }
    }

    private fun parseInstantiation(): Either<ParsingError, InstantiationNode> {
        val instantiationToken = consumeToken(TokenKind.INSTANTIATION)
        if (instantiationToken is Either.Left) return Either.Left(instantiationToken.value)
        return parseIdentifier().flatMapRight { name ->
            parseCallParameters().mapRight { args ->
                InstantiationNode(name.value, args, instantiationToken.unwrap())
            }
        }
    }

    private fun parseType(): Either<ParsingError, Type> {
        val isMutable = consumeTokenIfPresent(TokenKind.MUT) != null
        val baseType = parseBaseType() ?: return Either.Left(ParsingError.UnexpectedToken(peekToken()))

        return if (peekToken().kind == TokenKind.OPENING_BRACKETS) {
            parseArrayType(baseType, isMutable)
        } else {
            Either.Right(if (isMutable) Type.Mutable(baseType) else baseType)
        }
    }

    private fun parseBaseType(): Type? {
        val token = peekToken()
        return if (token.kind in TYPE_TOKENS) {
            consumeToken()
            tokenKindToType(token, false)
        } else {
            null
        }
    }

    private fun parseArrayType(baseType: Type, isMutable: Boolean): Either<ParsingError, Type> {
        consumeToken() // Consume opening bracket
        return when (peekToken().kind) {
            TokenKind.NUMBER -> {
                val size = consumeToken().value.toInt()
                consumeToken(TokenKind.CLOSING_BRACKETS).mapRight {
                    if (isMutable) Type.Mutable(Type.FixedArray(baseType, size)) else Type.FixedArray(baseType, size)
                }
            }

            TokenKind.TIMES -> {
                consumeToken()
                consumeToken(TokenKind.CLOSING_BRACKETS).mapRight {
                    if (isMutable) Type.Mutable(Type.DynamicArray(baseType)) else Type.DynamicArray(baseType)
                }
            }

            else -> Either.Left(ParsingError.UnexpectedToken(peekToken()))
        }
    }

    private fun parseParameters(): Either<ParsingError, List<ParameterNode>> {
        return consumeToken(TokenKind.OPENING_PARENTHESES).flatMapRight {
            parseCommaSeparatedList(::parseParameter, TokenKind.CLOSING_PARENTHESES)
        }
    }

    private fun parseParameter(): Either<ParsingError, ParameterNode> {
        return when {
            peekToken().kind == TokenKind.SELF -> {
                val token = consumeToken()
                Either.Right(ParameterNode("self", Type.Self, token))
            }

            peekToken().kind == TokenKind.MUT && peekNextToken().kind == TokenKind.SELF -> {
                consumeToken() // Consume MUT
                val token = consumeToken() // Consume SELF
                Either.Right(ParameterNode("self", Type.Mutable(Type.Self), token))
            }

            else -> parseIdentifier().flatMapRight { name ->
                consumeToken(TokenKind.TYPE_DECLARATION).flatMapRight { declaration ->
                    parseType().mapRight { type ->
                        ParameterNode(name.value, type, declaration)
                    }
                }
            }
        }
    }

    private fun parseCallParameters(): Either<ParsingError, List<SyntaxTreeNode>> {
        return consumeToken(TokenKind.OPENING_PARENTHESES).flatMapRight {
            parseCommaSeparatedList(::parseExpression, TokenKind.CLOSING_PARENTHESES)
        }
    }

    private fun parseReturnType(): Either<ParsingError, Type> {
        return if (peekToken().kind == TokenKind.RETURN_TYPE_DECLARATION) {
            consumeToken()
            parseType()
        } else {
            Either.Right(Type.Void)
        }
    }

    private fun <T> parseCommaSeparatedList(
        parseElement: () -> Either<ParsingError, T>,
        endToken: TokenKind
    ): Either<ParsingError, List<T>> {
        val elements = mutableListOf<T>()
        while (peekToken().kind != endToken) {
            when (val result = parseElement()) {
                is Either.Left -> return Either.Left(result.value)
                is Either.Right -> elements.add(result.value)
            }
            if (peekToken().kind == TokenKind.COMMA) {
                consumeToken()
            } else if (peekToken().kind != endToken) {
                return Either.Left(ParsingError.UnexpectedToken(peekToken()))
            }
        }
        consumeToken()
        return Either.Right(elements)
    }

    private fun parseIdentifier(): Either<ParsingError, Token> {
        val token = peekToken()
        return if (token.kind == TokenKind.IDENTIFIER) {
            consumeToken()
            Either.Right(token)
        } else {
            Either.Left(ParsingError.UnexpectedToken(token))
        }
    }

    private fun hasMoreTokens(): Boolean = currentPosition < tokens.count()

    private fun peekToken(): Token = if (hasMoreTokens()) tokens[currentPosition] else tokens.last()

    private fun peekNextToken(): Token = if (currentPosition + 1 < tokens.count()) tokens[currentPosition + 1] else tokens.last()

    private fun consumeToken(): Token = tokens[currentPosition++]

    private fun consumeToken(expected: TokenKind): Either<ParsingError, Token> {
        val token = peekToken()
        return if (token.kind == expected) {
            currentPosition++
            Either.Right(token)
        } else {
            Either.Left(ParsingError.IncorrectToken(token, expected))
        }
    }

    private fun consumeOneOf(vararg expected: TokenKind): Either<ParsingError, Token> {
        val token = peekToken()
        return if (token.kind in expected) {
            currentPosition++
            Either.Right(token)
        } else {
            Either.Left(ParsingError.IncorrectToken(token, expected.first()))
        }
    }

    private fun consumeTokenIfPresent(expected: TokenKind): Token? {
        return if (peekToken().kind == expected) {
            consumeToken()
        } else {
            null
        }
    }

    private fun tokenKindToType(token: Token, mutable: Boolean): Type {
        val base = when (token.kind) {
            TokenKind.ANY_TYPE -> Type.Any
            TokenKind.INT8_TYPE -> Type.Int8
            TokenKind.INT16_TYPE -> Type.Int16
            TokenKind.INT32_TYPE -> Type.Int32
            TokenKind.INT64_TYPE -> Type.Int64
            TokenKind.UINT8_TYPE -> Type.UInt8
            TokenKind.UINT16_TYPE -> Type.UInt16
            TokenKind.UINT32_TYPE -> Type.UInt32
            TokenKind.UINT64_TYPE -> Type.UInt64
            TokenKind.FLOAT32_TYPE -> Type.Float32
            TokenKind.FLOAT64_TYPE -> Type.Float64
            TokenKind.BOOL_TYPE -> Type.Boolean
            TokenKind.STRING_TYPE -> Type.String
            TokenKind.IDENTIFIER -> Type.UnknownReference(token.value, mutable)
            else -> throw IllegalArgumentException("Unexpected token kind for type: ${token.kind}")
        }
        return if (mutable) Type.Mutable(base) else base
    }

    companion object {
        private val ValidBinaryOperators = arrayOf(
            TokenKind.PLUS,
            TokenKind.MINUS,
            TokenKind.TIMES,
            TokenKind.DIVIDE,
            TokenKind.EQUALS
        )

        private val ValidCompoundAssignmentOperators = arrayOf(
            TokenKind.PLUS_ASSIGN,
            TokenKind.MINUS_ASSIGN,
            TokenKind.TIMES_ASSIGN,
            TokenKind.DIVIDE_ASSIGN
        )
    }
}