package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.SignatureFunction
import me.gabriel.gwydion.analysis.signature.SignatureTraitImpl
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.unknownReferenceSignatureToType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.FunctionNode
import me.gabriel.gwydion.frontend.parsing.ParameterNode
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

        val newBlock = block.createChild(
            id = node,
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
        val newBlock = block.surfaceSearchChild(node)
            ?: error("Block for trait impl ${node.trait} for ${node.type.signature} was not created")

        val trait = signatures.traits.find { it.name == node.trait }
        if (trait == null) return newBlock

        val missingFunctions = mutableListOf<SignatureFunction>()
        val wrongFunctions = mutableMapOf<FunctionNode, List<GwydionType>>()

        trait.functions.forEach { traitFunction ->
            val implFunction = node.functions.find { it.name == traitFunction.name }
            if (implFunction == null) {
                missingFunctions.add(traitFunction)
            } else {
                if (!doParametersMatch(implFunction.parameters.map { it.type }, traitFunction.parameters)) {
                    wrongFunctions[implFunction] = traitFunction.parameters
                }
            }
        }

        missingFunctions.forEach {
            results.errors.add(
                AnalysisError.MissingFunctionForTraitImpl(
                    trait = node.trait,
                    function = it.name,
                    struct = node.type.signature,
                    node = node
                )
            )
        }

        wrongFunctions.forEach { (functionNode, expectedParams) ->
            results.errors.add(
                AnalysisError.WrongFunctionParametersForTraitImpl(
                    trait = node.trait,
                    function = functionNode.name,
                    struct = node.type.signature,
                    node = functionNode,
                    correct = expectedParams
                )
            )
        }

        return newBlock
    }

    private fun doParametersMatch(provided: List<GwydionType>, required: List<GwydionType>): Boolean {
        if (provided.size != required.size) return false
        return provided.zip(required).all { (provided, required) ->
            provided == required
        }
    }
}