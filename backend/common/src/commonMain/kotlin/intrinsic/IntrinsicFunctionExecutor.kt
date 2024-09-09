package me.gabriel.selene.backend.common.intrinsic

abstract class IntrinsicFunctionExecutor<Context : Any, Module : Any, Value : Any> {
    open fun setup(module: Module) {}

    abstract fun onCall(context: Context): Value
}