package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.unknownReferenceSignatureToType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.ParameterNode

class ParameterAnalyzer: SingleNodeAnalyzer<ParameterNode>(ParameterNode::class) {
    override fun register(
        block: SymbolBlock,
        node: ParameterNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val type = unknownReferenceSignatureToType(signatures, node.type)

        if (type == GwydionType.Self && block.self != null) {
            block.defineSymbol(node, block.self!!)
            block.declareSymbol(node.name, block.self!!)
        } else {
            block.defineSymbol(node, type)
            block.declareSymbol(node.name, type)
        }
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: ParameterNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        return block
    }
}