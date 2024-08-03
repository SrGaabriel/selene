package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.*
import kotlin.math.exp

class KotlinCodeExecutor(private val tree: SyntaxTree): CodeExecutor {
    val functions = mutableListOf<FunctionNode>()
    val variables = mutableMapOf<String, Int>()
    val intrinsics = mutableListOf<IntrinsicFunction>(
        PrintFunction()
    )

    override fun execute() {
        executeBlock(tree.root, false)
        executeMain()
    }

    fun join(otherTree: SyntaxTree) {
        tree.join(otherTree)
    }

    fun executeMain(): Int {
        val mainFunction = functions.find { it.name == "main" }
        if (mainFunction == null) {
            throw IllegalStateException("No main function found")
        }
        return executeBlock(mainFunction.block)
    }

    fun executeBlock(block: BlockNode, expectReturn: Boolean=true): Int {
        val statements = block.getChildren()
        statements.forEach { statement ->
            when (statement) {
                is FunctionNode -> {
                    registerFunction(statement)
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
                    val variable = variables[statement.name] ?: throw IllegalStateException("Variable ${statement.name} not found")
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
        return 0
    }

    fun registerFunction(node: FunctionNode) {
        functions.add(node)
    }

    fun executeExpression(expression: SyntaxTreeNode): Int {
        return when (expression) {
            is NumberNode -> expression.value
            is BinaryOperatorNode -> {
                val left = executeExpression(expression.left)
                val right = executeExpression(expression.right)
                when (expression.operator) {
                    TokenKind.PLUS -> left + right
                    TokenKind.MINUS -> left - right
                    TokenKind.TIMES -> left * right
                    TokenKind.DIVIDE -> left / right
                    else -> throw IllegalStateException("Unknown operator")
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

    fun executeFunction(name: String, parameters: List<Any>): Int {
        val function = functions.find { it.name == name } ?: throw IllegalStateException("Function $name not found")
        val block = function.block
        function.parameters.parameters.forEachIndexed { index, parameter ->
            variables[parameter.name] = parameters[index] as Int
        }
        if (function.modifiers.contains(TokenKind.INTRINSIC)) {
            val intrinsic = intrinsics.find { it.name == name } ?: throw IllegalStateException("Intrinsic $name not found")
            return intrinsic.execute(parameters as List<Int>)
        } else {
            return executeBlock(block)
        }
    }
}