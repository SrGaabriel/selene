package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.unknownReferenceSignatureToType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.DataFieldNode

class DataFieldAnalyzer: SingleNodeAnalyzer<DataFieldNode>(DataFieldNode::class) {
    override fun register(
        block: SymbolBlock,
        node: DataFieldNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val type = unknownReferenceSignatureToType(
            signatures,
            node.type
        )
        block.defineSymbol(node, type)
        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: DataFieldNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val type = block.resolveExpression(node)
            ?: error("Type for DataFieldNode was not previously defined")

        if (type is GwydionType.Mutable) {
            results.errors.add(
                AnalysisError.StructFieldCannotBeMutable(
                    node = node,
                    name = node.name
                )
            )
        }
        return block
    }
}