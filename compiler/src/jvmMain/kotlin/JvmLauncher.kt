package me.gabriel.gwydion.compiler.jvm

import me.gabriel.gwydion.compiler.GwydionCompiler
import me.gabriel.gwydion.compiler.cli.CommandLine
import me.gabriel.gwydion.compiler.log.MordantLogger

fun main(args: Array<String>) {
    val platform = JvmCompilerPlatform(
        logger = MordantLogger()
    )
    val compiler = GwydionCompiler(
        platform = platform,
        cli = CommandLine(args)
    )
    compiler.start()
}