package me.gabriel.gwydion.ir

import me.gabriel.gwydion.analysis.ProgramMemoryRepository
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.parsing.SyntaxTree
import me.gabriel.gwydion.ir.intrinsics.IntrinsicFunction
import java.io.File

class LLVMCodeAdapter {
    private val intrinsics = mutableListOf<IntrinsicFunction>()
    private val stdlibDependencies = mutableListOf<String>()

    fun generate(
        module: String,
        tree: SyntaxTree,
        memory: ProgramMemoryRepository,
        signatures: Signatures,
        compileIntrinsics: Boolean
    ): String {
        val process = LLVMCodeAdaptationProcess(module, intrinsics, signatures, stdlibDependencies, compileIntrinsics)
        process.acceptNode(memory.root, tree.root)
        process.setup()
        process.finish()
        return process.getGeneratedCode()
    }

    fun addStdlibDependency(dependency: String) {
        stdlibDependencies.add(dependency)
    }

    fun registerIntrinsicFunction(vararg functions: IntrinsicFunction) {
        intrinsics.addAll(functions)
    }

    fun generateExecutable(llvmIr: String, outputDir: String, outputFileName: String) {
        val outputDirectory = File(outputDir)
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }

        val inputLlPath = "$outputDir/$outputFileName.ll"
//        val outputExePath = "$outputDir/$outputFileName"

//        File(outputExePath).delete()
        File(inputLlPath).delete()
        File(inputLlPath).createNewFile()
        File(inputLlPath).writeText(llvmIr)

//        val clangProcess = ProcessBuilder(
//            "clang",
//            inputLlPath,
//            "-o",
//            outputExePath
//        )
//            .redirectError(ProcessBuilder.Redirect.INHERIT)
//            .start()
//        clangProcess.waitFor()
    }
}