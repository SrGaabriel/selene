package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.ArrayAccessNode
import me.gabriel.selene.frontend.workingBase

class ArrayAccessAnalyzer: SingleNodeAnalyzer<ArrayAccessNode>(ArrayAccessNode::class) {
    override fun register(
        block: SymbolBlock,
        node: ArrayAccessNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val resolved = block.resolveExpression(node.array) ?: return block
        val type = when (val base = resolved.workingBase()) {
            is SeleneType.FixedArray -> base.baseType
            is SeleneType.DynamicArray -> base.baseType
            else -> return block
        }

        block.defineSymbol(node, type)

        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: ArrayAccessNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        return block
    }
}