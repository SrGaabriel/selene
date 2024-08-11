package me.gabriel.gwydion.compiler

import me.gabriel.gwydion.parsing.SyntaxTree

interface CodeGenerator {
    fun generate(tree: SyntaxTree, memory: ProgramMemoryRepository, compileIntrinsics: Boolean): String

    fun addStdlibDependency(dependency: String)

    fun registerIntrinsicFunction(vararg functions: IntrinsicFunction)
}