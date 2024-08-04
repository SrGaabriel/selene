package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.exception.AnalysisError
import me.gabriel.gwydion.parsing.*
import me.gabriel.gwydion.util.Either

class CumulativeSemanticAnalyzer(
    private val tree: SyntaxTree,
    private val repository: ProgramMemoryRepository
): SemanticAnalyzer {
    private val errors = mutableListOf<AnalysisError>()

    override fun analyzeTree(): AnalysisResult {
        findSymbols(tree.root, repository.root)
        if (errors.isNotEmpty()) {
            return AnalysisResult(errors)
        }
        analyzeNode(tree.root, repository.root)
        return AnalysisResult(errors)
    }

    fun findSymbols(node: SyntaxTreeNode, block: MemoryBlock): MemoryBlock? {
        when (node) {
            is RootNode -> {
                val parentBlocks = node.getChildren().map {
                    it to findSymbols(it, block)
                }
                parentBlocks.forEach { (parent, parentBlock) ->
                    parent.getChildren().forEach { findSymbols(it, parentBlock ?: block) }
                }
            }
            is BlockNode -> {
                node.getChildren().forEach { findSymbols(it, block) }
            }
            is FunctionNode -> {
                block.symbols.declare(node.name, node.returnType)
                println("Creating block ${node.name} in ${block.name}")
                return repository.createBlock(node.name, block)
            }
            is ParameterNode -> {
                if (node.type == Type.Unknown) {
                    errors.add(AnalysisError.UnknownType(node, node.type))
                    return null
                }
                block.symbols.declare(node.name, node.type)
                node.getChildren().forEach { findSymbols(it, block) }
            }
            is AssignmentNode -> {
                val type = if (node.type == Type.Unknown) {
                    getExpressionType(block, node.expression).getRightOrNull() ?: Type.Unknown
                } else {
                    node.type
                }
                block.symbols.declare(node.name, type)
                node.getChildren().forEach { findSymbols(it, block) }
            }
            else -> {}
        }
        return null
    }

    fun analyzeNode(node: SyntaxTreeNode, block: MemoryBlock) {
        val deeperBlock: MemoryBlock = when (node) {
            is BinaryOperatorNode -> {
                val getLeftType = getExpressionType(block, node.left)
                val getRightType = getExpressionType(block, node.right)

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
                block
            }
            is FunctionNode -> {
                val functionBlock = repository.root.surfaceSearchChild(node.name) ?: error("Block ${node.name} not found")
                if (node.modifiers.contains(Modifiers.INTRINSIC)) {
                    return
                }

                val returnNode = node.body.getChildren().find { it is ReturnNode }
                var returnType = if (returnNode != null) {
                    val result = getExpressionType(functionBlock, (returnNode as ReturnNode).expression)
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
                functionBlock
            }
            is IfNode -> {
                val conditionType = getExpressionType(block, node.condition)
                if (conditionType is Either.Left) {
                    errors.add(conditionType.value)
                    return
                }
                if (conditionType.unwrap() != Type.Boolean) {
                    errors.add(AnalysisError.InvalidCondition(node, conditionType.unwrap()))
                }
                block
            }
            else -> {
                block
            }
        }
        node.getChildren().forEach { analyzeNode(it, deeperBlock) }
    }

    tailrec fun getExpressionType(block: MemoryBlock, node: SyntaxTreeNode): Either<AnalysisError, Type> {
        return when (node) {
            is VariableReferenceNode -> {
                Either.Right(block.figureOutSymbol(node.name) ?: return Either.Left(AnalysisError.UndefinedVariable(node, block)))
            }
            is TypedSyntaxTreeNode -> Either.Right(node.type)
            is BinaryOperatorNode -> getExpressionType(block, node.left)
            is CallNode -> Either.Right(inferCallType(block, node))
            is EqualsNode -> Either.Right(Type.Boolean)
            else -> error("Unknown node type $node")
        }
    }

    fun inferCallType(block: MemoryBlock, node: CallNode): Type {
        val function = block.figureOutSymbol(node.name) ?: error("Function ${node.name} not found")
        return function
    }
}