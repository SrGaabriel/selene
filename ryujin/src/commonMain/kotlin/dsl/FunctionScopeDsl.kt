package me.gabriel.ryujin.dsl

import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.statement.DragonStatement

class FunctionScopeDsl(
    val module: ModuleScopeDsl,
    val function: DragonFunction
) {
    fun statement(statement: DragonStatement) {
        function.statements.add(statement)
    }
}