package me.gabriel.ryujin.statement

import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Value

class CallStatement(
    val functionName: String,
    override val type: DragonType,
    val arguments: Collection<Value>
) : TypedDragonStatement {
    override val memoryDependencies: Set<Value> = arguments.toSet()

    override fun llvm(): String {
        return "call ${type.llvm} @${functionName}(${arguments.joinToString { "${it.type.llvm} ${it.llvm()}" }})"
    }
}