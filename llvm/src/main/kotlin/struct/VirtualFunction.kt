package me.gabriel.gwydion.llvm.struct

class VirtualFunction(
    val name: String,
    val arguments: List<LLVMType>,
    val returnType: LLVMType
)