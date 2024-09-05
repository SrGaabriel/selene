package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.VariableReferenceNode

class VariableReferenceAnalyzer: SingleNodeAnalyzer<VariableReferenceNode>(VariableReferenceNode::class) {
    override fun register(
        block: SymbolBlock,
        node: VariableReferenceNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        block.defineSymbol(node, block.resolveSymbol(node.name) ?: SeleneType.Unknown)
        return block
    }
}