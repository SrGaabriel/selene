package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.doesProvidedTypeAccordToExpectedType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.BinaryOperatorNode

class BinaryOpAnalyzer: SingleNodeAnalyzer<BinaryOperatorNode>(BinaryOperatorNode::class) {
    override fun register(
        block: SymbolBlock,
        node: BinaryOperatorNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
//        block.defineSymbol(node, block.resolveExpression(node.left) ?: GwydionType.Unknown)
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: BinaryOperatorNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val left = block.resolveExpression(node.left) ?: GwydionType.Unknown
        val right = block.resolveExpression(node.right) ?: GwydionType.Unknown

        if (left != right) {
            results.errors.add(
                AnalysisError.BinaryOpTypeMismatch(
                    node,
                    left,
                    right
                )
            )
        }



        return block
    }
}