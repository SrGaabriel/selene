package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.exception.AnalysisError
import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.*
import me.gabriel.gwydion.util.Either

class CumulativeSemanticAnalyzer(private val tree: SyntaxTree): SemanticAnalyzer {
    private val errors = mutableListOf<AnalysisError>()
    private val symbolTable = SymbolTable()

    override fun analyzeTree(): List<AnalysisError> {
        findSymbols(tree.root)
        analyzeNode(tree.root)
        return errors
    }

    fun findSymbols(node: SyntaxTreeNode) {
        when (node) {
            is VariableNode -> {
                symbolTable.declare(node.name, node.type)
            }
            is FunctionNode -> {
                symbolTable.declare(node.name, node.returnType)
            }
            else -> {}
        }
        node.getChildren().forEach { findSymbols(it) }
    }

    fun analyzeNode(node: SyntaxTreeNode) {
        when (node) {
            is BinaryOperatorNode -> {
                if (node.operator == TokenKind.PLUS) {
                    if (node.left !is AlgebraicNode && node.right !is StringNode) {
                        errors.add(AnalysisError.InvalidAddition(node))
                    }
                }
                if (node.left is StringNode && node.right !is StringNode) {
                    errors.add(AnalysisError.InvalidAddition(node))
                } else if (node.left !is StringNode && node.right is StringNode) {
                    errors.add(AnalysisError.InvalidAddition(node))
                } else if (node.left is AlgebraicNode && node.right !is AlgebraicNode) {
                    if (node.operator != TokenKind.PLUS) {
                        errors.add(AnalysisError.InvalidAddition(node))
                    }
                } else if (node.left !is AlgebraicNode && node.right is AlgebraicNode) {
                    if (node.operator != TokenKind.PLUS) {
                        errors.add(AnalysisError.InvalidAddition(node))
                    }
                }
            }
            is FunctionNode -> {
                val returnNode = node.block.getChildren().find { it is ReturnNode }
                println(node.returnType)
                var returnType = if (returnNode != null) {
                    val result = getExpressionType((returnNode as ReturnNode).expression)
                    if (result is Either.Left) {
                        errors.add(result.value)
                        return
                    }
                    result.unwrap()
                } else {
                    Type.VOID
                }
                // If the user is returning an unknown type, we will assume that the function is returning that type
                val inferredType = if (returnType == Type.UNKNOWN) { node.returnType } else { returnType }
                if (inferredType != node.returnType) {
                    println("return type mismatch: expected ${node.returnType}, got $returnType for function ${node.name}")
                    errors.add(AnalysisError.ReturnTypeMismatch(node, node.returnType, returnType))
                }
            }
            else -> {}
        }
        node.getChildren().forEach { analyzeNode(it) }
    }

    fun getExpressionType(node: SyntaxTreeNode): Either<AnalysisError, Type> {
        return when (node) {
            is VariableNode -> {
                Either.Right(symbolTable.lookup(node.name) ?: return Either.Left(AnalysisError.UndefinedVariable(node)))
            }
            is ConstantNode -> Either.Right(node.type)
            is BinaryOperatorNode -> getExpressionType(node.left)
            else -> error("Unknown node type $node")
        }
    }
}