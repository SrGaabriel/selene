package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.analysis.util.unknownReferenceSignatureToType
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.ParameterNode

class ParameterAnalyzer: SingleNodeAnalyzer<ParameterNode>(ParameterNode::class) {
    override fun register(
        block: SymbolBlock,
        node: ParameterNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val type = unknownReferenceSignatureToType(signatures, node.type)

        if (type == SeleneType.Self && block.self != null) {
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