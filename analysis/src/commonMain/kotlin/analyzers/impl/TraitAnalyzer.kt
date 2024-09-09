package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.SignatureFunction
import me.gabriel.selene.analysis.signature.SignatureTrait
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.analysis.util.unknownReferenceSignatureToType
import me.gabriel.selene.frontend.parsing.TraitNode

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
                        modifiers = emptySet()
                    )
                }
            )
        )
        return block
    }
}