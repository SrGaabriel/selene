package me.gabriel.gwydion.analysis.util

import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.mapBase

internal fun unknownReferenceSignatureToType(
    signatures: Signatures,
    type: GwydionType,
): GwydionType = type.mapBase { unknown ->
    if (unknown !is GwydionType.UnknownReference)
        return@mapBase unknown

    val struct = signatures.structs.find { it.name == unknown.reference }
    if (struct != null) {
        return@mapBase GwydionType.Struct(struct.name, struct.fields)
    }

    val trait = signatures.traits.find { it.name == unknown.reference }
    if (trait != null) {
        return@mapBase GwydionType.Trait(trait.name, trait.functions.map {
            GwydionType.VirtualFunction(
                it.name,
                it.returnType,
                it.parameters
            )
        })
    }
    unknown
}