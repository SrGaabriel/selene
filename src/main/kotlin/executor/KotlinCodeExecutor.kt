package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.*

class KotlinCodeExecutor(private val tree: SyntaxTree): CodeExecutor {
    val functions = mutableListOf<FunctionNode>()

    override fun execute() {
        executeBlock(tree.root)
        executeMain()
    }

    fun executeMain() {
        val mainFunction = functions.find { it.name == "main" }
        if (mainFunction == null) {
            throw IllegalStateException("No main function found")
        }
        executeBlock(mainFunction.block)
    }

    fun executeBlock(block: BlockNode) {
        val statements = block.getChildren()
        statements.forEach { statement ->
            when (statement) {
                is FunctionNode -> {
                    registerFunction(statement)
                }
                is ReturnNode -> {
                    val expression = statement.expression
                    println(evaluateExpression(expression))
                }
                else -> {
                    throw IllegalStateException("Unknown statement type $statement")
                }
            }
        }
    }

    fun registerFunction(node: FunctionNode) {
        functions.add(node)
    }

    fun evaluateExpression(expression: SyntaxTreeNode): Int {
        return when (expression) {
            is NumberNode -> expression.value
            is BinaryOperatorNode -> {
                val left = evaluateExpression(expression.left)
                val right = evaluateExpression(expression.right)
                when (expression.operator) {
                    TokenKind.PLUS -> left + right
                    else -> throw IllegalStateException("Unknown operator")
                }
            }
            else -> throw IllegalStateException("Unknown expression type")
        }
    }
}