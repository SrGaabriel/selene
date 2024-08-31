package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.DataStructureReferenceNode

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

        block.defineSymbol(node, GwydionType.Struct(struct.name, struct.fields))

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