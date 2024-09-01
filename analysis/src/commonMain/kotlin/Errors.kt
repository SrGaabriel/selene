package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.lexing.TokenKind
import me.gabriel.gwydion.frontend.parsing.CallNode
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode

data class AnalysisResult(
    val errors: MutableList<AnalysisError> = mutableListOf(),
    val warnings: MutableList<AnalysisWarning> = mutableListOf()
)

sealed class AnalysisError(val message: String, val node: SyntaxTreeNode) {
    class InvalidOperation(node: SyntaxTreeNode, leftType: GwydionType, operator: TokenKind, rightType: GwydionType) : AnalysisError(
        "invalid operation: cannot run $operator.sig on ${leftType.signature} and ${rightType.signature}",
        node
    )

    class StructFieldCannotBeMutable(
        node: SyntaxTreeNode,
        name: String
    ): AnalysisError(
        "struct field cannot be mutable: $name",
        node
    )

    class ReturnTypeMismatch(node: SyntaxTreeNode, expected: GwydionType, actual: GwydionType) : AnalysisError(
        "return type mismatch: expected ${expected.signature}, got ${actual.signature}",
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

    class UnknownType(node: SyntaxTreeNode, type: GwydionType) : AnalysisError(
        "unknown type: ${type.signature}",
        node
    )

    class InvalidCondition(node: SyntaxTreeNode, type: GwydionType) : AnalysisError(
        "invalid condition: condition must be a boolean expression, got ${type.signature}",
        node
    )

    class UndefinedArray(node: SyntaxTreeNode, name: String) : AnalysisError(
        "undefined array: $name",
        node
    )
    class NotAnArray(node: SyntaxTreeNode, type: GwydionType) : AnalysisError(
        "not an array: ${type.signature}",
        node
    )

    class UndefinedDataStructure(node: SyntaxTreeNode, name: String) : AnalysisError(
        "undefined data structure: $name",
        node
    )

    class MissingArgumentsForInstantiation(
        node: SyntaxTreeNode,
        name: String,
        arguments: Map<String, GwydionType>
    ) : AnalysisError(
        "missing arguments for instantiation of $name: ${arguments.keys.joinToString(", ") { 
            "$it (${arguments[it]!!.signature})"
        }}",
        node
    )

    class InternalFunctionCall(
        node: CallNode,
        currentModule: String,
        functionModule: String,
    ) : AnalysisError(
        "cannot call internal function `${node.name}(...)` from different module: $currentModule -> $functionModule",
        node
    )

    class UnexpectedArguments(
        node: SyntaxTreeNode,
        name: String,
        arguments: Collection<SyntaxTreeNode>
    ) : AnalysisError(
        node = node,
        message = "too many arguments for $name: ${arguments.joinToString(", ")}"
    )

    class MissingReturnStatement(node: SyntaxTreeNode) : AnalysisError(
        "missing return statement",
        node
    )

    class MissingFunctionForTraitImpl(
        node: SyntaxTreeNode,
        trait: String,
        function: String,
        struct: String
    ) : AnalysisError(
        "missing function `$function` for trait impl: $trait for $struct",
        node
    )

    class LambdaTypeCannotBeInferred(node: SyntaxTreeNode) : AnalysisError(
        "lambda type cannot be inferred",
        node
    )

    class WrongFunctionParametersForTraitImpl(
        node: SyntaxTreeNode,
        function: String,
        trait: String,
        struct: String,
        correct: List<GwydionType>,
    ) : AnalysisError(
        "wrong function signature `$function` in trait impl $trait for $struct, expected${
            if (correct.isEmpty()) " no parameters"
            else ": (${correct.joinToString(", ") { it.signature }})"
        }",
        node
    )

    class BinaryOpTypeMismatch(node: SyntaxTreeNode, left: GwydionType, right: GwydionType) : AnalysisError(
        "binary operation type mismatch: ${left.signature} and ${right.signature}",
        node
    )

    class ReturnOutsideFunction(node: SyntaxTreeNode) : AnalysisError(
        "return statement outside function",
        node
    )

    class WrongArgumentTypeForInstantiation(node: SyntaxTreeNode, expected: GwydionType, provided: GwydionType) : AnalysisError(
        "wrong argument type for instantiation: expected ${expected.signature}, got ${provided.signature}",
        node
    )

    class CouldNotResolveType(
        node: SyntaxTreeNode,
        name: String
    ): AnalysisError(
        "could not resolve type for $name",
        node
    )

    class UndefinedField(node: SyntaxTreeNode, field: String) : AnalysisError(
        "undefined field: $field",
        node
    )

    class ArrayElementTypeMismatch(
        node: SyntaxTreeNode,
        index: Int,
        provided: GwydionType,
        expected: GwydionType
    ): AnalysisError(
        "array element type mismatch: expected ${expected.signature}, got ${provided.signature} at index $index",
        node
    )

    class NotADataStructure(node: SyntaxTreeNode, type: GwydionType) : AnalysisError(
        "not a data structure: ${type.signature}",
        node
    )

    class InvalidStructAccess(node: SyntaxTreeNode, struct: GwydionType) : AnalysisError(
        "invalid struct access: ${struct.signature}",
        node
    )

    class MissingArgumentsForFunctionCall(node: CallNode, expected: Int, actual: Int) : AnalysisError(
        "missing arguments for function call: expected $expected, got $actual",
        node
    )

    class WrongArgumentTypeForFunctionCall(node: SyntaxTreeNode, expected: GwydionType, actual: GwydionType) : AnalysisError(
        "wrong argument type for function call: expected ${expected.signature}, got ${actual.signature}",
        node
    )

    class TypeCannotBeMutable(node: SyntaxTreeNode, type: GwydionType) : AnalysisError(
        "type cannot be mutable: ${type.signature}",
        node
    )

    class ImmutableVariableMutation(node: SyntaxTreeNode, name: String) : AnalysisError(
        "immutable variable mutation: $name",
        node
    )
}

sealed class AnalysisWarning(val message: String, val node: SyntaxTreeNode) {
    class UnusedVariable(node: SyntaxTreeNode, name: String) : AnalysisWarning(
        "unused variable: $name",
        node
    )

    class UnusedFunction(node: SyntaxTreeNode, name: String) : AnalysisWarning(
        "unused function: $name",
        node
    )

    class UnreachableCode(node: SyntaxTreeNode) : AnalysisWarning(
        "unreachable code",
        node
    )
}