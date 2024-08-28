package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.StructAccessNode
import me.gabriel.gwydion.frontend.workingBase

class StructAccessAnalyzer: SingleNodeAnalyzer<StructAccessNode>(StructAccessNode::class) {
    override fun register(
        block: SymbolBlock,
        node: StructAccessNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val resolved = block.resolveExpression(node.struct) ?: return block
        val struct: GwydionType.Struct = when (val base = resolved.workingBase()) {
            is GwydionType.Struct -> base
            is GwydionType.Self -> block.self as? GwydionType.Struct ?: return block
            else -> return block
        }

        val field = struct.fields[node.field] ?: return block

        block.defineSymbol(node, field)

        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: StructAccessNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        return block
    }
}