package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.ArrayNode

class ArrayAnalyzer: SingleNodeAnalyzer<ArrayNode>(ArrayNode::class) {
    override fun register(
        block: SymbolBlock,
        node: ArrayNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val baseType = node.elements.firstOrNull()?.let {
            block.resolveExpression(it)
        } ?: GwydionType.Unknown

        val arrayType = if (node.dynamic) {
            GwydionType.DynamicArray(baseType)
        } else {
            GwydionType.FixedArray(baseType, node.elements.size)
        }

        block.defineSymbol(node, arrayType)

        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: ArrayNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        return block
    }
}