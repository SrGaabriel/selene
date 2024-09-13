package me.gabriel.selene.backend.llvm.session

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.dsl.FunctionScopeDsl
import me.gabriel.ryujin.dsl.ModuleScopeDsl
import me.gabriel.ryujin.dsl.ryujinModule
import me.gabriel.ryujin.struct.Constant
import me.gabriel.ryujin.struct.NullMemory
import me.gabriel.ryujin.struct.Value
import me.gabriel.ryujin.struct.Void
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.backend.common.SeleneCompilerModule
import me.gabriel.selene.backend.common.intrinsic.IntrinsicFunctionRepository
import me.gabriel.selene.backend.llvm.DragonHookContext
import me.gabriel.selene.backend.llvm.util.addPointerToStructs
import me.gabriel.selene.backend.llvm.util.asDragonType
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.*

class SeleneDragonCompilingSession(
    val compilerModule: SeleneCompilerModule,
    val intrinsicFunctionRepository: IntrinsicFunctionRepository<DragonHookContext, ModuleScopeDsl, Value>
) {
    val dsl = ryujinModule {}
    val root = compilerModule.astTree.root

    // todo: remove
    var developmentMode: Boolean = true

    fun compile(): DragonModule {
        return ryujinModule {
            generateModuleLevelDeclarations()
        }
    }

    fun ModuleScopeDsl.generateModuleLevelDeclarations() {
        for (child in root.getChildren()) {
            when (child) {
                is DataStructureNode -> generateStruct(child)
                is FunctionNode -> generateFunction(child)
                is TraitImplNode -> generateTraitImpl(child)
                else -> if (developmentMode) continue else error("Unsupported module-level node: $child")
            }
        }
    }

    fun ModuleScopeDsl.generateFunction(node: FunctionNode) {
        if (node.modifiers.contains(Modifiers.INTRINSIC)) {
            val intrinsic = intrinsicFunctionRepository.find(node.name)
                ?: if (developmentMode) return else error("Intrinsic function not found: ${node.name}")

            intrinsic.setup(this)
            return
        }

        createFunction(
            moduleDsl = this,
            parent = compilerModule.symbols.root,
            node = node
        )
    }

    fun FunctionScopeDsl.generateInstruction(
        block: SymbolBlock,
        node: SyntaxTreeNode,
        statement: Boolean = false
    ): Value? {
        return when (node) {
            is ReturnNode -> generateReturn(block, node)
            is NumberNode -> generateNumber(node)
            is CallNode -> generateCall(block, node, statement)
            is StringNode -> generateString(node)
            else -> error("Unsupported AST tree node instruction: ${node::class.simpleName}")
        }
    }

    fun FunctionScopeDsl.generateReturn(block: SymbolBlock, node: ReturnNode): Value? {
        val value = generateInstruction(block, node.expression)
        `return`(value ?: Void)
        return null
    }

    fun FunctionScopeDsl.generateCall(
        block: SymbolBlock,
        node: CallNode,
        statement: Boolean
    ): Value {
        val signature = compilerModule.signatures
            .functions.find { it.name == node.name } ?: error("Function signature not found: ${node.name}")
        val arguments = node.arguments.map { generateInstruction(block, it, false)!! }

        if (signature.modifiers.contains(Modifiers.INTRINSIC)) {
            val context = DragonHookContext(
                module = module,
                function = function,
                moduleDsl = module,
                functionDsl = this,
                compilingSession = this@SeleneDragonCompilingSession,
                argumentTypes = signature.parameters,
                argumentValues = arguments,
                argumentNodes = node.arguments,
                expectingReturnType = signature.returnType,
                statement = statement
            )
            val intrinsics = intrinsicFunctionRepository.find(node.name)
                ?: error("Intrinsic function not found: ${node.name}")
            return intrinsics.onCall(context)
        }
        val pure = !signature.modifiers.contains(Modifiers.IMPURE)
        val ignoreResult = statement || signature.returnType == SeleneType.Void
        if (pure && ignoreResult) {
            return NullMemory
        }

        val call =
            if (signature.module != compilerModule.name) {
                callExternal(
                    functionName = node.name,
                    returnType = signature.returnType.asDragonType(),
                    arguments = arguments,
                    pure = pure
                )
            } else {
                call(
                    functionName = node.name,
                    returnType = signature.returnType.asDragonType(),
                    arguments = arguments,
                    pure = pure
                )
            }
        if (ignoreResult) {
            call.ignore()
            return NullMemory
        }
        return call.assign()
    }

    fun FunctionScopeDsl.generateNumber(node: NumberNode): Value {
        return Constant.Number(
            type = node.type.asDragonType(),
            value = node.value
        )
    }

    fun FunctionScopeDsl.generateString(node: StringNode): Value {
        val text = (node.segments.single() as StringNode.Segment.Text).text
        val format = useFormat("str_${text.hashCode()}", text)
        return format.assign(constantOverride = true)
    }

    fun ModuleScopeDsl.generateStruct(node: DataStructureNode) {
        val struct = compilerModule.signatures.structs.find { it.name == node.name }
            ?: error("Struct ${node.name} not found in signatures")

        val types = struct.fields.values.map { addPointerToStructs(it.asDragonType()) }

        struct(
            name = node.name,
            types = types
        )
    }

    fun ModuleScopeDsl.generateTraitImpl(node: TraitImplNode): Value? {
        val traitSignature = compilerModule.signatures.traits.find { it.name == node.trait }
            ?: error("Trait ${node.trait} not found in signatures")

        val index = traitSignature.hashCode()
        virtualTable("vtable_trait_$index", node.functions.map {
            Constant.DeclaredConstantPtr(getNameForTraitImplFunction(node, it.name))
        })

        val traitBlock = compilerModule.symbols.root.surfaceSearchChild(node)
            ?: error("Trait ${node.trait} not found in root block")

        for (function in node.functions) {
            generateTraitFunction(traitBlock, node, function)
        }

        return null
    }

    fun ModuleScopeDsl.generateTraitFunction(
        traitBlock: SymbolBlock,
        traitImpl: TraitImplNode,
        functionNode: FunctionNode
    ): Value? {
        createFunction(this, traitBlock, functionNode, getNameForTraitImplFunction(traitImpl, functionNode.name))
        return null
    }

    private fun createFunction(
        moduleDsl: ModuleScopeDsl,
        parent: SymbolBlock,
        node: FunctionNode,
        customName: String? = null
    ) {
        val returnType = addPointerToStructs(node.returnType.asDragonType())
        val parameterTypes = node.parameters.map { addPointerToStructs(it.type.asDragonType()) }

        val block = parent.surfaceSearchChild(node)
            ?: error("Function block not found for function: ${node.name}")

        return moduleDsl.function(
            name = customName ?: node.name,
            returnType = returnType,
            parameters = parameterTypes,
        ) {
            for (instruction in node.body.getChildren()) {
                generateInstruction(block, instruction, true)
            }
        }
    }

    fun getNameForTraitImplFunction(impl: TraitImplNode, functionName: String): String {
        return "${functionName}_for_${impl.hashCode()}"
    }
}