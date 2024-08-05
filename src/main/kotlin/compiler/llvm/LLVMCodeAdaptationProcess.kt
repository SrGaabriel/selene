package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.executor.IntrinsicFunction
import me.gabriel.gwydion.llvm.LLVMCodeAssembler
import me.gabriel.gwydion.llvm.LLVMCodeGenerator
import me.gabriel.gwydion.llvm.struct.MemoryUnit
import me.gabriel.gwydion.llvm.struct.NullMemoryUnit
import me.gabriel.gwydion.parsing.*

/*
 * I decided to use exceptions instead of errors because the exceptions should be caught in
 * the semantic analysis phase. Using custom and pretty error messages is a waste
 * when there shouldn't be any errors in the first place.
 */
class LLVMCodeAdaptationProcess(
    private val tree: SyntaxTree,
    private val repository: ProgramMemoryRepository,
    private val intrinsics: List<IntrinsicFunction>
) {
    private val llvmGenerator = LLVMCodeGenerator()
    private val llvmAssembler = LLVMCodeAssembler(llvmGenerator)

    fun acceptNode(block: MemoryBlock, node: SyntaxTreeNode): MemoryUnit = when (node) {
        is RootNode, is BlockNode -> blockAdaptChildren(block, node)
        is FunctionNode -> generateFunction(node)
    }

    fun blockAdaptChildren(block: MemoryBlock, node: SyntaxTreeNode): NullMemoryUnit {
        node.getChildren().forEach { acceptNode(block, it) }
        return NullMemoryUnit
    }

    fun generateFunction(node: FunctionNode) {
        val block = repository.root.surfaceSearchChild(node.name)
            ?: error("Function ${node.name} not found in the memory repository")

        // TODO: complete
    }
}