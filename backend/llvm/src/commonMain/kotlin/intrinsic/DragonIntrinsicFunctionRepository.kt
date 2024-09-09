package me.gabriel.selene.backend.llvm.intrinsic

import me.gabriel.ryujin.dsl.ModuleScopeDsl
import me.gabriel.ryujin.struct.Value
import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionExecutor
import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionRepository
import me.gabriel.selene.backend.llvm.DragonHookContext

class DragonIntrinsicFunctionRepository: IntrinsicFunctionRepository<DragonHookContext, ModuleScopeDsl, Value>() {
    override val intrinsics: MutableMap<String, IntrinsicFunctionExecutor<DragonHookContext, ModuleScopeDsl, Value>> = mutableMapOf(
        "println" to PrintlnIntrinsicFunctionExecutor()
    )
}