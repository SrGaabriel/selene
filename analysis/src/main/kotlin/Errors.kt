package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.lexing.TokenKind
import me.gabriel.gwydion.frontend.parsing.CallNode
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode
import me.gabriel.gwydion.frontend.Type

data class AnalysisResult(
    val errors: MutableList<AnalysisError> = mutableListOf(),
    val warnings: MutableList<AnalysisWarning> = mutableListOf()
)

sealed class AnalysisError(val message: String, val node: SyntaxTreeNode) {
    class InvalidOperation(node: SyntaxTreeNode, leftType: Type, operator: TokenKind, rightType: Type) : AnalysisError(
        "invalid operation: cannot run $operator on $leftType and $rightType",
        node
    )

    class ReturnTypeMismatch(node: SyntaxTreeNode, expected: Type, actual: Type) : AnalysisError(
        "return type mismatch: expected $expected, got $actual",
        node
    )

    class UndefinedVariable(node: SyntaxTreeNode, name: String, block: String) : AnalysisError(
        "undefined variable: $name in block $block",
        node
    )

    class TraitForFunctionNotFound(node: SyntaxTreeNode, name: String, function: String) : AnalysisError(
        "unknown trait: func `$function` for variable $name could not be found",
        node
    )

    class UndefinedFunction(node: CallNode, block: String) : AnalysisError(
        "undefined function: ${node.name} in block $block",
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

    class UndefinedArray(node: SyntaxTreeNode, name: String) : AnalysisError(
        "undefined array: $name",
        node
    )

    class NotAnArray(node: SyntaxTreeNode, type: Type) : AnalysisError(
        "not an array: $type",
        node
    )

    class UndefinedDataStructure(node: SyntaxTreeNode, name: String) : AnalysisError(
        "undefined data structure: $name",
        node
    )

    class MissingArgumentsForInstantiation(node: SyntaxTreeNode, name: String) : AnalysisError(
        "missing arguments for instantiation of $name",
        node
    )

    class MissingReturnStatement(node: SyntaxTreeNode) : AnalysisError(
        "missing return statement",
        node
    )

    class ReturnOutsideFunction(node: SyntaxTreeNode) : AnalysisError(
        "return statement outside function",
        node
    )

    class WrongArgumentTypeForInstantiation(node: SyntaxTreeNode, expected: Type, actual: Type) : AnalysisError(
        "wrong argument type for instantiation: expected ${expected.id}, got ${actual.id}",
        node
    )

    class UndefinedField(node: SyntaxTreeNode, field: String) : AnalysisError(
        "undefined field: $field",
        node
    )

    class NotADataStructure(node: SyntaxTreeNode, type: Type) : AnalysisError(
        "not a data structure: $type",
        node
    )

    class InvalidStructAccess(node: SyntaxTreeNode, struct: Type) : AnalysisError(
        "invalid struct access: $struct",
        node
    )

    class MissingArgumentsForFunctionCall(node: CallNode, expected: Int, actual: Int) : AnalysisError(
        "missing arguments for function call: expected $expected, got $actual",
        node
    )

    class WrongArgumentTypeForFunctionCall(node: SyntaxTreeNode, expected: Type, actual: Type) : AnalysisError(
        "wrong argument type for function call: expected ${expected.signature}, got ${actual.signature}",
        node
    )

    class TypeCannotBeMutable(node: SyntaxTreeNode, type: Type) : AnalysisError(
        "type cannot be mutable: $type",
        node
    )

    class ImmutableVariableMutation(node: SyntaxTreeNode, name: String) : AnalysisError(
        "immutable variable mutation: $name",
        node
    )
}

sealed class AnalysisWarning(val message: String, val node: SyntaxTreeNode) {
    class UnreachableCode(node: SyntaxTreeNode) : AnalysisWarning(
        "unreachable code",
        node
    )
}