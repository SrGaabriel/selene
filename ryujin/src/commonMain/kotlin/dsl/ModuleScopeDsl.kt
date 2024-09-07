package me.gabriel.ryujin.dsl

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.struct.*

class ModuleScopeDsl: DragonModule {
    override val functions: MutableSet<DragonFunction> = mutableSetOf()
    override val dependencies: MutableSet<Dependency> = mutableSetOf()

    fun function(
        name: String,
        returnType: DragonType,
        parameters: Collection<DragonType> = emptyList(),
        block: FunctionScopeDsl.(Collection<Memory>) -> Unit
    ) {
        val parameterMemoryUnits = parameters.mapIndexed { index, dragonType ->
            Memory.Sized(
                type = dragonType,
                size = dragonType.size,
                register = index
            )
        }

        val function = DragonFunction(
            module = this,
            name = name,
            parameters = parameterMemoryUnits,
            returnType = returnType
        )
        functions.add(function)
        val dsl = FunctionScopeDsl(
            module = this,
            function = function
        )
        dsl.block(parameterMemoryUnits)
    }

    fun virtualTable(
        name: String,
        values: Collection<Value>
    ) {
        dependencies.add(Dependency.Constant(
            name = name,
            value = Constant.VirtualTable(values)
        ))
    }
}

fun ryujinModule(block: ModuleScopeDsl.() -> Unit): ModuleScopeDsl {
    val module = ModuleScopeDsl()
    module.apply(block)
    return module
}