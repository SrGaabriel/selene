package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.llvm.struct.LLVMType
import me.gabriel.gwydion.parsing.Type

fun Type.asLLVM(): LLVMType = when (this) {
    Type.String -> LLVMType.Pointer(LLVMType.I8)
    Type.Void -> LLVMType.Void
    Type.Any -> LLVMType.I32
    Type.Int8 -> LLVMType.I8
    Type.Int16 -> LLVMType.I16
    Type.Int32 -> LLVMType.I32
    Type.Int64 -> LLVMType.I64
    Type.Float32 -> LLVMType.F32
    Type.Float64 -> LLVMType.F64
    Type.Boolean -> LLVMType.I1
    is Type.FixedArray -> LLVMType.Array(
        type = this.type.asLLVM(),
        length = this.length
    )
    is Type.DynamicArray -> LLVMType.Pointer(this.type.asLLVM())
    is Type.Struct -> LLVMType.Struct(
        name = this.identifier,
        fields = this.fields.mapValues { it.value.asLLVM() }
    )
    is Type.Mutable -> this.baseType.asLLVM()
    else -> error("Unsupported LLVM type $this")
}