package me.gabriel.selene.backend.common.intrinsic

abstract class IntrinsicFunctionExecutor<Context : Any, Value : Any> {
    abstract fun onCall(context: Context): Value
}