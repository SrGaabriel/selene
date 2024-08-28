package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.SignatureTraitImpl
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.unknownReferenceSignatureToType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.TraitImplNode

class TraitImplAnalyzer: SingleNodeAnalyzer<TraitImplNode>(TraitImplNode::class) {
    override fun register(
        block: SymbolBlock,
        node: TraitImplNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        node.type = unknownReferenceSignatureToType(signatures, node.type)
        if (node.type is GwydionType.UnknownReference) return block

        val trait = signatures.traits.find { it.name == node.trait }
        if (trait == null) return block

        trait.impls.add(
            SignatureTraitImpl(
                trait = node.trait,
                types = node.functions.map { func ->
                    unknownReferenceSignatureToType(signatures, func.returnType).also { treatedType ->
                        func.returnType = treatedType
                    }
                },
                module = block.module,
                struct = node.type.signature,
                index = null
            )
        )

        node.functions.forEach {
            it.blockName = "${node.type.signature}#${it.name}"
        }

        val newBlock = block.createChild(
            name = "${node.type.signature} impls ${node.trait}",
            self = node.type
        )

        return newBlock
    }

    override fun analyze(
        block: SymbolBlock,
        node: TraitImplNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val newBlock = block.surfaceSearchChild("${node.type.signature} impls ${node.trait}")
            ?: error("Block for trait impl ${node.trait} for ${node.type.signature} was not created")

        return newBlock
    }
}