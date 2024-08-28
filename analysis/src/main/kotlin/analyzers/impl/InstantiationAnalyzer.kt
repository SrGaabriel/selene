package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.doesProvidedTypeAccordToExpectedType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.InstantiationNode
import kotlin.math.exp

class InstantiationAnalyzer: SingleNodeAnalyzer<InstantiationNode>(InstantiationNode::class) {
    override fun register(
        block: SymbolBlock,
        node: InstantiationNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val type = signatures.structs.find { it.name == node.name }
            ?.let {
                GwydionType.Struct(
                    it.name,
                    it.fields
                )
            }
            ?: GwydionType.Unknown

        block.defineSymbol(node, type)
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: InstantiationNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val struct = block.resolveExpression(node) as? GwydionType.Struct

        if (struct == null) {
            results.errors.add(
                AnalysisError.UndefinedDataStructure(
                    node = node,
                    name = node.name
                )
            )
            return block
        }

        if (struct.fields.size > node.arguments.size) {
            var index = 0
            val missing = struct.fields.filterNot {
                index++ < node.arguments.size
            }

            results.errors.add(
                AnalysisError.MissingArgumentsForInstantiation(
                    node = node,
                    name = node.name,
                    arguments = missing
                )
            )
        } else if (struct.fields.size < node.arguments.size) {
            results.errors.add(
                AnalysisError.UnexpectedArguments(
                    node = node,
                    name = node.name,
                    arguments = node.arguments.drop(struct.fields.size)
                )
            )
        }

        val types = struct.fields.values.toList()
        node.arguments.forEachIndexed { index, nodeArgument ->
            val expectedType = types[index]
            val providedType = block.resolveExpression(nodeArgument)
            if (providedType == null) {
                results.errors.add(
                    AnalysisError.CouldNotResolveType(
                        nodeArgument,
                        nodeArgument.mark.value
                    )
                )
                return@forEachIndexed
            }

            if (!doesProvidedTypeAccordToExpectedType(
                provided = providedType,
                required = expectedType,
                signatures = signatures
            )) {
                results.errors.add(
                    AnalysisError.WrongArgumentTypeForInstantiation(
                        node = nodeArgument,
                        provided = providedType,
                        expected = expectedType
                    )
                )
            }
        }

        return block
    }
}