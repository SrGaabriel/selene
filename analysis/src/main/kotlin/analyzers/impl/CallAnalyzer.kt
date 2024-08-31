package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.parsing.CallNode
import me.gabriel.gwydion.frontend.parsing.Modifiers

class CallAnalyzer: SingleNodeAnalyzer<CallNode>(CallNode::class) {
    override fun analyze(
        block: SymbolBlock,
        node: CallNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val function = signatures.functions.find { it.name == node.name }
        if (function == null) return@analyze block

        if (function.modifiers.contains(Modifiers.INTERNAL) && block.module != function.module) {
            results.errors.add(AnalysisError.InternalFunctionCall(
                node = node,
                currentModule = block.module,
                functionModule = function.module
            ))
        }
        return block
    }
}