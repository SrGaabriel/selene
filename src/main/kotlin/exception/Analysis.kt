package me.gabriel.gwydion.exception

import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.CallNode
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

    class UndefinedVariable(node: SyntaxTreeNode, name: String, block: MemoryBlock) : AnalysisError(
        "undefined variable: $name in block ${block.name}",
        node
    )

    class UndefinedTrait(node: SyntaxTreeNode, name: String) : AnalysisError(
        "undefined trait: $name",
        node
    )

    class UndefinedFunction(node: CallNode, block: MemoryBlock) : AnalysisError(
        "undefined function: ${node.name} in block ${block.name}",
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

    class WrongArgumentTypeForInstantiation(node: SyntaxTreeNode, expected: Type, actual: Type) : AnalysisError(
        "wrong argument type for instantiation: expected $expected, got $actual",
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

    class WrongArgumentTypeForFunctionCall(node: CallNode, expected: Type, actual: Type) : AnalysisError(
        "wrong argument type for function call: expected $expected, got $actual",
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