package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.llvm.struct.LLVMType
import me.gabriel.gwydion.parsing.Type

fun Type.asLLVM(): LLVMType = when (this) {
    Type.String -> LLVMType.Pointer(LLVMType.I8)
    Type.Void -> LLVMType.Void
    Type.Any -> LLVMType.I32
    Type.Int32 -> LLVMType.I32
    else -> error("Unsupported LLVM type $this")
}