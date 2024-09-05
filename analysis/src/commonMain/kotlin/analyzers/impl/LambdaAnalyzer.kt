package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisError
import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.LambdaNode

class LambdaAnalyzer: SingleNodeAnalyzer<LambdaNode>(LambdaNode::class) {
    override fun register(
        block: SymbolBlock,
        node: LambdaNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val newBlock = block.createChild(
            id = node
        )

        block.defineSymbol(node, SeleneType.Unknown)
        return newBlock
    }

    override fun analyze(
        block: SymbolBlock,
        node: LambdaNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val lambdaBlock = block.surfaceSearchChild(node)
            ?: error("Lambda block not registered")

        val type = block.resolveExpression(node) ?: SeleneType.Unknown
        if (type == SeleneType.Unknown) {
            results.errors.add(
                AnalysisError.LambdaTypeCannotBeInferred(
                    node
                )
            )
        }
        return lambdaBlock
    }
}