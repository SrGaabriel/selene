package me.gabriel.selene.compiler.jvm

import me.gabriel.selene.backend.llvm.DragonCompilerBackend
import me.gabriel.selene.compiler.SeleneCompiler
import me.gabriel.selene.compiler.cli.CommandLine
import me.gabriel.selene.compiler.log.MordantLogger

fun main(args: Array<String>) {
    val platform = JvmCompilerPlatform(
        logger = MordantLogger()
    )
    val compiler = SeleneCompiler(
        platform = platform,
        backend = DragonCompilerBackend(),
        cli = CommandLine(args)
    )
    compiler.start()
}