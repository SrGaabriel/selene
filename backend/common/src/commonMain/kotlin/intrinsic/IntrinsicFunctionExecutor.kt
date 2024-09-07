package me.gabriel.selene.backend.common.intrinsic

abstract class IntrinsicFunctionExecutor<T : IntrinsicFunction>(
    val function: T
) {
    abstract fun execute(): String
}