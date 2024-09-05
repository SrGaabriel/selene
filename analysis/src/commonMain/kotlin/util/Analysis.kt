package me.gabriel.selene.analysis.util

import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.mapBase

internal fun unknownReferenceSignatureToType(
    signatures: Signatures,
    type: SeleneType,
): SeleneType = type.mapBase { unknown ->
    if (unknown !is SeleneType.UnknownReference)
        return@mapBase unknown

    val struct = signatures.structs.find { it.name == unknown.reference }
    if (struct != null) {
        return@mapBase SeleneType.Struct(struct.name, struct.fields)
    }

    val trait = signatures.traits.find { it.name == unknown.reference }
    if (trait != null) {
        return@mapBase SeleneType.Trait(trait.name, trait.functions.map {
            SeleneType.VirtualFunction(
                it.name,
                it.returnType,
                it.parameters
            )
        })
    }
    unknown
}