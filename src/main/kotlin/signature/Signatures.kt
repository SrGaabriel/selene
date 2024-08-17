package me.gabriel.gwydion.signature

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.gabriel.gwydion.parsing.Type

@Serializable
data class Signatures(
    val traits: MutableSet<SignatureTrait> = mutableSetOf()
) {
    operator fun plus(other: Signatures): Signatures {
        val newTraits = traits.toMutableSet()
        other.traits.forEach { trait ->
            val existingTrait = newTraits.find { it.name == trait.name }
            if (existingTrait != null) {
                existingTrait.impls.addAll(trait.impls)
            } else {
                newTraits.add(trait)
            }
        }
        return Signatures(newTraits)
    }

    inline fun filterTraits(predicate: (SignatureTrait) -> Boolean): Signatures {
        return Signatures(
            traits = traits.filter(predicate).toMutableSet()
        )
    }
}

@Serializable
data class SignatureTrait(
    val name: String,
    val functions: List<SignatureFunction>,
    val impls: MutableSet<SignatureTraitImpl> = mutableSetOf()
)

@Serializable
data class SignatureFunction(
    val name: String,
    val returnType: Type,
    val parameters: List<Type>
)

@Serializable
data class SignatureTraitImpl(
    val struct: String,
    val trait: String,
    var index: Int?,
    var module: String?,
    val types: List<Type>
)