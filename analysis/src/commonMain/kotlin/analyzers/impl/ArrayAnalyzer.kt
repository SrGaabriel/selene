package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisError
import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.analysis.util.doesProvidedTypeAccordToExpectedType
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.ArrayNode
import me.gabriel.selene.frontend.workingBase

class ArrayAnalyzer: SingleNodeAnalyzer<ArrayNode>(ArrayNode::class) {
    override fun register(
        block: SymbolBlock,
        node: ArrayNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val baseType = node.elements.firstOrNull()?.let {
            block.resolveExpression(it)
        } ?: SeleneType.Undefined

        val arrayType = if (node.dynamic) {
            SeleneType.DynamicArray(baseType)
        } else {
            SeleneType.FixedArray(baseType, node.elements.size)
        }

        block.defineSymbol(node, arrayType)

        return block
    }

    override fun analyze(
        block: SymbolBlock,
        node: ArrayNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val arrayType = block.resolveExpression(node)?.workingBase()
            ?: error("Type for ArrayNode was not previously defined")

        val desiredType = when (arrayType) {
            is SeleneType.FixedArray -> arrayType.baseType
            is SeleneType.DynamicArray -> arrayType.baseType
            else -> error("Array type was not previously defined")
        }

        for ((index, element) in (node.elements).withIndex()) {
            val resolved = block.resolveExpression(element)
                ?: error("Element type was not previously defined")
            if (!doesProvidedTypeAccordToExpectedType(
                provided = resolved,
                required = desiredType,
                signatures = signatures
            )) {
                results.errors.add(
                    AnalysisError.ArrayElementTypeMismatch(
                        node = element,
                        expected = desiredType,
                        provided = resolved,
                        index = index
                    )
                )
            }
        }
        return block
    }
}