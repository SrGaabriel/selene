package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.InstantiationNode

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
        return block
    }
}