package me.gabriel.ryujin.statement

import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Value

data class CallStatement(
    val functionName: String,
    override val type: DragonType,
    val arguments: Collection<Value>,
    val pure: Boolean
) : TypedDragonStatement {
    override val memoryDependencies: Set<Value> = arguments.toSet()

    override fun statementLlvm(): String {
        return "call ${type.llvm} @${functionName}(${arguments.joinToString { "${it.type.llvm} ${it.llvm()}" }})"
    }
}