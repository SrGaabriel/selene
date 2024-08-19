package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.analyzer.figureOutTraitForVariable
import me.gabriel.gwydion.analyzer.getExpressionType
import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.intrinsics.IntrinsicFunction
import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.llvm.LLVMCodeAssembler
import me.gabriel.gwydion.llvm.LLVMCodeGenerator
import me.gabriel.gwydion.llvm.struct.*
import me.gabriel.gwydion.parsing.*
import me.gabriel.gwydion.signature.*
import me.gabriel.gwydion.util.castToType
import kotlin.math.absoluteValue
import kotlin.random.Random

/*
 * I decided to use exceptions instead of errors because the exceptions should be caught in
 * the semantic analysis phase. Using custom and pretty error messages is a waste
 * when there shouldn't be any errors in the first place.
 */
class LLVMCodeAdaptationProcess(
    private val module: String,
    private val intrinsics: List<IntrinsicFunction>,
    private val signatures: Signatures,
    private val stdlibDependencies: List<String>,
    private val compileIntrinsics: Boolean
) {
    private val llvmGenerator = LLVMCodeGenerator()
    private val assembler = LLVMCodeAssembler(llvmGenerator)

    private val traitObjects = mutableMapOf<MemoryUnit.Unsized, MutableList<TraitObject>>()
    private val traitObjectsImpl = mutableSetOf<String>()

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
        dependencies.add("@format_f = private unnamed_addr constant [3 x i8] c\"%f\\00\"")
        traitObjectsImpl.forEach {
            dependencies.add(it)
        }

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
        self: Type? = null
    ): Value = when (node) {
        is RootNode, is BlockNode -> blockAdaptChildren(block, node)
        is FunctionNode -> generateFunction(block, node, self)
        is CallNode -> generateFunctionCall(block, node, store)
        is StringNode -> generateString(block, node, store)
        is AssignmentNode -> generateAssignment(block, node)
        is NumberNode -> generateNumber(block, node, store)
        is VariableReferenceNode -> generateVariableReference(block, node)
        is BinaryOperatorNode -> generateBinaryOperator(block, node)
        is ReturnNode -> generateReturn(block, node)
        is IfNode -> generateIf(block, node)
        is BooleanNode -> generateBoolean(block, node, store)
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

    fun generateFunction(block: MemoryBlock, node: FunctionNode, self: Type?): NullMemoryUnit {
        if (node.modifiers.contains(Modifiers.INTRINSIC)) return NullMemoryUnit

        val parameters = mutableListOf<MemoryUnit>()
        node.parameters.forEach { param ->
            if (param.type is Type.Trait) {
                val vtable = MemoryUnit.Unsized(
                    register = assembler.nextRegister(),
                    type = LLVMType.Ptr
                )
                val data = MemoryUnit.Unsized(
                    register = assembler.nextRegister(),
                    type = LLVMType.Ptr
                )
                val trait = MemoryUnit.TraitData(
                    vtable = vtable,
                    data = data,
                    type = LLVMType.Trait(name = node.name, functions = (param.type as Type.Trait).functions.size)
                )
                block.memory.allocate(param.name, trait)
                block.symbols.declare(
                    name = param.name,
                    type = param.type
                )
                parameters.add(vtable)
                parameters.add(data)
                return@forEach
            }
            val type = if (param.type !== Type.Self) getProperReturnType(param.type) else self?.asLLVM()?.let { LLVMType.Pointer(it) } ?: error("Self type not found")

            val unit = MemoryUnit.Sized(
                register = assembler.nextRegister(),
                type = type,
                size = type.size
            )
            block.symbols.declare(
                name = param.name,
                type = param.type
            )
            block.memory.allocate(param.name, unit)
            parameters.add(unit)
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
    ): Value {
        val functionSymbol = block.figureOutSymbol(
            node.name
        ) ?: error("Function ${node.name} not found in block ${block.name}")

        val functionDefinition = block.figureOutDefinition(node.name) as? FunctionNode
            ?: error("Function ${node.name} not found in block ${block.name}")
        val arguments = mutableListOf<Value>()
        node.arguments.forEachIndexed { index, arg ->
            val result = acceptNode(block, arg, true)
            if (result is NullMemoryUnit || result.type is LLVMType.Void) {
                error("Argument $arg is void")
            }

            // the below would work, but what if our type is a struct and the function expects a trait?
            val functionTypeEquivalent = functionDefinition.parameters[index].type
            if (result is MemoryUnit.TraitData && functionTypeEquivalent is Type.Trait) {
                arguments.add(result.vtable)
                arguments.add(result.loadedData ?: error("TraitData was not loaded"))
            } else if (functionTypeEquivalent is Type.Trait) {
                val trait = signatures.traits.firstOrNull {
                    it.name == functionTypeEquivalent.identifier
                } ?: error("Trait ${functionTypeEquivalent.identifier} not found in signatures")

                val type = getExpressionType(block, arg, signatures).unwrap()
                if (type is Type.Trait) error("TraitData was not generated from a trait")
                val impl = trait.impls.firstOrNull {
                    it.struct == type.signature
                } ?: error("Trait implementation for ${getExpressionType(block, arg, signatures).unwrap().signature} not found in signatures")
                arguments.add(LLVMConstant("@${TraitObject.PREFIX}${impl.index}", LLVMType.Ptr))
                arguments.add(result)
            } else {
                arguments.add(result)
            }
        }

        val type = getProperReturnType(functionSymbol)
        val reg = assembler.nextRegister()
        val assignment = if (type != LLVMType.Void) {
            MemoryUnit.Sized(
                register = reg,
                type = type,
                size = type.size
            )
        } else NullMemoryUnit

        val intrinsic = intrinsics.find { it.name == node.name }
        if (intrinsic != null) {
            val call = intrinsic.handleCall(
                call = node,
                types = node.arguments.map { getExpressionType(block, it, signatures).let { it.getRightOrNull() ?: error(it.getLeft().message) } },
                arguments = arguments.joinToString(", ") { "${it.type.llvm} ${it.llvm()}" }
            )
            if (type != LLVMType.Void) {
                assembler.saveToRegister(assignment.register, call)
                return assignment
            } else {
                assembler.instruct(call)
                return LLVMConstant(call, type)
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
        val expression = acceptNode(block, node.expression, true) as? MemoryUnit.Sized
            ?: error("Expression ${node.expression} not stored")

        block.memory.allocate(node.name, expression)
        block.symbols.declare(
            name = node.name,
            type = getExpressionType(block, node.expression, signatures).unwrap()
        )
//        assembler.saveToRegister(unit.register, expression.llvm())
        return expression
    }

    fun generateString(block: MemoryBlock, node: StringNode, store: Boolean): Value {
        val singleSegment = node.segments.singleOrNull()
        if (singleSegment != null && singleSegment is StringNode.Segment.Text) {
            return assembler.buildString(singleSegment.text)
        }

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

    fun generateNumber(block: MemoryBlock, node: NumberNode, store: Boolean): Value {
        val type = node.type.asLLVM()
        if (store) {
            val addition = assembler.addNumber(
                type = type,
                left = LLVMConstant(node.value, type),
                right = LLVMConstant("0".castToType(node.type), type)
            )
            return addition
        }
        return LLVMConstant(
            node.value.toLong(),
            type
        )
    }

    fun generateVariableReference(block: MemoryBlock, node: VariableReferenceNode): MemoryUnit {
        val reference = block.figureOutMemory(node.name)
            ?: error("Reference ${node.name} could not be found")

        if (reference is MemoryUnit.TraitData) {
            if (reference.loadedData == null) {
                reference.loadedData = assembler.loadPointer(reference.data)
            }
        }

        return reference
    }

    fun generateBinaryOperator(block: MemoryBlock, node: BinaryOperatorNode): MemoryUnit {
        val typeResult = getExpressionType(block, node.left, signatures)
        if (typeResult.isLeft()) error("Couldn't figure out binary operation type")

        val type = typeResult.unwrap()
        val op = getBinaryOp(node.operator)
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
        val left = acceptNode(block, node.left)
        val right = acceptNode(block, node.right)
        val result = assembler.binaryOp(
            type = type.asLLVM(),
            left = left,
            op = op,
            right = right
        )
        return result
    }

    fun generateIf(block: MemoryBlock, node: IfNode): NullMemoryUnit {
        val condition = acceptNode(block, node.condition, true) as MemoryUnit.Sized
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
        val type = getExpressionType(block, node.left, signatures).unwrap()
        return assembler.handleComparison(left, right, type.asLLVM())
    }

    fun generateBoolean(block: MemoryBlock, node: BooleanNode, store: Boolean): Value {
        val type = LLVMType.I1
        if (store) {
            val value = assembler.addNumber(
                type = type,
                left = LLVMConstant(if (node.value) 1 else 0, type),
                right = LLVMConstant(0, type)
            )
            return value
        }
        return LLVMConstant(if (node.value) 1 else 0, type)
    }

    fun generateReturn(block: MemoryBlock, node: ReturnNode): NullMemoryUnit {
        val expression = acceptNode(block, node.expression, store = true)
        assembler.returnValue(expression.type, expression)
        return NullMemoryUnit
    }

    fun generateArray(block: MemoryBlock, node: ArrayNode): MemoryUnit {
        val arrayType = getExpressionType(block, node, signatures).unwrap().asLLVM()
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
        val structType = getExpressionType(block, node, signatures).unwrap().asLLVM()
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
            size = node.arguments.sumOf { getExpressionType(block, it, signatures).getRightOrNull()?.asLLVM()?.size ?: 0 },
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
        return NullMemoryUnit
    }

    fun generateTraitImpl(block: MemoryBlock, node: TraitImplNode): MemoryUnit {
        val trait = MemoryUnit.Unsized(
            register = Random.nextInt().absoluteValue, // Here we'll use absolute value instead of UInt to prevent potential overflow/compatibility issues
            type = LLVMType.Dynamic(listOf(
                LLVMType.I16,
                LLVMType.I16,
            ).plus(node.functions.map { LLVMType.Ptr }))
        )
        val traitSignature = signatures.traits.find { it.name == node.trait }
            ?: error("Trait ${node.trait} not found in signatures")
        val traitImplSignature = traitSignature.impls.find { it.struct == node.type.signature }
            ?: error("Trait implementation for ${node.type.signature} not found in signatures")
        traitImplSignature.index = trait.register
        traitImplSignature.module = traitImplSignature.module ?: module

        block.memory.allocate(
            name = "${node.trait}_trait_${node.type.signature}",
            unit = trait
        )

        registerTraitObject(
            traitMem = trait,
            structName = node.type.signature,
            functions = node.functions.map { it.name }
        )

        val traitBlock = block.surfaceSearchChild("${node.trait}_trait_${node.type.signature}")
            ?: error("Trait ${node.trait} not found in block ${block.name}")

        node.functions.forEach {
            traitBlock.symbols.declare(
                name = "${node.type.signature}_${it.name}",
                type = it.returnType
            )
            acceptNode(traitBlock, it.copy(
                name = "${node.type.signature}_${it.name}",
                parameters = it.parameters
            ), self = node.type)
        }

        return NullMemoryUnit
    }

    fun generateTraitCall(block: MemoryBlock, node: TraitFunctionCallNode, store: Boolean): MemoryUnit {
        val (trait, impl, function, _, variableType) = figureOutTraitForVariable(
            block = block,
            variable = node.trait,
            signatures = signatures,
            call = node.function
        ) ?: error("Trait for ${node.trait} not found")

        val type = getProperReturnType(function.returnType)
        val functionIndex = trait.functions.indexOfFirst { it.name == node.function }

        val traitImplPointer = if (impl != null) {
            // We have the struct!
            val virtualTable = getVirtualTableForTraitImpl(impl)
            assembler.getElementFromVirtualTable(
                table = '@' + TraitObject.PREFIX + virtualTable.register,
                tableType = virtualTable.type as LLVMType.Dynamic,
                type = LLVMType.Ptr,
                index = LLVMConstant(functionIndex + 2, LLVMType.I32),
            )
        } else {
            // We need to dynamically call it
            val memory = block.figureOutMemory(node.trait) as? MemoryUnit.TraitData ?: error("Trait ${node.trait} not found")

            assembler.getElementFromVirtualTable(
                table = '%' + memory.vtable.register.toString(),
                tableType = LLVMType.Dynamic(listOf(
                    LLVMType.I16,
                    LLVMType.I16,
                ).plus(trait.functions.map { LLVMType.Ptr })),
                type = LLVMType.Ptr,
                index = LLVMConstant(functionIndex + 2, LLVMType.I32),
            )
        }
        val loadedFunction = assembler.loadPointer(traitImplPointer)

        val assignment = if (type != LLVMType.Void) {
            MemoryUnit.Sized(
                register = assembler.nextRegister(),
                type = type,
                size = type.size
            )
        } else NullMemoryUnit

        val arguments = mutableListOf<Value>()
        node.arguments.map {
            val result = acceptNode(block, it)
            arguments.add(result)
        }

        val variableMemory = block.figureOutMemory(node.trait) ?: error("Variable for trait ${node.trait} not found")
        if (variableType !is Type.Trait) {
            arguments.add(0, variableMemory)
        } else {
            val traitMemory = variableMemory as? MemoryUnit.TraitData ?: error("Trait memory not found")
            arguments.add(0, traitMemory.data)
        }

        assembler.callFunction(
            name = loadedFunction.register.toString(),
            arguments = arguments,
            assignment = assignment,
            local = true
        )

        return assignment
    }

    fun getProperReturnType(returnType: Type): LLVMType =
        getProperReturnType(returnType.asLLVM())

    fun getProperReturnType(returnType: LLVMType): LLVMType {
        return when (returnType) {
            is LLVMType.Struct -> LLVMType.Pointer(returnType)
            is LLVMType.Array -> LLVMType.Pointer(returnType.type)
            else -> returnType
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

    fun getVirtualTableForTraitImpl(impl: SignatureTraitImpl): MemoryUnit.Unsized {
        if (impl.module != module) {
            traitObjectsImpl.add(
                "@trait_${impl.index} = external constant <{ i16, i16, ${
                    impl.types.joinToString(
                        ", "
                    ) { "ptr" }
                } }>"
            )
        }
        return MemoryUnit.Unsized(
            register = impl.index ?: error("Trait index not found"),
            type = LLVMType.Dynamic(listOf(
                LLVMType.I16,
                LLVMType.I16,
            ).plus(impl.types.map { LLVMType.Ptr }))
        )
    }

    fun getBinaryOp(kind: TokenKind): BinaryOp {
        return when (kind) {
            TokenKind.PLUS -> BinaryOp.Addition
            TokenKind.MINUS -> BinaryOp.Subtraction
            TokenKind.TIMES -> BinaryOp.Multiplication
            TokenKind.DIVIDE -> BinaryOp.Division
            else -> error("Binary operation not supported")
        }
    }
}