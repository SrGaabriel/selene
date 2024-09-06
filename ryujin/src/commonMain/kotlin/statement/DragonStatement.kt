package me.gabriel.ryujin.statement

import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Value

interface DragonStatement {
    val memoryDependencies: Set<Value>

    fun isValid(): Boolean = true

    fun llvm(): String
}

interface TypedDragonStatement : DragonStatement {
    val type: DragonType
}