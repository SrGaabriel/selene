package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.analyzer.SymbolTable
import me.gabriel.gwydion.compiler.CodeGenerator
import me.gabriel.gwydion.executor.IntrinsicFunction
import me.gabriel.gwydion.parsing.SyntaxTree
import java.io.File

class LLVMCodeGenerator: CodeGenerator {
    private val intrinsics = mutableListOf<IntrinsicFunction>()

    override fun generate(tree: SyntaxTree, symbols: SymbolTable): String {
        val process = LLVMCodeGeneratorProcess(tree, symbols, intrinsics)
        process.setup()
        process.generateNode(tree.root)
        return process.finish()
    }

    override fun registerIntrinsicFunction(function: IntrinsicFunction) {
        intrinsics.add(function)
    }

    fun generateExecutable(llvmIr: String, outputDir: String, outputFileName: String) {
        // Create the output directory if it doesn't exist
        val outputDirectory = File(outputDir)
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }

        // Define file paths
        val inputLlPath = "$outputDir/input.ll"
        val outputExePath = "$outputDir/$outputFileName"

        // Write the LLVM IR to a file
        File(inputLlPath).writeText(llvmIr)

        // Compile and link using clang
        val clangProcess = ProcessBuilder("clang", inputLlPath, "-v", "-o", outputExePath)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        clangProcess.waitFor()

        // Clean up temporary files
        File(inputLlPath).delete()
    }
}