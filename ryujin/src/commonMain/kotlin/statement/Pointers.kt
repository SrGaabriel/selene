package me.gabriel.ryujin.statement

import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Memory
import me.gabriel.ryujin.struct.Value
import me.gabriel.ryujin.struct.descendOneLevel

class StoreStatement(
    val value: Value,
    val target: Memory
): DragonStatement {
    override val memoryDependencies: Set<Value> = setOf(value, target)

    override fun llvm(): String =
        "store ${value.type.llvm} ${value.llvm()}, ${target.type.llvm} ${target.llvm()}"
}

class GetElementPointerStatement(
    val struct: Memory,
    override val type: DragonType,
    val index: Value,
    val total: Boolean = true,
    val inbounds: Boolean = true
): TypedDragonStatement {
    override val memoryDependencies: Set<Value> = setOf(index, struct)

    val originalType = if (struct.type !is DragonType.Pointer) {
        struct.type
    } else {
        (struct.type as DragonType.Pointer).type
    }
    val pointerType = DragonType.Pointer(originalType)

    override fun llvm(): String =
        "getelementptr " +
        (if (inbounds) {
            "inbounds "
        } else {
            ""
        }) +
        "${originalType.llvm}, ${pointerType.llvm} ${struct.llvm()}" + (if (total) {
            ", i32 0"
        } else {
            ""
        }) + ", i32 ${index.llvm()}"
}

class LoadStatement(
    val target: Memory
): TypedDragonStatement {
    override val memoryDependencies: Set<Value> = setOf(target)

    override fun isValid(): Boolean =
        target.type is DragonType.Pointer

    override val type: DragonType = target.type.descendOneLevel()

    override fun llvm(): String {
        require(isValid()) { "Cannot load from non-pointer type" }
        return "load ${type.llvm}, ${target.type.llvm} ${target.llvm()}"
    }
}

