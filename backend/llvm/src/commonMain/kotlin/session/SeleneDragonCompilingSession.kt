package me.gabriel.selene.backend.llvm.session

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.dsl.FunctionScopeDsl
import me.gabriel.ryujin.dsl.ModuleScopeDsl
import me.gabriel.ryujin.dsl.ryujinModule
import me.gabriel.ryujin.struct.Constant
import me.gabriel.ryujin.struct.Value
import me.gabriel.ryujin.struct.Void
import me.gabriel.selene.backend.common.SeleneCompilerModule
import me.gabriel.selene.backend.llvm.util.addPointerToStructs
import me.gabriel.selene.backend.llvm.util.asDragonType
import me.gabriel.selene.frontend.parsing.FunctionNode
import me.gabriel.selene.frontend.parsing.NumberNode
import me.gabriel.selene.frontend.parsing.ReturnNode
import me.gabriel.selene.frontend.parsing.SyntaxTreeNode

class SeleneDragonCompilingSession(
    val module: SeleneCompilerModule
) {
    private val dsl = ryujinModule {}
    private val root = module.astTree.root

    // todo: remove
    var developmentMode: Boolean = true

    fun compile(): DragonModule {
        return ryujinModule {
            generateModuleLevelDeclarations()
        }
    }

    private fun ModuleScopeDsl.generateModuleLevelDeclarations() {
        for (child in root.getChildren()) {
            when (child) {
                is FunctionNode -> generateFunction(child)
                else -> if (developmentMode) continue else error("Unsupported module-level node: $child")
            }
        }
    }

    private fun ModuleScopeDsl.generateFunction(node: FunctionNode) {
        val returnType = addPointerToStructs(node.returnType.asDragonType())
        val parameterTypes = node.parameters.map { addPointerToStructs(it.type.asDragonType()) }

        function(
            name = node.name,
            returnType = returnType,
            parameters = parameterTypes,
        ) {
            for (instruction in node.body.getChildren()) {
                generateInstruction(instruction)
            }
        }
    }

    private fun FunctionScopeDsl.generateInstruction(node: SyntaxTreeNode): Value? {
        return when (node) {
            is ReturnNode -> generateReturn(node)
            is NumberNode -> generateNumber(node)
            else -> error("Unsupported AST tree node instruction: ${node::class.simpleName}")
        }
    }

    private fun FunctionScopeDsl.generateReturn(node: ReturnNode): Value? {
        val value = generateInstruction(node.expression)
        `return`(value ?: Void)
        return null
    }

    private fun FunctionScopeDsl.generateNumber(node: NumberNode): Value {
        return Constant.Number(
            type = node.type.asDragonType(),
            value = node.value
        )
    }
}