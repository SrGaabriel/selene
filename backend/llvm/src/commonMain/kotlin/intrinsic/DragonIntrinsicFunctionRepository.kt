package me.gabriel.selene.backend.llvm.intrinsic

import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionExecutor
import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionRepository
import me.gabriel.selene.backend.llvm.DragonHookContext

class DragonIntrinsicFunctionRepository: IntrinsicFunctionRepository<DragonHookContext>() {
    override val intrinsics: MutableMap<String, IntrinsicFunctionExecutor<DragonHookContext>> = mutableMapOf(

    )
}