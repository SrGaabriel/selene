package me.gabriel.selene.analysis.util

import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType

fun doesProvidedTypeAccordToExpectedType(
    provided: SeleneType,
    required: SeleneType,
    signatures: Signatures
): Boolean {
    if (required == SeleneType.Undefined || required == SeleneType.Any) {
        return true
    }
    if (required is SeleneType.Mutable || provided is SeleneType.Mutable) {
        if (provided is SeleneType.Mutable && required !is SeleneType.Mutable) {
            return doesProvidedTypeAccordToExpectedType(required, provided.baseType, signatures)
        } else if (provided !is SeleneType.Mutable) {
            return false
        }
    }
    if (required is SeleneType.Trait && provided is SeleneType.Struct) {
        val trait = signatures.traits.find {
            it.name == required.identifier
        } ?: return false
        return trait.impls.any {
            it.struct == provided.identifier
        }
    }
    return provided == required
}