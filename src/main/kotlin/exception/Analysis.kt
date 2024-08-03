package me.gabriel.gwydion.exception

import me.gabriel.gwydion.parsing.BinaryOperatorNode
import me.gabriel.gwydion.parsing.SyntaxTreeNode
import me.gabriel.gwydion.parsing.Type

sealed class AnalysisError(val message: String, val node: SyntaxTreeNode) {
    class InvalidAddition(node: BinaryOperatorNode) : AnalysisError(
        "invalid addition: ${node.left.startToken?.kind} + ${node.right.startToken?.kind}",
        node
    )

    class ReturnTypeMismatch(node: SyntaxTreeNode, expected: Type, actual: Type) : AnalysisError(
        "return type mismatch: expected $expected, got $actual",
        node
    )

    class UndefinedVariable(node: SyntaxTreeNode) : AnalysisError(
        "undefined variable: ${node.startToken?.kind}",
        node
    )
}