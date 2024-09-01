package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.VariableReferenceNode

class VariableReferenceAnalyzer: SingleNodeAnalyzer<VariableReferenceNode>(VariableReferenceNode::class) {
    override fun register(
        block: SymbolBlock,
        node: VariableReferenceNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        block.defineSymbol(node, block.resolveSymbol(node.name) ?: GwydionType.Unknown)
        return block
    }
}