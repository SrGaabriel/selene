package me.gabriel.selene.backend.llvm.intrinsic

import me.gabriel.ryujin.struct.Constant
import me.gabriel.ryujin.struct.DragonType
import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionExecutor
import me.gabriel.selene.backend.llvm.DragonHookContext

class PrintlnIntrinsicFunctionExecutor : IntrinsicFunctionExecutor<DragonHookContext>() {
    override fun onCall(context: DragonHookContext) {
        context.functionDsl.run {
            callExternal(
                functionName = "printf",
                returnType = DragonType.Pointer(DragonType.Int8),
                arguments = listOf(
                    Constant.DeclaredConstantPtr("%s\\0A"),
                )
            )
        }
    }
}