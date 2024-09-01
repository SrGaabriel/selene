package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.parsing.LambdaParameterNode

class LambdaParameterAnalyzer: SingleNodeAnalyzer<LambdaParameterNode>(LambdaParameterNode::class) {
    override fun register(
        block: SymbolBlock,
        node: LambdaParameterNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        block.declareSymbol(node.name, node.type)
        block.defineSymbol(node, node.type)
        return block
    }
}