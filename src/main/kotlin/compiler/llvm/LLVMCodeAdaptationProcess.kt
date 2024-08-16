package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.analyzer.figureOutTraitForVariable
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
    private val intrinsics: List<IntrinsicFunction>,
    private val stdlibDependencies: List<String>,
    private val compileIntrinsics: Boolean
) {
    private val llvmGenerator = LLVMCodeGenerator()
    private val assembler = LLVMCodeAssembler(llvmGenerator)

    private val traitObjects = mutableMapOf<MemoryUnit.Unsized, MutableList<TraitObject>>()

    fun setup() {
        val dependencies = mutableSetOf<String>()
        stdlibDependencies.forEach {
            dependencies.add(it)
        }
        intrinsics.forEach {
            if (compileIntrinsics) {
                it.dependencies().forEach { dependencies.add(it) }
                dependencies.add(it.llvmIr())
            } else {
                it.declarations().forEach { dependencies.add(it) }
            }
        }
        if (!compileIntrinsics) {
            dependencies.add("declare i32 @printf(i8*, ...)")
        }
        dependencies.add("@format_s = private unnamed_addr constant [3 x i8] c\"%s\\00\"")
        dependencies.add("@format_n = private unnamed_addr constant [3 x i8] c\"%d\\00\"")
        dependencies.add("@format_b = private unnamed_addr constant [3 x i8] c\"%d\\00\"")

        (assembler.generator.getGeneratedDependencies() + dependencies).forEach {
            assembler.addDependency(it)
        }
    }

    fun finish() {
        traitObjects.forEach { (traitMem, traits) ->
            // TODO: fix
            traits.forEach { obj ->
                assembler.addDependency(assembler.generator.createTraitObject(obj = obj))
            }
        }
    }

    fun getGeneratedCode(): String {
        return assembler.finish()
    }

    fun acceptNode(
        block: MemoryBlock,
        node: SyntaxTreeNode,
        store: Boolean = false,
        self: Type.Struct? = null
    ): MemoryUnit = when (node) {
        is RootNode, is BlockNode -> blockAdaptChildren(block, node)
        is FunctionNode -> generateFunction(block, node, self)
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
        is DataStructureNode -> generateDataStruct(block, node)
        is InstantiationNode -> generateInstantiation(block, node)
        is StructAccessNode -> generateStructAccess(block, node)
        is MutationNode -> generateMutation(block, node)
        is TraitNode -> generateTrait(block, node)
        is TraitImplNode -> generateTraitImpl(block, node)
        is TraitFunctionCallNode -> generateTraitCall(block, node, store)
        else -> error("Node $node not supported")
    }

    fun blockAdaptChildren(block: MemoryBlock, node: SyntaxTreeNode): NullMemoryUnit {
        node.getChildren().forEach { acceptNode(block, it) }
        return NullMemoryUnit
    }

    fun generateFunction(block: MemoryBlock, node: FunctionNode, self: Type.Struct?): NullMemoryUnit {
        if (node.modifiers.contains(Modifiers.INTRINSIC)) return NullMemoryUnit

        val parameters = node.parameters.map {
            val type = if (it.type !== Type.Self) getProperReturnType(it.type) else self?.asLLVM()?.let { LLVMType.Pointer(it) } ?: error("Self type not found")
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
        val properReturnType = getProperReturnType(node.returnType)

        assembler.functionDsl(
            name = node.name,
            arguments = parameters,
            returnType = properReturnType
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

        val type = getProperReturnType(functionSymbol)
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
                types = node.arguments.map { getExpressionType(block, it).let { it.getRightOrNull() ?: error(it.getLeft().message) } },
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
        val type = arrayType.descendOneLevel()
        return assembler.createArray(
            type = type,
            size = if (!node.dynamic) node.elements.size else null,
            elements = node.elements.map { acceptNode(block, it) }
        )
    }

    fun generateArrayAccess(block: MemoryBlock, node: ArrayAccessNode): MemoryUnit {
        val arrayMemory = block.figureOutMemory(node.identifier) ?: error("Array ${node.identifier} not found")
        val index = acceptNode(block, node.index)

        val pointer = assembler.getElementFromStructure(
            struct = arrayMemory,
            type = arrayMemory.type.descendOneLevel(),
            index = index,
            total = false
        )
        return assembler.loadPointer(pointer)
    }

    fun generateDataStruct(block: MemoryBlock, node: DataStructureNode): MemoryUnit {
        val structType = getExpressionType(block, node).unwrap().asLLVM()
        if (structType !is LLVMType.Struct) error("Struct type not found")

        val struct = assembler.declareStruct(
            name = node.name,
            fields = node.fields.associate { it.name to it.type.asLLVM() }
        )
        block.memory.allocate(node.name, struct)
        return NullMemoryUnit
    }

    fun generateInstantiation(block: MemoryBlock, node: InstantiationNode): MemoryUnit {
        val memory = block.figureOutMemory(node.name) ?: error("Data structure ${node.name} not found")
        val allocation = assembler.allocateHeapMemoryAndCast(
            size = node.arguments.sumOf { getExpressionType(block, it).getRightOrNull()?.asLLVM()?.size ?: 0 },
            type = LLVMType.Pointer(memory.type)
        )

        node.arguments.forEachIndexed { index, argument ->
            val value = acceptNode(block, argument)
            assembler.setStructElementTo(
                value = value,
                struct = allocation,
                type = value.type,
                index = LLVMConstant(index, LLVMType.I32)
            )
        }
        return allocation
    }

    fun generateStructAccess(block: MemoryBlock, node: StructAccessNode): MemoryUnit {
        val struct = block.figureOutMemory(node.struct) ?: error("Struct ${node.struct} not found")
        val pointerType = struct.type as LLVMType.Pointer
        val structType = pointerType.type as LLVMType.Struct
        val index = structType.fields.keys.indexOf(node.field)
        val type = structType.fields[node.field] ?: error("Field ${node.field} not found in struct ${node.struct}")
        val pointer = assembler.getElementFromStructure(
            struct = struct,
            type = type,
            index = LLVMConstant(index, LLVMType.I32),
        )
        return assembler.loadPointer(pointer)
    }

    fun generateMutation(block: MemoryBlock, node: MutationNode): MemoryUnit {
        val struct = block.figureOutMemory(node.struct) ?: error("Struct ${node.struct} not found")
        val pointerType = struct.type as LLVMType.Pointer
        val structType = pointerType.type as LLVMType.Struct
        val index = structType.fields.keys.indexOf(node.field)
        val type = structType.fields[node.field] ?: error("Field ${node.field} not found in struct ${node.struct}")
        val value = acceptNode(block, node.expression)

        assembler.setStructElementTo(
            value = value,
            struct = struct,
            type = type,
            index = LLVMConstant(index, LLVMType.I32)
        )
        return NullMemoryUnit
    }

    fun generateTrait(block: MemoryBlock, node: TraitNode): MemoryUnit {
        println("Allocating ${node.name} in ${block.name}")
        return block.memory.allocate(
            name = node.name,
            unit = MemoryUnit.Unsized(
                register = assembler.nextRegister(),
                type = LLVMType.Dynamic(listOf(
                    LLVMType.I16,
                    LLVMType.I16,
                ).plus(node.functions.map { LLVMType.Ptr }))
            )
        )
    }

    fun generateTraitImpl(block: MemoryBlock, node: TraitImplNode): MemoryUnit {
        val trait = block.figureOutMemory(node.trait) ?: error("Trait ${node.trait} not found")
        val struct = block.figureOutSymbol(node.`object`) as? Type.Struct ?: error("Struct ${node.`object`} not found")

        registerTraitObject(
            traitMem = trait as MemoryUnit.Unsized,
            structName = struct.identifier,
            functions = node.functions.map { it.name }
        )

        val traitBlock = block.surfaceSearchChild("${node.trait}_trait_${node.`object`}")
            ?: error("Trait ${node.trait} not found in block ${block.name}")

        node.functions.forEach {
            traitBlock.symbols.declare(
                name = "${node.`object`}_${it.name}",
                type = it.returnType
            )
            acceptNode(traitBlock, it.copy(
                name = "${node.`object`}_${it.name}",
                parameters = it.parameters
            ), self = struct as Type.Struct)
        }

        return NullMemoryUnit
    }

    fun generateTraitCall(block: MemoryBlock, node: TraitFunctionCallNode, store: Boolean): MemoryUnit {
        val (trait, function) = figureOutTraitForVariable(
            block = block,
            variable = node.trait,
            call = node.function
        ) ?: error("Trait for ${node.trait} not found")

        val virtualTable = block.figureOutMemory(trait.identifier) ?: error("Trait ${trait.identifier} not found")
        val type = getProperReturnType(function.returnType)

        val traitImplPointer = assembler.getElementFromVirtualTable(
            table = '@' + TraitObject.PREFIX + virtualTable.register,
            tableType = virtualTable.type as LLVMType.Dynamic,
            type = LLVMType.Ptr,
            index = LLVMConstant(2, LLVMType.I32), // todo: fix
        )
        val functionPointer = assembler.getElementFromStructure(
            struct = assembler.loadPointer(traitImplPointer),
            type = LLVMType.Ptr,
            index = LLVMConstant(0, LLVMType.I32)
        )

        val loadedFunction = assembler.loadPointer(functionPointer)
        val assignment = MemoryUnit.Sized(
            register = assembler.nextRegister(),
            type = type,
            size = type.size
        )

        val arguments = node.arguments.map {
            acceptNode(block, it)
        }.toMutableList()

        val variableMemory = block.figureOutMemory(node.trait) ?: error("Variable for trait ${node.trait} not found")
        arguments.add(0, variableMemory)

        assembler.callFunction(
            name = loadedFunction.register.toString(),
            arguments = arguments,
            assignment = assignment,
            local = true
        )

        return NullMemoryUnit
    }

    fun getProperReturnType(returnType: Type): LLVMType {
        return when (val type = returnType.asLLVM()) {
            is LLVMType.Struct -> LLVMType.Pointer(type)
            is LLVMType.Array -> LLVMType.Pointer(type.type)
            else -> type
        }
    }

    fun registerTraitObject(traitMem: MemoryUnit.Unsized, structName: String, functions: List<String>) {
        // Create new key if trait doesn't exist, otherwise add to the list of functions.
        traitObjects.computeIfAbsent(traitMem) { mutableListOf() }
            .add(TraitObject(
                register = traitMem.register,
                name = structName,
                size = 8,
                alignment = 8,
                functions = functions
            ))
    }
}