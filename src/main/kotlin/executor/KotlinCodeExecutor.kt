package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.*
import kotlin.math.exp

class KotlinCodeExecutor(private val tree: SyntaxTree): CodeExecutor {
    val functions = mutableListOf<FunctionNode>()
    val variables = mutableMapOf<String, Any>()
    val intrinsics = mutableListOf(
        PrintFunction(),
        ReadFunction()
    )

    override fun execute() {
        parseRoot()
        executeMain()
    }

    fun join(otherTree: SyntaxTree) {
        tree.join(otherTree)
    }

    fun executeMain(): Any {
        val mainFunction = functions.find { it.name == "main" }
        if (mainFunction == null) {
            throw IllegalStateException("No main function found")
        }
        return executeBlock(mainFunction.block)
    }

    fun parseRoot() {
        tree.root.getChildren().forEach { node ->
            when (node) {
                is FunctionNode -> {
                    registerFunction(node)
                }
                else -> {
                    throw IllegalStateException("Unknown node type $node")
                }
            }
        }
    }

    fun executeBlock(block: BlockNode, expectReturn: Boolean=true): Any {
        val statements = block.getChildren()
        statements.forEach { statement ->
            when (statement) {
                is FunctionNode -> {
                    throw IllegalStateException("Function declaration inside function")
                }
                is ReturnNode -> {
                    val expression = statement.expression
                    if (expectReturn) {
                        return executeExpression(expression)
                    } else {
                        throw IllegalStateException("Unexpected return statement")
                    }
                }
                is AssignmentNode -> {
                    val expression = statement.expression
                    val value = executeExpression(expression)
                    variables[statement.name] = value
                }
                is CallNode -> {
                    executeFunction(statement.name, statement.parameters.parameters.map {
                        executeExpression(it)
                    })
                }
                is CompoundAssignmentNode -> {
                    val expression = statement.expression
                    val value = executeExpression(expression)
                    if (value !is Int) {
                        throw IllegalStateException("Value is not an integer")
                    }
                    val variable = variables[statement.name] ?: throw IllegalStateException("Variable ${statement.name} not found")
                    if (variable !is Int) {
                        throw IllegalStateException("Variable ${statement.name} is not an integer")
                    }
                    val result = when (statement.operator) {
                        TokenKind.PLUS_ASSIGN -> variable + value
                        TokenKind.MINUS_ASSIGN -> variable - value
                        TokenKind.TIMES_ASSIGN -> variable * value
                        TokenKind.DIVIDE_ASSIGN -> variable / value
                        else -> throw IllegalStateException("Unknown operator")
                    }
                    variables[statement.name] = result
                }
                else -> {
                    throw IllegalStateException("Unknown statement type $statement")
                }
            }
        }
        return Unit
    }

    fun registerFunction(node: FunctionNode) {
        functions.add(node)
    }

    fun executeExpression(expression: SyntaxTreeNode): Any {
        return when (expression) {
            is NumberNode -> {
                when (expression.type) {
                    Type.INT8 -> expression.value.toByte()
                    Type.INT16 -> expression.value.toShort()
                    Type.INT32 -> expression.value.toInt()
                    Type.INT64 -> expression.value.toLong()
                    Type.UINT8 -> expression.value.toUByte()
                    Type.UINT16 -> expression.value.toUShort()
                    Type.UINT32 -> expression.value.toUInt()
                    Type.UINT64 -> expression.value.toULong()
                    Type.FLOAT32 -> expression.value.toFloat()
                    Type.FLOAT64 -> expression.value.toDouble()
                    else -> throw IllegalStateException("Unknown number type")
                }
            }
            is StringNode -> expression.value
            is BinaryOperatorNode -> {
                val left = executeExpression(expression.left)
                if (left !is Int && left !is String) {
                    throw IllegalStateException("Left side of binary operator is not an integer")
                }
                val right = executeExpression(expression.right)
                if (!checkIfBothTypesAreEqual(left, right)) {
                    throw IllegalStateException("Both sides of binary operator are not the same type")
                } else {
                    return operateUnknownNumbers(left, expression.operator, right)
                }
            }
            is VariableNode -> {
                variables[expression.name] ?: throw IllegalStateException("Variable ${expression.name} not found")
            }
            is CallNode -> {
                executeFunction(expression.name, expression.parameters.parameters.map {
                    executeExpression(it)
                })
            }
            else -> throw IllegalStateException("Unknown expression type $expression")
        }
    }

    fun executeFunction(name: String, parameters: List<Any>): Any {
        val function = functions.find { it.name == name } ?: throw IllegalStateException("Function $name not found")
        val block = function.block
        function.parameters.parameters.forEachIndexed { index, parameter ->
            variables[parameter.name] = parameters[index]
        }
        if (function.modifiers.contains(TokenKind.INTRINSIC)) {
            val intrinsic = intrinsics.find { it.name == name } ?: throw IllegalStateException("Intrinsic $name not found")
            return intrinsic.execute(parameters)
        } else {
            return executeBlock(block)
        }
    }

    fun checkIfBothTypesAreEqual(left: Any, right: Any): Boolean {
        return left::class == right::class
    }

    // All we know is that both left and right have the same types. The return type needs to be the same as well.
    fun operateUnknownNumbers(left: Any, operator: TokenKind, right: Any): Any {
        return when (operator) {
            TokenKind.PLUS -> {
                when (left) {
                    is Int -> left + right as Int
                    is String -> left + right as String
                    else -> throw IllegalStateException("Unknown type for operator")
                }
            }
            TokenKind.MINUS -> {
                when (left) {
                    is Int -> left - right as Int
                    else -> throw IllegalStateException("Unknown type for operator")
                }
            }
            TokenKind.TIMES -> {
                when (left) {
                    is Int -> left * right as Int
                    else -> throw IllegalStateException("Unknown type for operator")
                }
            }
            TokenKind.DIVIDE -> {
                when (left) {
                    is Int -> left / right as Int
                    else -> throw IllegalStateException("Unknown type for operator")
                }
            }
            else -> throw IllegalStateException("Unknown operator")
        }
    }
}