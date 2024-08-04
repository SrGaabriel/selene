package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.exception.AnalysisError
import me.gabriel.gwydion.parsing.*
import me.gabriel.gwydion.util.Either

class AnalysisResult(
    val errors: List<AnalysisError>
)

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
    val function = block.figureOutSymbol(node.name) ?: return Type.Unknown
    return function
}