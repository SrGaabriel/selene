package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.DataStructureReferenceNode

class StructReferenceAnalyzer: SingleNodeAnalyzer<DataStructureReferenceNode>(DataStructureReferenceNode::class) {
    override fun register(
        block: SymbolBlock,
        node: DataStructureReferenceNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val struct = signatures.structs.find {
            it.name == node.name
        } ?: return block

        block.defineSymbol(node, SeleneType.Struct(struct.name, struct.fields))

        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: DataStructureReferenceNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        return block
    }
}