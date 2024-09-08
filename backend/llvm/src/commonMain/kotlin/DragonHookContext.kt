package me.gabriel.selene.backend.llvm

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.dsl.FunctionScopeDsl
import me.gabriel.ryujin.dsl.ModuleScopeDsl
import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.struct.Value
import me.gabriel.selene.backend.llvm.session.SeleneDragonCompilingSession
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.SyntaxTreeNode

class DragonHookContext(
    val module: DragonModule,
    val function: DragonFunction,
    val moduleDsl: ModuleScopeDsl,
    val functionDsl: FunctionScopeDsl,
    val compilingSession: SeleneDragonCompilingSession,
    val argumentTypes: List<SeleneType>,
    val argumentValues: Collection<Value>,
    val argumentNodes: Collection<SyntaxTreeNode>,
    val expectingReturnType: SeleneType,
    val statement: Boolean
)