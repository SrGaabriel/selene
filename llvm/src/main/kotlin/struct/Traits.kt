package me.gabriel.gwydion.llvm.struct

class TraitObject(
    val prefix: String,
    val size: Int,
    val alignment: Int,
    val functions: List<String>
)