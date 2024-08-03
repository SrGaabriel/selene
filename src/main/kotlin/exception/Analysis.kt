package me.gabriel.gwydion.exception

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.SyntaxTreeNode
import me.gabriel.gwydion.parsing.Type
import me.gabriel.gwydion.parsing.VariableReferenceNode

sealed class AnalysisError(val message: String, val node: SyntaxTreeNode) {
    class InvalidOperation(node: SyntaxTreeNode, leftType: Type, operator: TokenKind, rightType: Type) : AnalysisError(
        "invalid operation: cannot run $operator on $leftType and $rightType",
        node
    )

    class ReturnTypeMismatch(node: SyntaxTreeNode, expected: Type, actual: Type) : AnalysisError(
        "return type mismatch: expected $expected, got $actual",
        node
    )

    class UndefinedVariable(node: VariableReferenceNode) : AnalysisError(
        "undefined variable: ${node.name}",
        node
    )

    class UnknownType(node: SyntaxTreeNode, type: Type) : AnalysisError(
        "unknown type: $type",
        node
    )

    class InvalidCondition(node: SyntaxTreeNode, type: Type) : AnalysisError(
        "invalid condition: condition must be a boolean expression, got $type",
        node
    )
}