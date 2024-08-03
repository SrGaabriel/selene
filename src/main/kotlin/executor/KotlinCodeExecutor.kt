package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.*

class KotlinCodeExecutor(private val tree: SyntaxTree): CodeExecutor {
    val functions = mutableListOf<FunctionNode>()
    val variables = mutableMapOf<String, Int>()

    override fun execute() {
        executeBlock(tree.root, false)
        println(executeMain())
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
                else -> {
                    throw IllegalStateException("Unknown statement type $statement")
                }
            }
        }
        if (expectReturn) throw IllegalStateException("No return statement found") else return 0
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
                functions.find { it.name == expression.name }?.let {
                    return executeBlock(it.block)
                } ?: throw IllegalStateException("Function ${expression.name} not found")
            }
            else -> throw IllegalStateException("Unknown expression type $expression")
        }
    }
}