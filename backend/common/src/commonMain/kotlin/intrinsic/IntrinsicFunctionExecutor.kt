package me.gabriel.selene.backend.common.intrinsic

abstract class IntrinsicFunctionExecutor<Context : Any> {
    abstract fun onCall(context: Context)
}