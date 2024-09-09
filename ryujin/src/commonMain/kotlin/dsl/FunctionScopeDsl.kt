package me.gabriel.ryujin.dsl

import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.statement.*
import me.gabriel.ryujin.struct.Constant
import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Memory
import me.gabriel.ryujin.struct.Value

class FunctionScopeDsl(
    val module: ModuleScopeDsl,
    val function: DragonFunction
) {
    var register = function.parameters.size.coerceAtLeast(1)

    private val memoryAssignments = mutableMapOf<DragonStatement, Memory>()

    fun statement(
        statement: DragonStatement
    ) {
        if (!statement.isValid()) {
            throw IllegalArgumentException("Invalid statement: $statement")
        }
        register++
        function.statements.add(statement)
    }

    fun `return`(value: Value) =
        statement(ReturnStatement(value))

    fun assignment(target: Memory, value: TypedDragonStatement) {
        statement(AssignStatement(target, value))
    }

    fun add(left: Value, right: Value): AddStatement =
        AddStatement(left, right)

    fun call(
        functionName: String,
        returnType: DragonType,
        arguments: Collection<Value>,
        pure: Boolean
    ): CallStatement =
        CallStatement(functionName, returnType, arguments, pure)

    fun callExternal(
        functionName: String,
        returnType: DragonType,
        arguments: Collection<Value>,
        definition: Collection<DragonType> = arguments.map { it.type },
        pure: Boolean
    ): CallStatement {
        module.dependOnFunction(
            name = functionName,
            returnType = returnType,
            parameters = definition
        )
        return call(functionName, returnType, arguments, pure)
    }

    fun useFormat(
        name: String,
        format: String
    ): GetElementPointerStatement {
        val format = module.format(name, format)
        return getElementAt(
            structure = format,
            elementType = DragonType.Int8,
            index = Constant.Number("0", DragonType.Int32),
            total = true,
            inbounds = false
        )
    }

    fun allocate(
        type: DragonType,
        alignment: Int = type.defaultAlignment
    ): Memory =
        AllocateStatement(type, alignment).assign()

    fun buildString(
        text: String
    ): Memory {
        val allocated = allocate(DragonType.Int8, text.length + 1)
        storeAll(text.map {
            Constant.Number(it.code.toString(), DragonType.Int8)
        }, allocated)
        return allocated
    }

    fun getElementAt(
        structure: Value,
        elementType: DragonType,
        index: Value,
        total: Boolean = true,
        inbounds: Boolean = true
    ): GetElementPointerStatement {
        return GetElementPointerStatement(
            struct = structure,
            elementType = elementType,
            index = index,
            total = total,
            inbounds = inbounds
        )
    }

    fun store(value: Value, target: Value) {
        statement(StoreStatement(value, target))
    }

    fun storeAll(values: Collection<Value>, target: Value) {
        statement(CompositeStoreStatement(values, target))
    }

    fun load(target: Memory): LoadStatement =
        LoadStatement(target)

    fun assign(constantOverride: Boolean, value: (FunctionScopeDsl) -> TypedDragonStatement): Memory =
        value(this).assign(constantOverride = constantOverride)

    fun TypedDragonStatement.ignore() =
        statement(this)

    fun TypedDragonStatement.assign(constantOverride: Boolean? = null): Memory {
        val constant: Boolean = constantOverride ?: (
            this is CallStatement && this.pure
        )

        val memory = Memory.Sized(
            type = type,
            size = type.size,
            register = register
        )
        val statement = AssignStatement(memory, this)
        if (constant) {
            val preloadedMemory = memoryAssignments[statement.value]
            if (preloadedMemory != null) {
                return preloadedMemory
            }
        }
        memoryAssignments[statement.value] = memory
        statement(statement)
        return memory
    }
}