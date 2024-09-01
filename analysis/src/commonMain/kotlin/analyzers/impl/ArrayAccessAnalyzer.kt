package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.ArrayAccessNode
import me.gabriel.gwydion.frontend.workingBase

class ArrayAccessAnalyzer: SingleNodeAnalyzer<ArrayAccessNode>(ArrayAccessNode::class) {
    override fun register(
        block: SymbolBlock,
        node: ArrayAccessNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val resolved = block.resolveExpression(node.array) ?: return block
        val type = when (val base = resolved.workingBase()) {
            is GwydionType.FixedArray -> base.baseType
            is GwydionType.DynamicArray -> base.baseType
            else -> return block
        }

        block.defineSymbol(node, type)

        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: ArrayAccessNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        return block
    }
}