package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.parsing.LambdaParameterNode

class LambdaParameterAnalyzer: SingleNodeAnalyzer<LambdaParameterNode>(LambdaParameterNode::class) {
    override fun register(
        block: SymbolBlock,
        node: LambdaParameterNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        block.declareSymbol(node.name, node.type)
        block.defineSymbol(node, node.type)
        return block
    }
}