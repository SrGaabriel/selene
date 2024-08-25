package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.signature.SignatureStruct
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.unknownReferenceSignatureToType
import me.gabriel.gwydion.frontend.parsing.DataStructureNode

class StructAnalyzer: SingleNodeAnalyzer<DataStructureNode>(DataStructureNode::class) {
    override fun register(block: SymbolBlock, node: DataStructureNode, signatures: Signatures): SymbolBlock {
        signatures.structs.add(
            SignatureStruct(
                module = "TODO",
                name = node.name,
                fields = node.fields.associate { it.name to unknownReferenceSignatureToType(signatures, it.type) }
            )
        )
        // TODO: Implement struct block to replace `self`
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: DataStructureNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val structBlock =
            block.surfaceSearchChild(node.name) ?: error("Block for struct ${node.name} was not created")
        return structBlock
    }
}