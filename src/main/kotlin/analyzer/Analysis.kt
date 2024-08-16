package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.exception.AnalysisError
import me.gabriel.gwydion.parsing.*
import me.gabriel.gwydion.util.Either

class AnalysisResult(
    val errors: List<AnalysisError>
)

tailrec fun getExpressionType(block: MemoryBlock, node: SyntaxTreeNode): Either<AnalysisError, out Type> {
    return when (node) {
        is VariableReferenceNode -> {
            Either.Right(block.figureOutSymbol(node.name) ?: return Either.Left(AnalysisError.UndefinedVariable(node, node.name, block)))
        }
        is AssignmentNode -> {
            if (node.type == Type.Unknown) {
                getExpressionType(block, node.expression).getRightOrNull() ?: Type.Unknown
            } else {
                node.type
            }

            getExpressionType(block, node.expression)
        }
        is TypedSyntaxTreeNode -> Either.Right(node.type)
        is BinaryOperatorNode -> getExpressionType(block, node.left)
        is CallNode -> Either.Right(inferCallType(block, node))
        is EqualsNode -> Either.Right(Type.Boolean)
        is ArrayNode -> getExpressionType(block, node.elements.first()).let {
            it.mapRight {
                if (!node.dynamic) Type.FixedArray(it, node.elements.size)
                else Type.DynamicArray(it)
            }
        }
        is ArrayAccessNode -> {
            val arrayType = block.figureOutSymbol(node.identifier) ?: return Either.Left(AnalysisError.UndefinedArray(node, node.identifier))
            when (arrayType) {
                is Type.FixedArray -> Either.Right(arrayType.type)
                is Type.DynamicArray -> Either.Right(arrayType.type)
                else -> Either.Left(AnalysisError.NotAnArray(node, arrayType))
            }
        }
        is InstantiationNode -> {
            val struct = block.figureOutSymbol(node.name) ?: return Either.Left(AnalysisError.UndefinedDataStructure(node, node.name))
            Either.Right(struct)
        }
        is DataStructureNode -> Either.Right(block.figureOutSymbol(node.name) ?: return Either.Left(AnalysisError.UndefinedDataStructure(node, node.name)))
        is TraitFunctionCallNode -> {
            val (_, function) = figureOutTraitForVariable(
                block = block,
                variable = node.trait,
                call = node.function
            ) ?: return Either.Left(AnalysisError.UndefinedTrait(node, node.trait))
            Either.Right(function.returnType)
        }
        is StructAccessNode -> {
            val struct = when (node.struct) {
                "self" -> block.self ?: return Either.Left(AnalysisError.UndefinedDataStructure(node, node.struct))
                else -> block.figureOutSymbol(node.struct) ?: return Either.Left(AnalysisError.UndefinedDataStructure(node, node.struct))
            }

            when (struct) {
                is Type.Struct -> {
                    val field = struct.fields[node.field] ?: return Either.Left(AnalysisError.UndefinedField(node, node.field))
                    Either.Right(field)
                }
                else -> Either.Left(AnalysisError.NotADataStructure(node, struct))
            }
        }
        else -> error("Unknown node type $node")
    }
}

fun inferCallType(block: MemoryBlock, node: CallNode): Type {
    val function = block.figureOutSymbol(node.name) ?: return Type.Unknown
    return function
}

fun figureOutTraitForVariable(
    block: MemoryBlock,
    variable: String,
    call: String
): Pair<Type.Trait, TraitFunctionNode>? {
    val resolvedVariable = block.figureOutSymbol(variable) ?: return null

    return when (resolvedVariable) {
        is Type.Struct -> {
            val (trait, function) = resolvedVariable.traits
                .firstNotNullOfOrNull { trait ->
                    trait.functions.find { it.name == call }?.let { trait to it }
                } ?: return null

            trait to function
        }
        is Type.Trait -> {
            val function = resolvedVariable.functions.find { it.name == call } ?: return null
            resolvedVariable to function
        }
        else -> null
    }
}
