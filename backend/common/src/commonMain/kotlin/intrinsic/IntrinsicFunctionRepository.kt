package me.gabriel.selene.backend.common.intrinsic

abstract class IntrinsicFunctionRepository<Context : Any, Module : Any, Value : Any> {
    abstract val intrinsics: MutableMap<String, IntrinsicFunctionExecutor<Context, Module, Value>>

    fun register(name: String, executor: IntrinsicFunctionExecutor<Context, Module, Value>) {
        intrinsics[name] = executor
    }

    fun find(name: String): IntrinsicFunctionExecutor<Context, Module, Value>? {
        return intrinsics[name]
    }

    fun unregister(name: String) {
        intrinsics.remove(name)
    }
}