package me.gabriel.gwydion

import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.executor.KotlinCodeExecutor
import me.gabriel.gwydion.log.LogLevel
import me.gabriel.gwydion.log.MordantLogger
import me.gabriel.gwydion.reader.AmbiguousSourceReader

fun main() {
    val logger = MordantLogger()
    val reader = AmbiguousSourceReader(logger)
    val stdlib = reader.read(findStdlib())
    val memory = ProgramMemoryRepository()
    val stdlibCompiled = parse(logger, stdlib, memory) ?: return

    val example = reader.read(readText())
    val compiled = parse(logger, example, memory) ?: return

    stdlibCompiled.join(compiled)
    val executor = KotlinCodeExecutor(stdlibCompiled)
    logger.log(LogLevel.INFO) { +"Now executing the code... Output:" }
    executor.execute()
    logger.log(LogLevel.INFO) { +"The code was executed with exit code 0" }
}