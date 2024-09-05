package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.StructAccessNode
import me.gabriel.selene.frontend.workingBase

class StructAccessAnalyzer: SingleNodeAnalyzer<StructAccessNode>(StructAccessNode::class) {
    override fun register(
        block: SymbolBlock,
        node: StructAccessNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val resolved = block.resolveExpression(node.struct) ?: return block
        val struct: SeleneType.Struct = when (val base = resolved.workingBase()) {
            is SeleneType.Struct -> base
            is SeleneType.Self -> block.self as? SeleneType.Struct ?: return block
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