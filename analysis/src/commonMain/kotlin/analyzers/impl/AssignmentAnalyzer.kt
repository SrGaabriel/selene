package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.AssignmentNode

class AssignmentAnalyzer: SingleNodeAnalyzer<AssignmentNode>(AssignmentNode::class) {
    override fun register(
        block: SymbolBlock,
        node: AssignmentNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        visitor.visit(node.expression) {
            val type = block.resolveExpression(node.expression) ?: return@visit

            if (node.mutable) {
                block.defineSymbol(node, SeleneType.Mutable(type))
            } else {
                block.defineSymbol(node, type)
            }
            block.assignSymbol(node.name, node.expression)
            block.defineSymbol(node, type)
            block.declareSymbol(node.name, type)
        }
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: AssignmentNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        return block
    }
}