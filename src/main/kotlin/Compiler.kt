package me.gabriel.gwydion;

import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.compiler.llvm.LLVMCodeAdapter
import me.gabriel.gwydion.executor.PrintFunction
import me.gabriel.gwydion.executor.PrintlnFunction
import me.gabriel.gwydion.executor.ReadlineFunction
import me.gabriel.gwydion.log.LogLevel
import me.gabriel.gwydion.log.MordantLogger
import me.gabriel.gwydion.reader.AmbiguousSourceReader
import java.io.File
import java.time.Instant

fun main() {
    val logger = MordantLogger()
    logger.log(LogLevel.INFO) { +"Starting the Gwydion compiler..." }

    val stdlib = findStdlib()
    val reader = AmbiguousSourceReader(logger)
    val memory = ProgramMemoryRepository()
    val stdlibTree = parse(logger, reader.read(stdlib), memory) ?: return

    val memoryStart = Instant.now()
    val example2 = File("src/main/resources/basic.wy").readText()
    val tree = parse(logger, example2, memory) ?: return
    val llvmCodeAdapter = LLVMCodeAdapter()
    llvmCodeAdapter.registerIntrinsicFunction(
        PrintFunction(),
        PrintlnFunction(),
        ReadlineFunction()
    )
    val memoryEnd = Instant.now()
    val memoryDelay = memoryEnd.toEpochMilli() - memoryStart.toEpochMilli()
    logger.log(LogLevel.INFO) { +"Memory analysis took ${memoryDelay}ms" }

    val generationStart = Instant.now()
    tree.join(stdlibTree)
    val generated = llvmCodeAdapter.generate(tree, memory)
    val generationEnd = Instant.now()
    val generationDelay = generationEnd.toEpochMilli() - generationStart.toEpochMilli()
    logger.log(LogLevel.INFO) { +"Code generation took ${generationDelay}ms" }
    println(generated)
    val compilingStart = Instant.now()
    llvmCodeAdapter.generateExecutable(
        llvmIr = generated,
        outputDir = "xscales",
        outputFileName = "output.exe"
    )
    val executionEnd = Instant.now()
    val executionDelay = executionEnd.toEpochMilli() - compilingStart.toEpochMilli()
    logger.log(LogLevel.INFO) { +"Compiling took ${executionDelay}ms" }

    logger.log(LogLevel.INFO) { +"The total time to generate and compile the code was ${executionDelay + generationDelay + memoryDelay}ms" }
    return
}

fun readText(): File {
    return File("src/main/resources")
}