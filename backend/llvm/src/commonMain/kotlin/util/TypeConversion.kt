package me.gabriel.selene.backend.llvm.util

import me.gabriel.ryujin.struct.DragonType
import me.gabriel.selene.frontend.SeleneType

fun SeleneType.asDragonType(): DragonType = when (this) {
    SeleneType.String -> DragonType.Pointer(DragonType.Int8)
    SeleneType.Void -> DragonType.Void
    SeleneType.Any -> DragonType.Int32
    SeleneType.Int8 -> DragonType.Int8
    SeleneType.Int16 -> DragonType.Int16
    SeleneType.Int32 -> DragonType.Int32
    SeleneType.Int64 -> DragonType.Int64
    SeleneType.Float32 -> DragonType.Float32
    SeleneType.Float64 -> DragonType.Float64
    SeleneType.Boolean -> DragonType.Int1
    is SeleneType.FixedArray -> DragonType.Array(
        type = this.baseType.asDragonType(),
        length = this.length
    )
    is SeleneType.DynamicArray -> DragonType.Pointer(this.baseType.asDragonType())
    is SeleneType.Struct -> DragonType.Struct(
        name = this.identifier,
        types = this.fields.values.map { addPointerToStructs(it.asDragonType()) }
    )
    is SeleneType.Mutable -> this.baseType.asDragonType()
    is SeleneType.Lambda -> DragonType.Ptr
    else -> error("Unsupported LLVM type $this")
}


fun addPointerToStructs(returnType: DragonType): DragonType {
    return when (returnType) {
        is DragonType.Struct, is DragonType.Array -> DragonType.Pointer(returnType)
        else -> returnType
    }
}