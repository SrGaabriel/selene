package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.analyzer.SymbolTable
import me.gabriel.gwydion.compiler.CodeGenerator
import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.executor.IntrinsicFunction
import me.gabriel.gwydion.parsing.SyntaxTree
import java.io.File

class LLVMCodeGenerator: CodeGenerator {
    private val intrinsics = mutableListOf<IntrinsicFunction>()

    override fun generate(tree: SyntaxTree, memory: ProgramMemoryRepository): String {
        val process = LLVMCodeGeneratorProcess(tree, memory, intrinsics)
        process.setup()
        process.generateNode(tree.root, memory.root)
        return process.finish()
    }

    override fun registerIntrinsicFunction(vararg functions: IntrinsicFunction) {
        intrinsics.addAll(functions)
    }

    fun generateExecutable(llvmIr: String, outputDir: String, outputFileName: String) {
        val outputDirectory = File(outputDir)
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }

        val inputLlPath = "$outputDir/input.ll"
        val outputExePath = "$outputDir/$outputFileName"

        File(outputExePath).delete()
        File(inputLlPath).writeText(llvmIr)

        val clangProcess = ProcessBuilder("clang", inputLlPath, "", "-o", outputExePath)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        clangProcess.waitFor()

        File(inputLlPath).delete()
    }
}