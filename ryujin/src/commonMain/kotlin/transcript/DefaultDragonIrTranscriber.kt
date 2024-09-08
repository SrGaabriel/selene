package me.gabriel.ryujin.transcript

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.function.DragonFunction
import me.gabriel.ryujin.struct.Dependency

class DefaultDragonIrTranscriber: DragonIrTranscriber {
    override fun transcribe(module: DragonModule): String {
        val dependencies = module.dependencies.joinToString("\n") { transcribeDependency(it) }
        val functions = module.functions.joinToString("\n") { transcribeFunction(it) }

        return """
            |$dependencies
            |
            |$functions
        """.trimMargin(marginPrefix = "|")
    }

    override fun transcribeDependency(dependency: Dependency): String {
        return when (dependency) {
            is Dependency.Constant -> transcribeConstantDependency(dependency)
            is Dependency.Struct -> transcribeStructDependency(dependency)
            is Dependency.Function -> transcribeFunctionDependency(dependency)
        }
    }

    override fun transcribeFunction(function: DragonFunction): String {
        return buildString {
            append("define ${function.returnType.llvm} @${function.name}(")
            append(function.parameters.joinToString(", ") { "${it.type.llvm} %${it.register}" })
            append(") {\n")
            function.statements.forEach { append("  ${it.llvm()}\n") }
            append("}")
        }
    }

    private fun transcribeConstantDependency(dependency: Dependency.Constant): String {
        return """
            |@${dependency.name} = unnamed_addr constant ${dependency.value.type.llvm} ${dependency.value.llvm()}
        """.trimMargin(marginPrefix = "|")
    }

    private fun transcribeStructDependency(dependency: Dependency.Struct): String {
        return """
            |%${dependency.name} = type { ${dependency.types.joinToString(", ") {it.llvm}} }
        """.trimMargin(marginPrefix = "|")
    }

    private fun transcribeFunctionDependency(dependency: Dependency.Function): String {
        return """
            |declare ${dependency.returnType.llvm} @${dependency.name}(${dependency.parameters.joinToString(", ") {it.llvm}})
        """.trimMargin(marginPrefix = "|")
    }
}