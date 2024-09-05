package me.gabriel.selene.analysis.util

import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.signature.SignatureFunction
import me.gabriel.selene.analysis.signature.SignatureTrait
import me.gabriel.selene.analysis.signature.SignatureTraitImpl
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.DataStructureReferenceNode
import me.gabriel.selene.frontend.parsing.SyntaxTreeNode
import me.gabriel.selene.frontend.parsing.VariableReferenceNode
import me.gabriel.selene.frontend.workingBase

data class TraitFunctionMetadata(
    val trait: SignatureTrait,
    val impl: SignatureTraitImpl?,
    val function: SignatureFunction,
    val returnType: SeleneType,
    val variableType: SeleneType
)

fun resolveTraitForExpression(
    block: SymbolBlock,
    variable: SyntaxTreeNode,
    signatures: Signatures,
    call: String
): TraitFunctionMetadata? {
    val resolvedVariable = when (variable) {
        is DataStructureReferenceNode -> signatures.structs.find {
            it.name == variable.name
        }?.let {
            SeleneType.Struct(
                it.name,
                it.fields
            )
        }
        is VariableReferenceNode -> block.resolveSymbol(variable.name).also {
        }
        else -> null
    } ?: return null

    return signatures.traits.firstNotNullOfOrNull { trait ->
        val impl = if (resolvedVariable !is SeleneType.Trait) trait.impls.firstOrNull {
            it.struct == resolvedVariable.workingBase().signature
        } else null

        if (impl != null || resolvedVariable is SeleneType.Trait) {
            val function = trait.functions.firstOrNull { it.name == call }
            if (function != null) {
                return TraitFunctionMetadata(trait, impl, function, function.returnType, resolvedVariable)
            }
        }
        null
    }
}