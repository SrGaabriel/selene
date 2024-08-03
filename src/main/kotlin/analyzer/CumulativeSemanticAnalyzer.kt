package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.exception.AnalysisError
import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.*
import me.gabriel.gwydion.util.Either

class CumulativeSemanticAnalyzer(
    private val tree: SyntaxTree,
    private val symbolTable: SymbolTable = SymbolTable()
): SemanticAnalyzer {
    private val errors = mutableListOf<AnalysisError>()

    override fun analyzeTree(): AnalysisResult {
        findSymbols(tree.root)
        if (errors.isNotEmpty()) {
            return AnalysisResult(symbolTable, errors)
        }
        analyzeNode(tree.root)
        return AnalysisResult(symbolTable, errors)
    }

    fun findSymbols(node: SyntaxTreeNode) {
        when (node) {
            is ParameterNode -> {
                if (node.type == Type.Unknown) {
                    errors.add(AnalysisError.UnknownType(node, node.type))
                    return
                }
                symbolTable.declare(node.name, node.type)
            }
            is FunctionNode -> {
                symbolTable.declare(node.name, node.returnType)
            }
            is AssignmentNode -> {
                val type = if (node.type == Type.Unknown) {
                    getExpressionType(node.expression).getRightOrNull() ?: Type.Unknown
                } else {
                    node.type
                }
                symbolTable.declare(node.name, type)
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
                if (leftType != rightType) {
                    errors.add(AnalysisError.InvalidOperation(node, leftType, node.operator, rightType))
                }
            }
            is FunctionNode -> {
                if (node.modifiers.contains(Modifiers.INTRINSIC)) {
                    return
                }

                val returnNode = node.body.getChildren().find { it is ReturnNode }
                var returnType = if (returnNode != null) {
                    val result = getExpressionType((returnNode as ReturnNode).expression)
                    if (result is Either.Left) {
                        errors.add(result.value)
                        return
                    }
                    result.unwrap()
                } else {
                    Type.Void
                }
                // If the user is returning an unknown type, we will assume that the function is returning that type
                val inferredType = if (returnType == Type.Unknown) { node.returnType } else { returnType }
                if (inferredType != node.returnType) {
                    errors.add(AnalysisError.ReturnTypeMismatch(node, node.returnType, returnType))
                }
            }
            is IfNode -> {
                val conditionType = getExpressionType(node.condition)
                if (conditionType is Either.Left) {
                    errors.add(conditionType.value)
                    return
                }
                if (conditionType.unwrap() != Type.Boolean) {
                    errors.add(AnalysisError.InvalidCondition(node, conditionType.unwrap()))
                }
            }
            else -> {}
        }
        node.getChildren().forEach { analyzeNode(it) }
    }

    tailrec fun getExpressionType(node: SyntaxTreeNode): Either<AnalysisError, Type> {
        return when (node) {
            is VariableReferenceNode -> {
                Either.Right(symbolTable.lookup(node.name) ?: return Either.Left(AnalysisError.UndefinedVariable(node)))
            }
            is TypedSyntaxTreeNode -> Either.Right(node.type)
            is BinaryOperatorNode -> getExpressionType(node.left)
            is CallNode -> Either.Right(inferCallType(node))
            is EqualsNode -> Either.Right(Type.Boolean)
            else -> error("Unknown node type $node")
        }
    }

    fun inferCallType(node: CallNode): Type {
        val function = symbolTable.lookup(node.name) ?: return Type.Unknown
        return function
    }
}