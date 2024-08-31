package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.SignatureFunction
import me.gabriel.gwydion.analysis.signature.SignatureTrait
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.unknownReferenceSignatureToType
import me.gabriel.gwydion.frontend.parsing.TraitNode

class TraitAnalyzer: SingleNodeAnalyzer<TraitNode>(TraitNode::class) {
    override fun register(
        block: SymbolBlock,
        node: TraitNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        signatures.traits.add(
            SignatureTrait(
                name = node.name,
                functions = node.functions.map {
                    SignatureFunction(
                        module = block.module,
                        name = it.name,
                        returnType = unknownReferenceSignatureToType(signatures, it.returnType).also { treatedType ->
                            it.returnType = treatedType
                        },
                        parameters = it.parameters.map {
                            unknownReferenceSignatureToType(signatures, it.type).also { treatedType ->
                                it.type = treatedType
                            }
                        },
                        modifiers = emptyList()
                    )
                }
            )
        )
        return block
    }
}