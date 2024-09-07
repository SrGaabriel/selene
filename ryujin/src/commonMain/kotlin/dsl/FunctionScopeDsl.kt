package me.gabriel.ryujin.dsl

import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.statement.*
import me.gabriel.ryujin.struct.Memory
import me.gabriel.ryujin.struct.Value

class FunctionScopeDsl(
    val module: ModuleScopeDsl,
    val function: DragonFunction
) {
    var register = function.parameters.size

    fun statement(statement: DragonStatement) {
        if (!statement.isValid()) {
            throw IllegalArgumentException("Invalid statement: $statement")
        }
        function.statements.add(statement)
    }

    fun `return`(value: Value) =
        statement(ReturnStatement(value))

    fun assignment(target: Memory, value: TypedDragonStatement) {
        statement(AssignStatement(target, value))
    }

    fun add(left: Value, right: Value): AddStatement =
        AddStatement(left, right)

    fun load(target: Memory): LoadStatement =
        LoadStatement(target)

    fun assign(value: (FunctionScopeDsl) -> TypedDragonStatement): Memory =
        value(this).assign()

    fun TypedDragonStatement.ignore() =
        statement(this)

    fun TypedDragonStatement.assign(): Memory {
        val memory = Memory.Sized(
            type = type,
            size = type.size,
            register = register++
        )
        statement(AssignStatement(memory, this))
        return memory
    }
}