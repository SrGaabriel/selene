package me.gabriel.selene.ir

import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.llvm.struct.LLVMType

fun SeleneType.asLLVM(): LLVMType = when (this) {
    SeleneType.String -> LLVMType.Pointer(LLVMType.I8)
    SeleneType.Void -> LLVMType.Void
    SeleneType.Any -> LLVMType.I32
    SeleneType.Int8 -> LLVMType.I8
    SeleneType.Int16 -> LLVMType.I16
    SeleneType.Int32 -> LLVMType.I32
    SeleneType.Int64 -> LLVMType.I64
    SeleneType.Float32 -> LLVMType.F32
    SeleneType.Float64 -> LLVMType.F64
    SeleneType.Boolean -> LLVMType.I1
    is SeleneType.FixedArray -> LLVMType.Array(
        type = this.baseType.asLLVM(),
        length = this.length
    )
    is SeleneType.DynamicArray -> LLVMType.Pointer(this.baseType.asLLVM())
    is SeleneType.Struct -> LLVMType.Struct(
        name = this.identifier,
        fields = this.fields.mapValues { getProperReturnType(it.value.asLLVM()) }
    )
    is SeleneType.Mutable -> this.baseType.asLLVM()
    is SeleneType.Lambda -> LLVMType.Ptr
    else -> error("Unsupported LLVM type $this")
}

fun getProperReturnType(returnType: SeleneType): LLVMType =
    getProperReturnType(returnType.asLLVM())

fun getProperReturnType(returnType: LLVMType): LLVMType {
    return when (returnType) {
        is LLVMType.Struct, is LLVMType.Array -> LLVMType.Pointer(returnType)
        else -> returnType
    }
}