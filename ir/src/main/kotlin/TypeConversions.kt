package me.gabriel.gwydion.ir

import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.llvm.struct.LLVMType

fun GwydionType.asLLVM(): LLVMType = when (this) {
    GwydionType.String -> LLVMType.Pointer(LLVMType.I8)
    GwydionType.Void -> LLVMType.Void
    GwydionType.Any -> LLVMType.I32
    GwydionType.Int8 -> LLVMType.I8
    GwydionType.Int16 -> LLVMType.I16
    GwydionType.Int32 -> LLVMType.I32
    GwydionType.Int64 -> LLVMType.I64
    GwydionType.Float32 -> LLVMType.F32
    GwydionType.Float64 -> LLVMType.F64
    GwydionType.Boolean -> LLVMType.I1
    is GwydionType.FixedArray -> LLVMType.Array(
        type = this.baseType.asLLVM(),
        length = this.length
    )
    is GwydionType.DynamicArray -> LLVMType.Pointer(this.baseType.asLLVM())
    is GwydionType.Struct -> LLVMType.Struct(
        name = this.identifier,
        fields = this.fields.mapValues { getProperReturnType(it.value.asLLVM()) }
    )
    is GwydionType.Mutable -> this.baseType.asLLVM()
    is GwydionType.Lambda -> LLVMType.Ptr
    else -> error("Unsupported LLVM type $this")
}

fun getProperReturnType(returnType: GwydionType): LLVMType =
    getProperReturnType(returnType.asLLVM())

fun getProperReturnType(returnType: LLVMType): LLVMType {
    return when (returnType) {
        is LLVMType.Struct, is LLVMType.Array -> LLVMType.Pointer(returnType)
        else -> returnType
    }
}