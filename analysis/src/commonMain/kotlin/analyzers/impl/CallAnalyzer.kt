package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisError
import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.CallNode
import me.gabriel.selene.frontend.parsing.Modifiers

class CallAnalyzer: SingleNodeAnalyzer<CallNode>(CallNode::class) {
    override fun register(
        block: SymbolBlock,
        node: CallNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val potentialLambda = block.resolveSymbol(node.name)
        if (potentialLambda is SeleneType.Lambda) {
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
        parameters: List<SeleneType>,
        visitor: TypeInferenceVisitor
    ) {
        for ((index, argument) in node.arguments.withIndex()) {
            visitor.visit(argument) {
                val type = block.resolveExpression(argument) ?: SeleneType.Unknown
                if (type == SeleneType.Unknown) {
                    val expectedType = parameters.getOrNull(index) ?: SeleneType.Unknown
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
        if (function == null) {
            val potentialLambda = block.resolveSymbol(node.name)
            if (potentialLambda !is SeleneType.Lambda) {
                results.errors.add(AnalysisError.UndefinedFunction(
                    node = node,
                ))
                return block
            }
            return@analyze block
        }

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