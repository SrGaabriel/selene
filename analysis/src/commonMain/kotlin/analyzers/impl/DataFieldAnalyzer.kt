package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisError
import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.analysis.util.unknownReferenceSignatureToType
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.DataFieldNode

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

        if (type is SeleneType.Mutable) {
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