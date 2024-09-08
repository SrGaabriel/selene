package me.gabriel.selene.backend.common

import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionRepository

interface SeleneCompilerBackend<Context : Any, Value : Any> {
    val intrinsics: IntrinsicFunctionRepository<Context, Value>

    fun compile(
        module: SeleneCompilerModule
    ): String
}