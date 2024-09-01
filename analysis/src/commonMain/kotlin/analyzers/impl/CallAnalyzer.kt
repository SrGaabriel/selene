package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.CallNode
import me.gabriel.gwydion.frontend.parsing.Modifiers

class CallAnalyzer: SingleNodeAnalyzer<CallNode>(CallNode::class) {
    override fun register(
        block: SymbolBlock,
        node: CallNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val potentialLambda = block.resolveSymbol(node.name)
        if (potentialLambda is GwydionType.Lambda) {
            registerCall(block, node, potentialLambda.parameters, visitor)
            block.defineSymbol(node, potentialLambda.returnType)
            return block
        }

        val function = signatures.functions.find { it.name == node.name }
        if (function == null) return block

        registerCall(block, node, function.parameters, visitor)
        block.defineSymbol(node, function.returnType)
        return block
    }

    private fun registerCall(
        block: SymbolBlock,
        node: CallNode,
        parameters: List<GwydionType>,
        visitor: TypeInferenceVisitor
    ) {
        for ((index, argument) in node.arguments.withIndex()) {
            visitor.visit(argument) {
                val type = block.resolveExpression(argument) ?: GwydionType.Unknown
                if (type == GwydionType.Unknown) {
                    val expectedType = parameters.getOrNull(index) ?: GwydionType.Unknown
                    block.defineSymbol(argument, expectedType)
                }
            }
        }
    }

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