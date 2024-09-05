package me.gabriel.gwydion.compiler

import me.gabriel.gwydion.compiler.io.IoPlatform
import me.gabriel.gwydion.tools.GwydionLogger

interface GwydionCompilerPlatform {
    val io: IoPlatform
    val logger: GwydionLogger

    fun exitProcess(status: Int): Nothing
}