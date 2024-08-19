package me.gabriel.gwydion.compiler

import me.gabriel.gwydion.intrinsics.IntrinsicFunction
import me.gabriel.gwydion.parsing.SyntaxTree
import me.gabriel.gwydion.signature.Signatures

interface CodeGenerator {
    fun generate(
        module: String,
        tree: SyntaxTree,
        memory: ProgramMemoryRepository,
        signatures: Signatures,
        compileIntrinsics: Boolean
    ): String

    fun addStdlibDependency(dependency: String)

    fun registerIntrinsicFunction(vararg functions: IntrinsicFunction)
}