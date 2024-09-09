package me.gabriel.selene.backend.llvm.intrinsic

import me.gabriel.ryujin.dsl.ModuleScopeDsl
import me.gabriel.ryujin.struct.DragonType
import me.gabriel.ryujin.struct.NullMemory
import me.gabriel.ryujin.struct.Value
import me.gabriel.ryujin.struct.extractPrimitiveType
import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionExecutor
import me.gabriel.selene.backend.llvm.DragonHookContext

class PrintlnIntrinsicFunctionExecutor : IntrinsicFunctionExecutor<DragonHookContext, ModuleScopeDsl, Value>() {
    override fun onCall(context: DragonHookContext): Value {
        val singleType = context.argumentValues.map { it.type }.distinct().single()
        val (formatName, formatCode) = when (singleType.extractPrimitiveType()) {
            DragonType.Int32 -> "formatln_int32" to "%d\u000A"
            DragonType.Int64 -> "formatln_int64" to "%ld\u000A"
            DragonType.Float32 -> "formatln_float32" to "%f\u000A"
            DragonType.Float64 -> "formatln_float64" to "%lf\u000A"
            DragonType.Int8 -> null to null
            else -> throw IllegalArgumentException("Unsupported type $singleType")
        }
        if (formatName == null || formatCode == null) {
            context.functionDsl.run {
                callExternal(
                    functionName = "puts",
                    returnType = DragonType.Int32,
                    arguments = listOf(context.argumentValues.single()),
                    definition = listOf(DragonType.Pointer(DragonType.Int8)),
                    pure = false
                ).ignore()
            }
            return NullMemory
        }

        context.functionDsl.run {
            val format = useFormat(formatName, formatCode)
            callExternal(
                functionName = "printf",
                returnType = DragonType.Int32,
                arguments = listOf(format) + context.argumentValues,
                definition = listOf(DragonType.Pointer(DragonType.Int8), DragonType.Vararg),
                pure = false
            ).ignore()
        }
        return NullMemory
    }
}