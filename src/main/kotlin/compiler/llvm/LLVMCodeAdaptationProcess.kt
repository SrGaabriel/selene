package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.analyzer.getExpressionType
import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.compiler.IntrinsicFunction
import me.gabriel.gwydion.llvm.LLVMCodeAssembler
import me.gabriel.gwydion.llvm.LLVMCodeGenerator
import me.gabriel.gwydion.llvm.struct.*
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
        is BinaryOperatorNode -> generateBinaryOperator(block, node)
        is ReturnNode -> generateReturn(block, node)
        is IfNode -> generateIf(block, node)
        is BooleanNode -> generateBoolean(block, node)
        is EqualsNode -> generateEquality(block, node)
        is ArrayNode -> generateArray(block, node)
        is ArrayAccessNode -> generateArrayAccess(block, node)
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
            val unit = MemoryUnit.Sized(
                register = assembler.nextRegister(),
                type = type,
                size = type.size
            )
            block.symbols.declare(
                name = it.name,
                type = it.type
            )
            block.memory.allocate(it.name, unit)
            unit
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
        ) ?: error("Function ${node.name} not found in block ${block.name}")

        val arguments = node.arguments.map {
            acceptNode(block, it, true)
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
            val call = intrinsic.handleCall(
                call = node,
                types = node.arguments.map { getExpressionType(block, it).unwrap() },
                arguments = arguments.joinToString(", ") { "${it.type.llvm} %${it.register}" }
            )
            if (store) {
                assembler.saveToRegister(assignment.register, call)
                return assignment
            } else {
                assembler.instruct(call)
                return NullMemoryUnit
            }
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

        println("Alloca: ${node.name} ${expression}")
        block.memory.allocate(node.name, expression)
        block.symbols.declare(
            name = node.name,
            type = getExpressionType(block, node.expression).unwrap()
        )
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

                is StringNode.Segment.Reference -> {
                    val reference = generateVariableReference(block, segment.node)
                    segmentUnits.add(reference)
                }

                else -> error("Segment not supported")
            }
        }
        val space = assembler.allocateHeapMemory(
            size = 64
        ) as MemoryUnit.Sized
        segmentUnits.forEach { segment ->
            println("Segment: $segment")
            assembler.addSourceToDestinationString(
                source = segment,
                destination = space
            )
        }
        return space
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
            ?: error("Reference ${node.name} could not be found")

        return reference
    }

    fun generateBinaryOperator(block: MemoryBlock, node: BinaryOperatorNode): MemoryUnit {
        val typeResult = getExpressionType(block, node.left)
        if (typeResult.isLeft()) error("Couldn't figure out binary operation type")

        val type = typeResult.unwrap()
        if (type == Type.String) {
            val left = acceptNode(block, node.left) as MemoryUnit.Sized
            val right = acceptNode(block, node.right) as MemoryUnit.Sized
            val resultString = assembler.allocateHeapMemory(
                size = 64
            ) as MemoryUnit.Sized
            assembler.copySourceToDestinationString(
                source = left,
                destination = resultString
            )
            assembler.addSourceToDestinationString(
                source = right,
                destination = resultString
            )
            return resultString
        }
        return NullMemoryUnit
    }

    fun generateIf(block: MemoryBlock, node: IfNode): NullMemoryUnit {
        val condition = acceptNode(block, node.condition)
        val trueLabel = assembler.nextLabel()
        val falseLabel = assembler.nextLabel()
        val endLabel = assembler.nextLabel()

        assembler.conditionalBranch(condition, trueLabel, falseLabel)
        assembler.createBranch(trueLabel)
        acceptNode(block, node.body)
        assembler.unconditionalBranchTo(endLabel)
        if (node.elseBody != null) {
            assembler.createBranch(falseLabel)
            acceptNode(block, node.elseBody)
            assembler.unconditionalBranchTo(endLabel)
        }
        assembler.createBranch(endLabel)

        return NullMemoryUnit
    }

    fun generateEquality(block: MemoryBlock, node: EqualsNode): MemoryUnit {
        val left = acceptNode(block, node.left)
        val right = acceptNode(block, node.right)
        val type = getExpressionType(block, node.left).unwrap()
        return assembler.handleComparison(left, right, type.asLLVM())
    }

    fun generateBoolean(block: MemoryBlock, node: BooleanNode): MemoryUnit {
        val type = LLVMType.I1
        return assembler.addNumber(
            type = type,
            left = LLVMConstant(if (node.value) 1 else 0, type),
            right = LLVMConstant(0, type)
        )
    }

    fun generateReturn(block: MemoryBlock, node: ReturnNode): NullMemoryUnit {
        val expression = acceptNode(block, node.expression)
        assembler.returnValue(expression.type, expression)
        return NullMemoryUnit
    }

    fun generateArray(block: MemoryBlock, node: ArrayNode): MemoryUnit {
        val arrayType = getExpressionType(block, node).unwrap().asLLVM()
        if (arrayType !is LLVMType.Array) error("Array type not found")
        val type = arrayType.type
        println("Array type: $type")
        return assembler.createArray(
            type = type,
            size = node.elements.size,
            elements = node.elements.map { acceptNode(block, it) }
        )
    }

    fun generateArrayAccess(block: MemoryBlock, node: ArrayAccessNode): MemoryUnit {
        val arrayMemory = block.figureOutMemory(node.identifier) ?: error("Array ${node.identifier} not found")
        val index = acceptNode(block, node.index)

        val pointer = assembler.smartGetElementFromStructure(
            struct = arrayMemory as? MemoryUnit.Structure ?: error("Array not found"),
            index = index
        )
        return assembler.loadPointer(pointer)
    }
}