package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.doesProvidedTypeAccordToExpectedType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.ArrayNode
import me.gabriel.gwydion.frontend.workingBase

class ArrayAnalyzer: SingleNodeAnalyzer<ArrayNode>(ArrayNode::class) {
    override fun register(
        block: SymbolBlock,
        node: ArrayNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        val baseType = node.elements.firstOrNull()?.let {
            block.resolveExpression(it)
        } ?: GwydionType.Unknown

        val arrayType = if (node.dynamic) {
            GwydionType.DynamicArray(baseType)
        } else {
            GwydionType.FixedArray(baseType, node.elements.size)
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
            is GwydionType.FixedArray -> arrayType.baseType
            is GwydionType.DynamicArray -> arrayType.baseType
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