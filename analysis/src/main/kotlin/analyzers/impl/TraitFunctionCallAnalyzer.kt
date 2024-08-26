package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.resolveTraitForExpression
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.TraitFunctionCallNode

class TraitFunctionCallAnalyzer: SingleNodeAnalyzer<TraitFunctionCallNode>(TraitFunctionCallNode::class) {
    override fun register(
        block: SymbolBlock,
        node: TraitFunctionCallNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        println("Welp ${node.trait}")
        val traitType = block.resolveExpression(node.trait) ?: return block
        println("Passed? ${traitType}")

        if (traitType is GwydionType.Trait) {
            val function = traitType.functions.firstOrNull { it.name == node.function }
            if (function != null) {
                block.defineSymbol(node, function.returnType)
            }
            return block
        }

        val (_, _, function) = resolveTraitForExpression(
            block = block,
            variable = node.trait,
            signatures = signatures,
            call = node.function
        ) ?: return block

        block.defineSymbol(node, function.returnType)

        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: TraitFunctionCallNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {

        return block
    }
}