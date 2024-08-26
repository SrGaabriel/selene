package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.AssignmentNode

class AssignmentAnalyzer: SingleNodeAnalyzer<AssignmentNode>(AssignmentNode::class) {
    override fun register(
        block: SymbolBlock,
        node: AssignmentNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        println("Ran")
        visitor.visit(node.expression) {
            println("Visited ${node.expression} ${block.resolveExpression(node.expression)}")
            val type = block.resolveExpression(node.expression) ?: return@visit

            if (node.mutable) {
                block.defineSymbol(node, GwydionType.Mutable(type))
            } else {
                block.defineSymbol(node, type)
            }

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