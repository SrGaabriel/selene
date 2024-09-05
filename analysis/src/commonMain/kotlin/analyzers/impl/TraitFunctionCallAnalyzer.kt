package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.analysis.util.resolveTraitForExpression
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.TraitFunctionCallNode

class TraitFunctionCallAnalyzer: SingleNodeAnalyzer<TraitFunctionCallNode>(TraitFunctionCallNode::class) {
    override fun register(
        block: SymbolBlock,
        node: TraitFunctionCallNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val traitType = block.resolveExpression(node.trait) ?: visitor.visit(node.trait) {
            block.resolveExpression(node.trait) ?: return@visit
        }

        if (traitType is SeleneType.Trait) {
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