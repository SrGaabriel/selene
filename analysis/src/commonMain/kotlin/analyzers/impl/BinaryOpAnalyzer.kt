package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisError
import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.BinaryOperatorNode

class BinaryOpAnalyzer: SingleNodeAnalyzer<BinaryOperatorNode>(BinaryOperatorNode::class) {
    override fun register(
        block: SymbolBlock,
        node: BinaryOperatorNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        visitor.visit(node.left) {
            block.defineSymbol(node, block.resolveExpression(node.left) ?: SeleneType.Undefined)
        }
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: BinaryOperatorNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val left = block.resolveExpression(node.left) ?: SeleneType.Undefined
        val right = block.resolveExpression(node.right) ?: SeleneType.Undefined

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