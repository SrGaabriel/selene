package me.gabriel.gwydion.signature

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Signatures(
    @SerialName("trait_impls")
    val traitImpls: MutableSet<SignatureTraitImpl> = mutableSetOf()
) {
    operator fun plus(other: Signatures): Signatures {
        return Signatures((traitImpls + other.traitImpls).toMutableSet())
    }

    inline fun filter(predicate: (SignatureTraitImpl) -> Boolean): Signatures {
        return Signatures(traitImpls.filter(predicate).toMutableSet())
    }
}

@Serializable
data class SignatureTraitImpl(
    val struct: String,
    val trait: String,
    val index: Int,
    val module: String,
    val types: List<String>
)