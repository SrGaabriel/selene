package me.gabriel.gwydion.compiler.io

import java.io.File

// TEMPORARY: Currently JVM and LLVM only
object CompilerIOService {
    fun generateExecutable(llvmIr: String, outputDir: String, outputFileName: String) {
        val outputDirectory = File(outputDir)
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }

        val inputLlPath = "$outputDir/$outputFileName.ll"

        File(inputLlPath).delete()
        File(inputLlPath).createNewFile()
        File(inputLlPath).writeText(llvmIr)
    }
}