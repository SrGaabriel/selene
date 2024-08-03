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
        if (errors.isNotEmpty()) {
            return errors
        }
        analyzeNode(tree.root)
        return errors
    }

    fun findSymbols(node: SyntaxTreeNode) {
        when (node) {
            is ParameterNode -> {
                if (node.type == Type.UNKNOWN) {
                    errors.add(AnalysisError.UnknownType(node, node.type))
                    return
                }
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
                val getLeftType = getExpressionType(node.left)
                val getRightType = getExpressionType(node.right)

                if (getLeftType is Either.Left) {
                    errors.add(getLeftType.value)
                    return
                } else if (getRightType is Either.Left) {
                    errors.add(getRightType.value)
                    return
                }
                val leftType = getLeftType.unwrap()
                val rightType = getRightType.unwrap()
                if (node.operator == TokenKind.PLUS) {
                    if (node.left !is AlgebraicNode && node.right !is StringNode) {
                        errors.add(AnalysisError.InvalidOperation(node, leftType, node.operator, rightType))
                    }
                }

                if (leftType != rightType) {
                    errors.add(AnalysisError.InvalidOperation(node, leftType, node.operator, rightType))
                }
            }
            is FunctionNode -> {
                val returnNode = node.block.getChildren().find { it is ReturnNode }
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

    tailrec fun getExpressionType(node: SyntaxTreeNode): Either<AnalysisError, Type> {
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