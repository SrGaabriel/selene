package me.gabriel.selene.backend.common.intrinsic

abstract class IntrinsicFunctionRepository<Context : Any> {
    abstract val intrinsics: MutableMap<String, IntrinsicFunctionExecutor<Context>>

    fun register(name: String, executor: IntrinsicFunctionExecutor<Context>) {
        intrinsics[name] = executor
    }

    fun find(name: String): IntrinsicFunctionExecutor<Context>? {
        return intrinsics[name]
    }

    fun unregister(name: String) {
        intrinsics.remove(name)
    }
}