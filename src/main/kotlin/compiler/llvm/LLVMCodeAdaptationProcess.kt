package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.analyzer.getExpressionType
import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.executor.IntrinsicFunction
import me.gabriel.gwydion.llvm.LLVMCodeAssembler
import me.gabriel.gwydion.llvm.LLVMCodeGenerator
import me.gabriel.gwydion.llvm.struct.LLVMConstant
import me.gabriel.gwydion.llvm.struct.LLVMType
import me.gabriel.gwydion.llvm.struct.MemoryUnit
import me.gabriel.gwydion.llvm.struct.NullMemoryUnit
import me.gabriel.gwydion.parsing.*
import kotlin.math.exp

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
    private val assembler = LLVMCodeAssembler(llvmGenerator)

    fun setup() {
        intrinsics.forEach {
            assembler.addDependency(it.llvmIr())
            it.dependencies().forEach { dependency ->
                assembler.addDependency(dependency)
            }
        }
        assembler.addDependencies()
        assembler.addDependency("@format_s = private unnamed_addr constant [3 x i8] c\"%s\\00\"")
        assembler.addDependency("@format_n = private unnamed_addr constant [3 x i8] c\"%d\\00\"")
    }

    fun finish(): String = assembler.finish()

    fun acceptNode(
        block: MemoryBlock,
        node: SyntaxTreeNode,
        store: Boolean = false
    ): MemoryUnit = when (node) {
        is RootNode, is BlockNode -> blockAdaptChildren(block, node)
        is FunctionNode -> generateFunction(node)
        is CallNode -> generateFunctionCall(block, node, store)
        is StringNode -> generateString(block, node)
        is AssignmentNode -> generateAssignment(block, node)
        is NumberNode -> generateNumber(block, node)
        is VariableReferenceNode -> generateVariableReference(block, node)
        else -> error("Node $node not supported")
    }

    fun blockAdaptChildren(block: MemoryBlock, node: SyntaxTreeNode): NullMemoryUnit {
        node.getChildren().forEach { acceptNode(block, it) }
        return NullMemoryUnit
    }

    fun generateFunction(node: FunctionNode): NullMemoryUnit {
        if (node.modifiers.contains(Modifiers.INTRINSIC)) return NullMemoryUnit
        val block = repository.root.surfaceSearchChild(node.name)
            ?: error("Function ${node.name} not found in the memory repository")

        val parameters = node.parameters.map {
            val type = it.type.asLLVM()
            MemoryUnit.Sized(
                register = assembler.nextRegister(),
                type = type,
                size = type.size
            )
        }

        assembler.functionDsl(
            name = node.name,
            arguments = parameters,
            returnType = node.returnType.asLLVM()
        ) {
            acceptNode(block, node.body)
            if (node.returnType == Type.Void) {
                assembler.returnVoid()
            }
        }

        return NullMemoryUnit
    }

    fun generateFunctionCall(
        block: MemoryBlock,
        node: CallNode,
        store: Boolean
    ): MemoryUnit {
        val functionSymbol = block.figureOutSymbol(
            node.name
        ) ?:
            error("Function ${node.name} not found in block ${block.name}")

        val arguments = node.arguments.map {
            acceptNode(block, it)
        }

        val type = functionSymbol.asLLVM()
        val assignment = if (store) {
            // Todo: review
            MemoryUnit.Sized(
                register = assembler.nextRegister(),
                type = type,
                size = type.size
            )
        } else NullMemoryUnit

        val intrinsic = intrinsics.find { it.name == node.name }
        if (intrinsic != null) {
            assembler.instruct(intrinsic.handleCall(
                call = node,
                types = node.arguments.map { getExpressionType(block, it).unwrap() },
                arguments = arguments.joinToString(", ") { "${it.type.llvm} %${it.register}" }
            ))
            return NullMemoryUnit
        }
        assembler.callFunction(
            name = node.name,
            arguments = arguments,
            assignment = assignment
        )
        return assignment
    }

    fun generateAssignment(block: MemoryBlock, node: AssignmentNode): MemoryUnit {
        val expression = acceptNode(block, node.expression, true)

        println("Alloca")
        block.memory.allocate(node.name, expression)
//        assembler.saveToRegister(unit.register, expression.llvm())
        return expression
    }

    fun generateString(block: MemoryBlock, node: StringNode): MemoryUnit {
        val segmentUnits = mutableListOf<MemoryUnit>()
        for (segment in node.segments) {
            when (segment) {
                is StringNode.Segment.Text -> {
                    segmentUnits.add(assembler.buildString(segment.text))
                }
                else -> error("Segment not supported")
            }
        }
        return segmentUnits.first()
    }

    fun generateNumber(block: MemoryBlock, node: NumberNode): MemoryUnit {
        val type = node.type.asLLVM()
        val addition = assembler.addNumber(
            type = type,
            left = LLVMConstant(node.value.toInt(), type),
            right = LLVMConstant(0, type)
        )
        return addition
    }

    fun generateVariableReference(block: MemoryBlock, node: VariableReferenceNode): MemoryUnit {
        val reference = block.figureOutMemory(node.name)
            ?: error("Reference could not be found")

        return reference
    }
}