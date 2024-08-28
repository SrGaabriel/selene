package me.gabriel.gwydion.analysis.util

import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType

fun doesProvidedTypeAccordToExpectedType(
    provided: GwydionType,
    required: GwydionType,
    signatures: Signatures
): Boolean {
    if (required == GwydionType.Unknown || required == GwydionType.Any) {
        return true
    }
    if (required is GwydionType.Mutable || provided is GwydionType.Mutable) {
        if (provided is GwydionType.Mutable && required !is GwydionType.Mutable) {
            return doesProvidedTypeAccordToExpectedType(required, provided.baseType, signatures)
        } else if (provided !is GwydionType.Mutable) {
            return false
        }
    }
    if (required is GwydionType.Trait && provided is GwydionType.Struct) {
        val trait = signatures.traits.find {
            it.name == required.identifier
        } ?: return false
        return trait.impls.any {
            it.struct == provided.identifier
        }
    }
    return provided == required
}