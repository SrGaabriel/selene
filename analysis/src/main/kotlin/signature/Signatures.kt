package me.gabriel.gwydion.analysis.signature

import me.gabriel.gwydion.frontend.Type
import kotlinx.serialization.Serializable

@Serializable
data class Signatures(
    val structs: MutableSet<SignatureStruct> = mutableSetOf(),
    val functions: MutableSet<SignatureFunction> = mutableSetOf(),
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
        val newFunctions = functions.toMutableSet()
        newFunctions.addAll(other.functions)
        val newStructs = structs.toMutableSet()
        newStructs.addAll(other.structs)
        return Signatures(newStructs, newFunctions, newTraits)
    }
}

@Serializable
data class SignatureStruct(
    val name: String,
    val module: String,
    val fields: Map<String, Type>
)

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