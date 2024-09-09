package me.gabriel.ryujin.statement

import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Value

class AddStatement(
    val left: Value,
    val right: Value
) : TypedDragonStatement {
    override val memoryDependencies: Set<Value>
        get() = setOf(left, right)

    override fun isValid(): Boolean {
        return left.type == right.type
    }

    override val type: DragonType get() = left.type

    override fun statementLlvm(): String =
        "add ${type.llvm} ${left.llvm()}, ${right.llvm()}"
}
