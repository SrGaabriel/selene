package me.gabriel.selene.analysis.signature

import me.gabriel.selene.frontend.SeleneType
import kotlinx.serialization.Serializable
import me.gabriel.selene.frontend.parsing.Modifiers

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
    val fields: Map<String, SeleneType>
)

@Serializable
data class SignatureTrait(
    val name: String,
    val functions: List<SignatureFunction>,
    val impls: MutableSet<SignatureTraitImpl> = mutableSetOf()
)

@Serializable
data class SignatureFunction(
    val module: String,
    val name: String,
    val returnType: SeleneType,
    val parameters: List<SeleneType>,
    val modifiers: Set<Modifiers>
)

@Serializable
data class SignatureTraitImpl(
    val struct: String,
    val trait: String,
    var index: Int?,
    var module: String?,
    val types: List<SeleneType>
)