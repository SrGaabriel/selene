package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.SignatureStruct
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.unknownReferenceSignatureToType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.DataStructureNode

class StructAnalyzer: SingleNodeAnalyzer<DataStructureNode>(DataStructureNode::class) {
    override fun register(
        block: SymbolBlock,
        node: DataStructureNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val fields = node.fields.associate { it.name to unknownReferenceSignatureToType(signatures, it.type) }

        signatures.structs.add(
            SignatureStruct(
                module = "TODO",
                name = node.name,
                fields = fields
            )
        )
        block.defineSymbol(node, GwydionType.Struct(
            identifier = node.name,
            fields = fields
        ))
        // TODO: Implement struct block to replace `self`
        return block
    }
}