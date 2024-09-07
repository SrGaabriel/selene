package me.gabriel.selene.backend.common

interface SeleneCompilerBackend {
    fun compile(
        module: SeleneCompilerModule
    ): String
}