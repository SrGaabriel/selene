package me.gabriel.gwydion.link

import me.gabriel.gwydion.compiler.llvm.LLVMCodeAdapter
import me.gabriel.gwydion.compiler.llvm.asLLVM
import me.gabriel.gwydion.parsing.DataStructureNode
import me.gabriel.gwydion.parsing.FunctionNode
import me.gabriel.gwydion.parsing.Modifiers
import me.gabriel.gwydion.parsing.SyntaxTree

object StdlibLinker {
    fun link(
        stdlib: SyntaxTree,
        adapter: LLVMCodeAdapter
    ) {
        stdlib.root.getChildren().forEach {
            when (it) {
                is FunctionNode -> {
                    if (it.modifiers.contains(Modifiers.INTRINSIC)) return@forEach
                    adapter.addStdlibDependency("""
                        declare ${it.returnType.asLLVM().llvm} @${it.name}(${it.parameters.joinToString { it.type.asLLVM().llvm }})
                    """.trimIndent())
                }
                is DataStructureNode -> {
                    adapter.addStdlibDependency("""
                        %${it.name} = type { ${it.fields.joinToString { it.type.asLLVM().llvm }} }
                    """.trimIndent())
                }
                else -> {}
            }
        }
    }
}