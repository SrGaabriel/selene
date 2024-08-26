package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.signature.SignatureFunction
import me.gabriel.gwydion.analysis.signature.SignatureTrait
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.parsing.TraitNode

class TraitAnalyzer: SingleNodeAnalyzer<TraitNode>(TraitNode::class) {
    override fun register(block: SymbolBlock, node: TraitNode, signatures: Signatures): SymbolBlock {
        signatures.traits.add(
            SignatureTrait(
                name = node.name,
                functions = node.functions.map {
                    SignatureFunction(
                        name = it.name,
                        returnType = it.returnType,
                        parameters = it.parameters.map { it.type }
                    )
                }
            )
        )
        return block
    }
}