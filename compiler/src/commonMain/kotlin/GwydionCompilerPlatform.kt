package me.gabriel.gwydion.compiler

import me.gabriel.gwydion.compiler.cli.CommandLine
import me.gabriel.gwydion.compiler.io.IoPlatform
import me.gabriel.gwydion.tools.GwydionLogger

interface GwydionCompilerPlatform {
    val io: IoPlatform
    val cli: CommandLine
    val logger: GwydionLogger

    fun getCurrentTimeMillis(): Long

    fun exitProcess(status: Int): Nothing
}