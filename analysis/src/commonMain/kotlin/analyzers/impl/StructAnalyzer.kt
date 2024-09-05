package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.SignatureStruct
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.analysis.util.unknownReferenceSignatureToType
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.DataStructureNode

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
                module = block.module,
                name = node.name,
                fields = fields
            )
        )
        block.defineSymbol(node, SeleneType.Struct(
            identifier = node.name,
            fields = fields
        ))
        // TODO: Implement struct block to replace `self`
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: DataStructureNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        return block
    }
}