package me.gabriel.gwydion.ir

import me.gabriel.gwydion.analysis.SymbolRepository
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.parsing.SyntaxTree
import me.gabriel.gwydion.ir.intrinsics.IntrinsicFunction

class LLVMCodeAdapter {
    private val intrinsics = mutableListOf<IntrinsicFunction>()
    private val stdlibDependencies = mutableListOf<String>()

    fun generate(
        module: String,
        tree: SyntaxTree,
        symbols: SymbolRepository,
        signatures: Signatures,
        compileIntrinsics: Boolean
    ): String {
        val process = LLVMCodeAdaptationProcess(module, intrinsics, signatures, stdlibDependencies, compileIntrinsics)
        process.acceptNode(symbols.root, tree.root)
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
}