package me.gabriel.selene.backend.common

import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionRepository

interface SeleneCompilerBackend<Context : Any> {
    val intrinsics: IntrinsicFunctionRepository<Context>

    fun compile(
        module: SeleneCompilerModule
    ): String
}