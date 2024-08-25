package me.gabriel.gwydion.analysis.util

import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.signature.SignatureFunction
import me.gabriel.gwydion.analysis.signature.SignatureTrait
import me.gabriel.gwydion.analysis.signature.SignatureTraitImpl
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode
import me.gabriel.gwydion.frontend.workingBase

data class TraitFunctionMetadata(
    val trait: SignatureTrait,
    val impl: SignatureTraitImpl?,
    val function: SignatureFunction,
    val returnType: GwydionType,
    val variableType: GwydionType
)

fun resolveTraitForExpression(
    block: SymbolBlock,
    variable: SyntaxTreeNode,
    signatures: Signatures,
    call: String
): TraitFunctionMetadata? {
    val resolvedVariable = block.resolveExpression(variable) ?: return null

    return signatures.traits.firstNotNullOfOrNull { trait ->
        val impl = if (resolvedVariable !is GwydionType.Trait) trait.impls.firstOrNull {
            it.struct == resolvedVariable.workingBase().signature
        } else null

        if (impl != null || resolvedVariable is GwydionType.Trait) {
            val function = trait.functions.firstOrNull { it.name == call }
            if (function != null) {
                return TraitFunctionMetadata(trait, impl, function, function.returnType, resolvedVariable)
            }
        }
        null
    }
}