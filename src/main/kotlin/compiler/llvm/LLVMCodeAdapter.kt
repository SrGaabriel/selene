package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.compiler.CodeGenerator
import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.compiler.IntrinsicFunction
import me.gabriel.gwydion.parsing.SyntaxTree
import java.io.File

class LLVMCodeAdapter: CodeGenerator {
    private val intrinsics = mutableListOf<IntrinsicFunction>()

    override fun generate(tree: SyntaxTree, memory: ProgramMemoryRepository): String {
        val process = LLVMCodeAdaptationProcess(tree, memory, intrinsics)
        process.acceptNode(memory.root, tree.root)
        process.setup()
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
        File(inputLlPath).delete()
        File(inputLlPath).writeText(llvmIr)

        val clangProcess = ProcessBuilder(
            "clang",
            inputLlPath,
            "-o",
            outputExePath
        )
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        clangProcess.waitFor()
    }

    companion object {
        val VACUUM = File(
            if (System.getProperty("os.name").startsWith("Windows"))
                "NUL"
            else
                "/dev/null")
    }
}