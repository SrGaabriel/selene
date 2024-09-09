package me.gabriel.selene.backend.llvm.intrinsic

import me.gabriel.ryujin.dsl.ModuleScopeDsl
import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.Value
import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionExecutor
import me.gabriel.selene.backend.llvm.DragonHookContext

class ReadlnIntrinsicFunctionExecutor: IntrinsicFunctionExecutor<DragonHookContext, ModuleScopeDsl, Value>() {
    override fun onCall(context: DragonHookContext): Value {
        TODO("Not yet implemented")
    }

    override fun setup(module: ModuleScopeDsl) {
        module.run {
            function(
                name = "readln",
                returnType = DragonType.Pointer(DragonType.Int8),
                parameters = emptyList()
            ) {
            }
        }
    }
}