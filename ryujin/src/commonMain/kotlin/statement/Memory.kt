package me.gabriel.ryujin.statement

import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Value

class AllocateStatement(
    val allocationType: DragonType,
    val alignment: Int
) : TypedDragonStatement {
    override val memoryDependencies: Set<Value> = setOf()

    override val type: DragonType = DragonType.Pointer(allocationType)

    override fun statementLlvm(): String {
        return "alloca ${type.llvm}, align $alignment"
    }
}

class StoreStatement(
    val value: Value,
    val target: Value
) : TypedDragonStatement {
    override val memoryDependencies: Set<Value> = setOf(value, target)

    override val type: DragonType = DragonType.Void

    override fun isValid(): Boolean {
        return target.type is DragonType.Pointer
    }

    override fun statementLlvm(): String {
        return "store ${value.type.llvm} ${value.llvm()}, ${target.type.llvm} ${target.llvm()}"
    }
}

class CompositeStoreStatement(
    val values: Collection<Value>,
    val target: Value
) : TypedDragonStatement {
    override val memoryDependencies: Set<Value> = setOf(target) + values

    override val type: DragonType = DragonType.Void
    private val uniqueType = values.asSequence().map { it.type }.distinct().singleOrNull()

    override fun isValid(): Boolean {
        return uniqueType != null || (target.type as? DragonType.Pointer)?.type == uniqueType
    }

    override fun statementLlvm(): String {
        return "store ${uniqueType!!.llvm} ${values.joinToString(
            prefix = "[",
            separator = ", ",
            postfix = "]",
        ) { it.llvm() }}, ${target.type.llvm} ${target.llvm()}"
    }
}