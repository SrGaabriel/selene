package me.gabriel.gwydion.llvm.struct

class TraitObject(
    val prefix: String = PREFIX,
    val register: Int,
    val name: String,
    val size: Int,
    val alignment: Int,
    val functions: List<String>
) {
    companion object {
        const val PREFIX = "trait_"
    }
}