package me.gabriel.selene.ir

import me.gabriel.selene.analysis.SymbolRepository
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.parsing.SyntaxTree
import me.gabriel.selene.ir.intrinsics.IntrinsicFunction

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
        process.setup()
        process.acceptNode(symbols.root, tree.root)
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