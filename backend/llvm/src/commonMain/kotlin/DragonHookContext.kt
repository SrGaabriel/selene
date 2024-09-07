package me.gabriel.selene.backend.llvm

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.dsl.FunctionScopeDsl
import me.gabriel.ryujin.dsl.ModuleScopeDsl
import me.gabriel.ryujin.function.DragonFunction

class DragonHookContext(
    val module: DragonModule,
    val function: DragonFunction,
    val moduleDsl: ModuleScopeDsl,
    val functionDsl: FunctionScopeDsl
)