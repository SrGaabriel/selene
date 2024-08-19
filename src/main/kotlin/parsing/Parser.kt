package me.gabriel.gwydion.parsing

import me.gabriel.gwydion.exception.ParsingError
import me.gabriel.gwydion.lexing.TYPE_TOKENS
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
        val openingToken = consume(TokenKind.OPENING_BRACES).ifLeft {
            return Either.Left(it)
        }.unwrap()
        val statements = parseStatementSequence()
        if (statements.isLeft()) {
            return Either.Left(statements.getLeft())
        }
        val closingToken = consume(TokenKind.CLOSING_BRACES).ifLeft {
            return Either.Left(it)
        }.unwrap()
        return Either.Right(BlockNode(
            children = statements.getRight(),
            start = openingToken,
            end = closingToken
        ))
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
                Either.Right(ReturnNode(
                    expression = expression.getRight(),
                    start = token
                ))
            }
            TokenKind.FUNCTION -> {
                val function = parseFunctionNode(token)
                if (function.isLeft()) {
                    return Either.Left(function.getLeft())
                }
                Either.Right(function.getRight())
            }
            TokenKind.IF -> {
                position++
                val condition = parseExpression()
                if (condition.isLeft()) {
                    return Either.Left(condition.getLeft())
                }
                val block = parseBlock()
                if (block.isLeft()) {
                    return Either.Left(block.getLeft())
                }

                val elseBlock = if (peek().kind == TokenKind.ELSE) {
                    position++
                    parseBlock().ifLeft {
                        return Either.Left(it)
                    }
                } else {
                    null
                }

                Either.Right(IfNode(
                    condition = condition.unwrap(),
                    body = block.unwrap(),
                    elseBody = elseBlock?.unwrap(),
                    start = token
                ))
            }
            TokenKind.DATA -> {
                parseDataStructure().mapRight { it }
            }
            TokenKind.TRAIT -> {
                parseTrait().mapRight { it }
            }
            TokenKind.MAKE -> {
                parseTraitImplementation().mapRight { it }
            }
            TokenKind.CLOSING_BRACES -> {
                // We are probably reading the parent's closing braces
                // Don't know if this is the right way to handle this
                Either.Right(null)
            }
            TokenKind.INTRINSIC -> {
                position++
                val function = parseWholeFunction(listOf(Modifiers.INTRINSIC))
                if (function.isLeft()) {
                    return Either.Left(function.getLeft())
                }
                Either.Right(function.getRight())
            }
            TokenKind.MUT -> {
                position++
                if (peek().kind != TokenKind.IDENTIFIER) {
                    return Either.Left(ParsingError.UnexpectedToken(peek()))
                }
                val identifier = parseIdentifier()
                if (identifier.isLeft()) {
                    return Either.Left(identifier.getLeft())
                }
                if (peek().kind != TokenKind.DECLARATION) {
                    return Either.Left(ParsingError.UnexpectedToken(peek()))
                }
                position++
                val expression = parseExpression()
                if (expression.isLeft()) {
                    return Either.Left(expression.getLeft())
                }
                consume(TokenKind.SEMICOLON).ifLeft {
                    return Either.Left(it)
                }
                Either.Right(AssignmentNode(identifier.unwrap(), expression.getRight(), true, Type.Unknown, token))
            }
            TokenKind.IDENTIFIER -> {
                position++
                val peek = peek()
                // todo: improve this if-else-if-else-if-else shit
                if (peek.kind == TokenKind.DECLARATION) {
                    position++
                    val expression = parseExpression()
                    if (expression.isLeft()) {
                        return Either.Left(expression.getLeft())
                    }
                    consume(TokenKind.SEMICOLON).ifLeft {
                        return Either.Left(it)
                    }
                    Either.Right(AssignmentNode(token.value, expression.getRight(), false, Type.Unknown, token))
                } else if (peek.kind == TokenKind.PLUS_ASSIGN || peek.kind == TokenKind.MINUS_ASSIGN || peek.kind == TokenKind.TIMES_ASSIGN || peek.kind == TokenKind.DIVIDE_ASSIGN) {
                    val compoundAssignment = parseCompoundAssignment(token)
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
                } else if (peek.kind == TokenKind.DOT && peekNext().kind == TokenKind.IDENTIFIER) {
                    val following = tokens[position + 2]
                    return if (following.kind == TokenKind.MUTATION) {
                        val mutation = parseMutation(token.value)
                        if (mutation.isLeft()) {
                            return Either.Left(mutation.getLeft())
                        }
                        consume(TokenKind.SEMICOLON).ifLeft {
                            return Either.Left(it)
                        }
                        Either.Right(mutation.unwrap())
                    } else if (following.kind == TokenKind.OPENING_PARENTHESES) {
                        val call = parseTraitFunctionCall(token.value)
                        if (call.isLeft()) {
                            return Either.Left(call.getLeft())
                        }
                        consume(TokenKind.SEMICOLON).ifLeft {
                            return Either.Left(it)
                        }
                        Either.Right(call.unwrap())
                    } else {
                        Either.Left(ParsingError.UnexpectedToken(following))
                    }
                } else {
                    Either.Left(ParsingError.UnexpectedIdentifier(token))
                }
            }
            else -> Either.Left(ParsingError.UnexpectedToken(token))
        }
    }

    fun parseWholeFunction(modifiers: List<Modifiers>): Either<ParsingError, FunctionNode> {
        val next = peek()
        return when (next.kind) {
            TokenKind.FUNCTION -> {
                val function = parseFunctionNode(next)
                if (function.isLeft()) {
                    return Either.Left(function.getLeft())
                }
                function.getRight().modifiers.addAll(modifiers)
                Either.Right(function.getRight())
            }
            else -> Either.Left(ParsingError.InvalidModifier(next))
        }
    }

    fun parseFunctionNode(token: Token): Either<ParsingError, FunctionNode> {
        position++
        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        val parameters = parseParameters()
        if (parameters.isLeft()) {
            return Either.Left(parameters.getLeft())
        }

        val returnType = if (peek().kind != TokenKind.OPENING_BRACES) {
            consume(TokenKind.RETURN_TYPE_DECLARATION).ifLeft {
                return Either.Left(it)
            }

            parseTypeOrNull() ?: return Either.Left(ParsingError.UnexpectedToken(peek()))
        } else {
            Type.Void
        }
        val block = parseBlock()
        if (block.isLeft()) {
            return Either.Left(block.getLeft())
        }
        val function = FunctionNode(
            name = identifier.unwrap(),
            parameters = parameters.unwrap(),
            returnType = returnType,
            body = block.unwrap(),

            modifiers = mutableListOf()
        )
        return Either.Right(function)
    }

    fun parseDataStructure(): Either<ParsingError, DataStructureNode> {
        val token = consume(TokenKind.DATA).unwrap()
        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        val fields = parseDataFields()
        if (fields.isLeft()) {
            return Either.Left(fields.getLeft())
        }
        if (peek().kind == TokenKind.SEMICOLON) {
            position++
        }
        return Either.Right(DataStructureNode(
            name = identifier.unwrap(),
            fields = fields.unwrap(),
            start = token,
            end = token
        ))
    }

    fun parseDataFields(): Either<ParsingError, List<DataFieldNode>> {
        consume(TokenKind.OPENING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        val fields = mutableListOf<DataFieldNode>()
        while (peek().kind != TokenKind.CLOSING_PARENTHESES) {
            val field = parseDataField()
            if (field.isLeft()) {
                return Either.Left(field.getLeft())
            }
            fields.add(field.getRight())
            if (peek().kind == TokenKind.COMMA) {
                position++
            }
        }
        consume(TokenKind.CLOSING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        return Either.Right(fields)
    }

    fun parseDataField(): Either<ParsingError, DataFieldNode> {
        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        val typeDeclaration = consume(TokenKind.TYPE_DECLARATION).ifLeft {
            return Either.Left(it)
        }
        val type = parseTypeOrNull() ?: return Either.Left(ParsingError.UnexpectedToken(peek()))
        return Either.Right(DataFieldNode(
            name = identifier.unwrap(),
            type = type,
            start = typeDeclaration.unwrap(),
            end = typeDeclaration.unwrap()
        ))
    }

    fun parseTrait(): Either<ParsingError, TraitNode> {
        val token = consume(TokenKind.TRAIT).unwrap()
        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        val functions = parseTraitFunctions()
        if (functions.isLeft()) {
            return Either.Left(functions.getLeft())
        }
        return Either.Right(TraitNode(
            name = identifier.unwrap(),
            functions = functions.unwrap(),
            start = token,
            end = token
        ))
    }

    fun parseTraitFunctions(): Either<ParsingError, List<TraitFunctionNode>> {
        consume(TokenKind.OPENING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        val functions = mutableListOf<TraitFunctionNode>()
        while (peek().kind != TokenKind.CLOSING_PARENTHESES) {
            val function = parseTraitFunction()
            if (function.isLeft()) {
                return Either.Left(function.getLeft())
            }
            functions.add(function.getRight())
            if (peek().kind == TokenKind.SEMICOLON) {
                position++
            }
        }
        consume(TokenKind.CLOSING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        return Either.Right(functions)
    }

    fun parseTraitFunction(): Either<ParsingError, TraitFunctionNode> {
        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        val parameters = parseParameters()
        if (parameters.isLeft()) {
            return Either.Left(parameters.getLeft())
        }
        val returnType = if (peek().kind != TokenKind.OPENING_BRACES) {
            consume(TokenKind.RETURN_TYPE_DECLARATION).ifLeft {
                return Either.Left(it)
            }
            parseTypeOrNull() ?: return Either.Left(ParsingError.UnexpectedToken(peek()))
        } else {
            Type.Void
        }
        return Either.Right(TraitFunctionNode(
            name = identifier.unwrap(),
            parameters = parameters.unwrap(),
            returnType = returnType,
        ))
    }

    fun parseTraitFunctionCall(trait: String): Either<ParsingError, TraitFunctionCallNode> {
        consume(TokenKind.DOT).ifLeft {
            return Either.Left(it)
        }
        val function = parseIdentifier()
        if (function.isLeft()) {
            return Either.Left(function.getLeft())
        }
        val parameters = parseCallParameters()
        if (parameters.isLeft()) {
            return Either.Left(parameters.getLeft())
        }
        return Either.Right(TraitFunctionCallNode(
            trait = trait,
            function = function.unwrap(),
            arguments = parameters.unwrap()
        ))
    }

    fun parseTraitImplementation(): Either<ParsingError, TraitImplNode> {
        val token = consume(TokenKind.MAKE).unwrap()
        val type = parseTypeOrNull()
            ?: return Either.Left(ParsingError.UnexpectedToken(peek()))
        consume(TokenKind.INTO).ifLeft {
            return Either.Left(it)
        }
        val trait = parseIdentifier()
        if (trait.isLeft()) {
            return Either.Left(trait.getLeft())
        }
        val functions = mutableListOf<FunctionNode>()
        consume(TokenKind.OPENING_BRACES).ifLeft {
            return Either.Left(it)
        }
        while (peek().kind != TokenKind.CLOSING_BRACES) {
            val function = parseWholeFunction(listOf())
            if (function.isLeft()) {
                return Either.Left(function.getLeft())
            }
            functions.add(function.getRight())
        }
        consume(TokenKind.CLOSING_BRACES).ifLeft {
            return Either.Left(it)
        }

        return Either.Right(TraitImplNode(
            type = type,
            trait = trait.unwrap(),
            functions = functions
        ))
    }

    fun parseTypeOrNull(): Type? {
        val mutable = peek().kind == TokenKind.MUT
        if (mutable) position++
        val next = peek()
        val base = if (next.kind in TYPE_TOKENS) {
            position++
            tokenKindToType(next, mutable)
        } else {
            return null
        }
        if (base !is Type.UnknownReference && mutable) {
            return null
        }
        if (peek().kind == TokenKind.OPENING_BRACKETS) {
            position++
            val type = when (peek().kind) {
                TokenKind.NUMBER -> {
                    val number = consume().value.toInt()
                    if (number < 0) {
                        return null
                    }
                    Type.FixedArray(base, number)
                }
                TokenKind.TIMES -> {
                    position++
                    Type.DynamicArray(base)
                }
                else -> return null
            }
            consume(TokenKind.CLOSING_BRACKETS).ifLeft {
                return null
            }
            return type
        }
        return base
    }

    fun parseCompoundAssignment(token: Token): Either<ParsingError, SyntaxTreeNode> {
        val operation = peek().kind
        if (operation != TokenKind.PLUS_ASSIGN && operation != TokenKind.MINUS_ASSIGN && operation != TokenKind.TIMES_ASSIGN && operation != TokenKind.DIVIDE_ASSIGN) {
            return Either.Left(ParsingError.UnexpectedToken(peek()))
        }
        position++
        val expression = parseExpression()
        if (expression.isLeft()) {
            return Either.Left(expression.getLeft())
        }

        return Either.Right(CompoundAssignmentNode(token.value, operation, expression.getRight(), token))
    }

    fun parseParameters(): Either<ParsingError, List<ParameterNode>> {
        consume(TokenKind.OPENING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        val parameters = mutableListOf<ParameterNode>()
        while (peek().kind != TokenKind.CLOSING_PARENTHESES) {
            val parameter = parseParameter(peek())
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
        return Either.Right(parameters)
    }

    fun parseParameter(token: Token): Either<ParsingError, ParameterNode> {
        if (token.kind == TokenKind.SELF) {
            position++
            return Either.Right(ParameterNode(
                name = "self",
                type = Type.Self,
                token = token
            ))
        }

        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        val next = peek()
        if (next.kind != TokenKind.TYPE_DECLARATION) {
            return Either.Left(ParsingError.ParameterMissingTypeDeclaration(tokens[position-1]))
        }
        position++
        val type = parseTypeOrNull() ?: return Either.Left(ParsingError.UnexpectedToken(peek()))

        return Either.Right(ParameterNode(
            name = identifier.unwrap(),
            type = type,
            token = token
        ))
    }

    fun parseIdentifier(): Either<ParsingError, String> {
        val token = peek()
        if (token.kind == TokenKind.IDENTIFIER) {
            position++
            return Either.Right(token.value)
        }
        return Either.Left(ParsingError.UnexpectedToken(token))
    }

    fun parseExpression(ignoreEquals: Boolean = false): Either<ParsingError, SyntaxTreeNode> {
        val token = tokens[position]
        if (!ignoreEquals && peekNext().kind == TokenKind.EQUALS) {
            val left = parseExpression(true)
            if (left.isLeft()) {
                return Either.Left(left.getLeft())
            }
            consume(TokenKind.EQUALS).ifLeft {
                return Either.Left(it)
            }
            val right = parseExpression()
            if (right.isLeft()) {
                return Either.Left(right.getLeft())
            }
            return Either.Right(EqualsNode(left.unwrap(), right.unwrap(), token))
        }
        return when (token.kind) {
            TokenKind.NUMBER -> {
                parseNumericExpression()
            }
            TokenKind.STRING_START -> {
                parseStringExpression()
            }
            TokenKind.BOOL_TYPE -> {
                position++
                Either.Right(BooleanNode(token.value.toBoolean(), token))
            }
            TokenKind.OPENING_BRACKETS -> {
                parseArray()
            }
            TokenKind.TIMES -> {
                if (peekNext().kind == TokenKind.OPENING_BRACKETS) {
                    parseArray()
                } else {
                    return Either.Left(ParsingError.UnexpectedToken(token))
                }
            }
            TokenKind.INSTANTIATION -> {
                position++
                val identifier = parseIdentifier()
                if (identifier.isLeft()) {
                    return Either.Left(identifier.getLeft())
                }
                val arguments = parseCallParameters()
                if (arguments.isLeft()) {
                    return Either.Left(arguments.getLeft())
                }
                Either.Right(InstantiationNode(
                    name = identifier.unwrap(),
                    arguments = arguments.unwrap()
                ))
            }
            TokenKind.IDENTIFIER, TokenKind.SELF -> {
                when (peekNext().kind) {
                    TokenKind.OPENING_PARENTHESES -> {
                        position++
                        val parameters = parseCallParameters()
                        if (parameters.isLeft()) {
                            return Either.Left(parameters.getLeft())
                        }
                        Either.Right(
                            CallNode(
                                name = token.value,
                                arguments = parameters.unwrap()
                            )
                        )
                    }
                    TokenKind.OPENING_BRACKETS -> {
                        parseArrayAccess()
                    }
                    TokenKind.DOT -> {
                        if (tokens[position + 3].kind == TokenKind.OPENING_PARENTHESES) {
                            position++
                            val call = parseTraitFunctionCall(token.value)
                            if (call.isLeft()) {
                                return Either.Left(call.getLeft())
                            }
                            return Either.Right(call.unwrap())
                        }
                        position += 2
                        val field = parseIdentifier()
                        if (field.isLeft()) {
                            return Either.Left(field.getLeft())
                        }
                        Either.Right(StructAccessNode(
                            struct = token.value,
                            field = field.unwrap(),
                        ))
                    }
                    else -> {
                        parseNumericExpression()
                    }
                }
            }
            else -> Either.Left(ParsingError.UnexpectedToken(token))
        }
    }

    fun parseStringExpression(): Either<ParsingError, SyntaxTreeNode> {
        var left = parseStringTerm()
        if (left.isLeft()) {
            return Either.Left(left.getLeft())
        }

        while (peek().kind == TokenKind.PLUS) {
            position++
            val right = parseStringTerm()
            if (right.isLeft()) {
                return Either.Left(right.getLeft())
            }
            left = Either.Right(BinaryOperatorNode(left.unwrap(), TokenKind.PLUS, right.unwrap()))
        }

        return left
    }

    fun parseStringTerm(): Either<ParsingError, SyntaxTreeNode> {
        return when (peek().kind) {
            TokenKind.STRING_START -> {
                parseStringNode()
            }
            TokenKind.IDENTIFIER -> {
                val consumed = consume()
                Either.Right(VariableReferenceNode(
                    name = consumed.value,
                    start = consumed
                ))
            }
            TokenKind.OPENING_PARENTHESES -> {
                position++
                val expr = parseStringExpression()
                consume(TokenKind.CLOSING_PARENTHESES).ifLeft {
                    return Either.Left(it)
                }
                expr
            }
            else -> Either.Left(ParsingError.UnexpectedToken(peek()))
        }
    }

    fun parseStringNode(): Either<ParsingError, SyntaxTreeNode> {
        val start = consume(TokenKind.STRING_START).ifLeft {
            return Either.Left(it)
        }
        val segments = mutableListOf<StringNode.Segment>()
        while (position < tokens.count() && peek().kind != TokenKind.STRING_END) {
            val segment = when (peek().kind) {
                TokenKind.STRING_TEXT -> {
                    val text = consume().value
                    StringNode.Segment.Text(text)
                }
                TokenKind.STRING_EXPRESSION_REFERENCE -> {
                    val consumed = consume()
                    StringNode.Segment.Reference(VariableReferenceNode(consumed.value, consumed))
                }
                else -> return Either.Left(ParsingError.UnexpectedToken(peek()))
            }
            segments.add(segment)
        }
        consume(TokenKind.STRING_END).ifLeft {
            return Either.Left(it)
        }
        return Either.Right(StringNode(
            value = "",
            segments = segments,
            start = start.unwrap()
        ))
    }

    fun parseCallParameters(): Either<ParsingError, List<SyntaxTreeNode>> {
        consume(TokenKind.OPENING_PARENTHESES).ifLeft {
            return Either.Left(it)
        }
        val parameters = mutableListOf<SyntaxTreeNode>()
        while (peek().kind != TokenKind.CLOSING_PARENTHESES) {
            val a = peek()
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
        return Either.Right(parameters)
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
            TokenKind.SELF -> {
                position++
                if (peek().kind !== TokenKind.DOT) {
                    return Either.Right(VariableReferenceNode("self", tokens[position - 1]))
                }
                position++
                val field = parseIdentifier()
                Either.Right(StructAccessNode(
                    struct = "self",
                    field = field.unwrap(),
                ))
            }
            TokenKind.NUMBER -> {
                val token = consume()
                Either.Right(NumberNode(token.value, false, Type.Int32, token))
            }
            TokenKind.IDENTIFIER -> consume().let { Either.Right(VariableReferenceNode(it.value, it)) }
            else -> Either.Left(ParsingError.UnexpectedToken(tokens[position]))
        }
    }

    fun parseArray(): Either<ParsingError, SyntaxTreeNode> {
        val dynamic = peek().kind == TokenKind.TIMES
        if (dynamic) {
            position++
        }
        val openingToken = consume(TokenKind.OPENING_BRACKETS).ifLeft {
            return Either.Left(it)
        }.unwrap()
        val elements = mutableListOf<SyntaxTreeNode>()
        while (peek().kind != TokenKind.CLOSING_BRACKETS) {
            val element = parseExpression()
            if (element.isLeft()) {
                return Either.Left(element.getLeft())
            }
            elements.add(element.getRight())
            if (peek().kind == TokenKind.COMMA) {
                position++
            }
        }
        val closingToken = consume(TokenKind.CLOSING_BRACKETS).ifLeft {
            return Either.Left(it)
        }.unwrap()
        return Either.Right(ArrayNode(
            elements = elements,
            dynamic = dynamic,
            start = openingToken,
            end = closingToken
        ))
    }

    fun parseArrayAccess(): Either<ParsingError, SyntaxTreeNode> {
        val identifier = parseIdentifier()
        if (identifier.isLeft()) {
            return Either.Left(identifier.getLeft())
        }
        val openingToken = consume(TokenKind.OPENING_BRACKETS).ifLeft {
            return Either.Left(it)
        }.unwrap()
        val index = parseExpression()
        if (index.isLeft()) {
            return Either.Left(index.getLeft())
        }
        val closingToken = consume(TokenKind.CLOSING_BRACKETS).ifLeft {
            return Either.Left(it)
        }.unwrap()
        return Either.Right(ArrayAccessNode(
            identifier = identifier.unwrap(),
            index = index.unwrap(),
            start = openingToken,
            end = closingToken
        ))
    }

    fun parseMutation(struct: String): Either<ParsingError, SyntaxTreeNode> {
        consume(TokenKind.DOT).ifLeft {
            return Either.Left(it)
        }
        val field = parseIdentifier()
        if (field.isLeft()) {
            return Either.Left(field.getLeft())
        }
        val mutation = consume(TokenKind.MUTATION).ifLeft {
            return Either.Left(it)
        }
        val expression = parseExpression()
        if (expression.isLeft()) {
            return Either.Left(expression.getLeft())
        }
        return Either.Right(MutationNode(
            struct = struct,
            field = field.unwrap(),
            expression = expression.unwrap(),
            start = mutation.unwrap()
        ))
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