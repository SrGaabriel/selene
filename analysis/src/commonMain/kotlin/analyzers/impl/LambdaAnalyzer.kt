package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.LambdaNode

class LambdaAnalyzer: SingleNodeAnalyzer<LambdaNode>(LambdaNode::class) {
    override fun register(
        block: SymbolBlock,
        node: LambdaNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        block.defineSymbol(node, GwydionType.Unknown)
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: LambdaNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val type = block.resolveExpression(node) ?: GwydionType.Unknown
        if (type == GwydionType.Unknown) {
            results.errors.add(
                AnalysisError.LambdaTypeCannotBeInferred(
                    node
                )
            )
        }
        return block
    }
}