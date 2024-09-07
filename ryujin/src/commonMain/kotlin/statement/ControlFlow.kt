package me.gabriel.ryujin.statement

import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Value

class ReturnStatement(
    val value: Value
) : DragonStatement {
    override val memoryDependencies: Set<Value> = setOf(value)

    override fun llvm(): String {
        if (value.type == DragonType.Void) {
            return "ret void"
        }
        return "ret ${value.type.llvm} ${value.llvm()}"
    }
}