package me.gabriel.ryujin.dsl

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.struct.Dependency
import me.gabriel.ryujin.struct.DragonType

class ModuleScopeDsl: DragonModule {
    override val functions: MutableSet<DragonFunction> = mutableSetOf()
    override val dependencies: MutableSet<Dependency> = mutableSetOf()

    fun function(
        name: String,
        parameters: Map<String, DragonType>,
        returnType: DragonType,
        block: FunctionScopeDsl.() -> Unit
    ) {
        val function = DragonFunction(
            module = this,
            name = name,
            parameters = parameters,
            returnType = returnType
        )
        FunctionScopeDsl(
            module = this,
            function = function
        ).apply(block)
    }
}

