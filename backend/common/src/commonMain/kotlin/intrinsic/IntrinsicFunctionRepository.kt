package me.gabriel.selene.backend.common.intrinsic

abstract class IntrinsicFunctionRepository<Context : Any, Value : Any> {
    abstract val intrinsics: MutableMap<String, IntrinsicFunctionExecutor<Context, Value>>

    fun register(name: String, executor: IntrinsicFunctionExecutor<Context, Value>) {
        intrinsics[name] = executor
    }

    fun find(name: String): IntrinsicFunctionExecutor<Context, Value>? {
        return intrinsics[name]
    }

    fun unregister(name: String) {
        intrinsics.remove(name)
    }
}