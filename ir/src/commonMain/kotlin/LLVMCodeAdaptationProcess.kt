package me.gabriel.gwydion.ir

import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.signature.SignatureTraitImpl
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.castToType
import me.gabriel.gwydion.analysis.util.resolveTraitForExpression
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.lexing.TokenKind
import me.gabriel.gwydion.frontend.parsing.*
import me.gabriel.gwydion.ir.intrinsics.IntrinsicFunction
import me.gabriel.gwydion.llvm.LLVMCodeAssembler
import me.gabriel.gwydion.llvm.LLVMCodeGenerator
import me.gabriel.gwydion.llvm.struct.*

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

    private val lambdas = mutableListOf<LambdaData>()
    private val traitObjects = mutableMapOf<MemoryUnit.Unsized, MutableList<TraitObject>>()
    private val traitObjectsImpl = mutableSetOf<String>()

    private val memory = mutableMapOf<SymbolBlock, MutableMap<String, MemoryUnit>>()

    private val intrinsicDependencies = mutableSetOf<String>()

    fun setup() {
        val dependencies = mutableSetOf<String>()
        stdlibDependencies.forEach {
            dependencies.add(it)
        }
        if (compileIntrinsics) {
            intrinsics.forEach {
                it.dependencies().forEach { dependencies.add(it) }
                dependencies.add(it.llvmIr())
            }
        } else {
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
        intrinsicDependencies.forEach {
            assembler.addDependency(it)
        }
        traitObjects.forEach { (_, traits) ->
            traits.forEach { obj ->
                assembler.addDependency(assembler.generator.createTraitObject(obj = obj))
            }
        }
        lambdas.forEach { lambda ->
            assembler.functionDsl(
                name = "lambda_${lambda.node.hashCode()}",
                arguments = lambda.parameters,
                returnType = getProperReturnType(lambda.returnType)
            ) {
                assembler.returnValue(acceptNode(lambda.block, lambda.node.body))
            }
        }
    }

    fun getGeneratedCode(): String {
        return assembler.finish()
    }

    fun acceptNode(
        block: SymbolBlock,
        node: SyntaxTreeNode,
        store: Boolean = false,
        self: GwydionType? = null
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
        is ArrayAssignmentNode -> generateArrayAssignment(block, node)
        is TraitFunctionCallNode -> generateTraitCall(block, node, store)
        is ForNode -> generateFor(block, node)
        is LambdaNode -> generateLambda(block, node, store)
        else -> error("Node $node not supported")
    }

    private fun blockAdaptChildren(block: SymbolBlock, node: SyntaxTreeNode): NullMemoryUnit {
        node.getChildren().forEach { acceptNode(block, it) }
        return NullMemoryUnit
    }

    private fun generateFunction(block: SymbolBlock, node: FunctionNode, self: GwydionType?): NullMemoryUnit {
        if (node.modifiers.contains(Modifiers.INTRINSIC)) return NullMemoryUnit

        val parameters = mutableListOf<MemoryUnit>()
        val block = block.surfaceSearchChild(node)
            ?: error("Block ${node.name} not found in block ${block.name}")
        node.parameters.forEach { param ->
            if (param.type is GwydionType.Trait) {
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
                    type = LLVMType.Trait(
                        name = node.name,
                        functions = (param.type as GwydionType.Trait).functions.size
                    )
                )
                putMemoryUnit(block, param.name, trait)

                parameters.add(vtable)
                parameters.add(data)
                return@forEach
            }
            val type = if (param.type.base !is GwydionType.Self) getProperReturnType(param.type) else self?.asLLVM()?.let { LLVMType.Pointer(it) } ?: error("Self type not found")

            val unit = MemoryUnit.Sized(
                register = assembler.nextRegister(),
                type = type,
                size = type.size
            )
            putMemoryUnit(block, param.name, unit)
            parameters.add(unit)
        }
        val properReturnType = getProperReturnType(node.returnType)
        val name = if (self != null) {
            "${self.signature}.${node.name}"
        } else node.name

        assembler.functionDsl(
            name = name,
            arguments = parameters,
            returnType = properReturnType
        ) {
            acceptNode(block, node.body)
            if (node.returnType == GwydionType.Void) {
                assembler.returnVoid()
            }
        }

        return NullMemoryUnit
    }

    private fun generateFunctionCall(
        block: SymbolBlock,
        node: CallNode,
        store: Boolean
    ): Value {
        val potentialLambda = block.resolveSymbol(node.name)
        if (potentialLambda != null && potentialLambda is GwydionType.Lambda) {
            val parameters = mutableListOf<Value>()
            node.arguments.forEachIndexed { index, arg ->
                val result = acceptNode(block, arg)
                parameters.add(result)
            }

            val type = getProperReturnType(potentialLambda.returnType)
            val reg = assembler.nextRegister()
            val assignment = if (type != LLVMType.Void) {
                MemoryUnit.Sized(
                    register = reg,
                    type = type,
                    size = type.size
                )
            } else NullMemoryUnit

            val inMemory = resolveMemoryUnit(block, node.name)
                ?: error("Lambda ${node.name} not found in block ${block.name}")

            assembler.callFunction(
                name = inMemory.register.toString(),
                arguments = parameters,
                assignment = assignment,
                local = true
            )
            return assignment
        }

        val (expectedParameters, functionSymbol) = signatures.functions.find { it.name == node.name }
            ?.let { it.parameters to it.returnType }
            ?: error("Function ${node.name} not found in block ${block.name}")

        val arguments = mutableListOf<Value>()
        node.arguments.forEachIndexed { index, arg ->
            val result = passReferenceToArray(acceptNode(block, arg, true))
            if (result is NullMemoryUnit || result.type is LLVMType.Void) {
                error("Argument $arg is void")
            }

            val functionTypeEquivalent = expectedParameters[index]
            if (result is MemoryUnit.TraitData && functionTypeEquivalent is GwydionType.Trait) {
                arguments.add(result.vtable)
                arguments.add(result.loadedData ?: error("TraitData was not loaded"))
            } else if (functionTypeEquivalent is GwydionType.Trait) {
                val trait = signatures.traits.firstOrNull {
                    it.name == functionTypeEquivalent.identifier
                } ?: error("Trait ${functionTypeEquivalent.identifier} not found in signatures")

                val type = block.resolveExpression(arg)
                if (type == null || type is GwydionType.Trait) error("TraitData was not generated from a trait")

                val impl = trait.impls.firstOrNull {
                    it.struct == type.signature
                } ?: error("Trait implementation not found in signatures")

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
            if (!compileIntrinsics)
                intrinsicDependencies.addAll(intrinsic.declarations())

            val call = intrinsic.handleCall(
                call = node,
                types = node.arguments.map {
                    block.resolveExpression(it) ?: error("Type not found for argument $it")
                },
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

    private fun generateAssignment(block: SymbolBlock, node: AssignmentNode): MemoryUnit {
        val expression = acceptNode(block, node.expression, true) as? MemoryUnit
            ?: error("Expression ${node.expression} not stored")

        putMemoryUnit(block, node.name, expression)

        return expression
    }

    private fun generateString(block: SymbolBlock, node: StringNode, store: Boolean): Value {
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

    private fun generateNumber(block: SymbolBlock, node: NumberNode, store: Boolean): Value {
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

    private fun generateVariableReference(block: SymbolBlock, node: VariableReferenceNode): MemoryUnit {
        val reference = resolveMemoryUnit(block, node.name)
            ?: error("Reference ${node.name} could not be found")

        if (reference is MemoryUnit.TraitData) {
            if (reference.loadedData == null) {
                reference.loadedData = assembler.loadPointer(reference.data)
            }
        }

        return reference
    }

    private fun generateBinaryOperator(block: SymbolBlock, node: BinaryOperatorNode): MemoryUnit {
        val type = block.resolveExpression(node.left)
            ?: error("Couldn't resolve binary operation type at ${block.name} for ${node.left}")

        val op = getBinaryOp(node.operator.kind)
        if (type == GwydionType.String) {
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

    private fun generateIf(block: SymbolBlock, node: IfNode): NullMemoryUnit {
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
            acceptNode(block, node.elseBody!!)
            assembler.unconditionalBranchTo(endLabel)
        }
        assembler.createBranch(endLabel)

        return NullMemoryUnit
    }

    private fun generateEquality(block: SymbolBlock, node: EqualsNode): MemoryUnit {
        val left = acceptNode(block, node.left)
        val right = acceptNode(block, node.right)
        val type = block.resolveExpression(node.left)
            ?: error("Couldn't resolve equality type")
        return assembler.handleComparison(left, right, type.asLLVM())
    }

    private fun generateBoolean(block: SymbolBlock, node: BooleanNode, store: Boolean): Value {
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

    private fun generateReturn(block: SymbolBlock, node: ReturnNode): NullMemoryUnit {
        val expression = acceptNode(block, node.expression, store = true)
        val unit = if (expression.type.extractPrimitiveType() is LLVMType.Array) {
            assembler.getElementFromStructure(
                struct = expression,
                type = (expression.type.extractPrimitiveType() as LLVMType.Array).type.extractPrimitiveType(),
                index = LLVMConstant(0, LLVMType.I32),
                total = true
            )
        } else expression

        assembler.returnValue(getProperReturnType(unit.type), unit)
        return NullMemoryUnit
    }

    private fun generateArray(block: SymbolBlock, node: ArrayNode): MemoryUnit {
        val arrayType = block.resolveExpression(node)?.asLLVM()
            ?: error("Couldn't resolve array type")
        val type = arrayType.descendOneLevel()
        return assembler.createArray(
            type = type,
            size = if (!node.dynamic) node.elements.size else null,
            elements = node.elements.map { acceptNode(block, it) }
        )
    }

    private fun generateArrayAccess(block: SymbolBlock, node: ArrayAccessNode): MemoryUnit {
        val arrayMemory = acceptNode(block, node.array) as MemoryUnit
        val index = acceptNode(block, node.index)

        val intermediate = (arrayMemory.type as LLVMType.Pointer).descendOneLevel()
        val type = intermediate.descendOneLevel()
        val pointer = assembler.getElementFromStructure(
            struct = arrayMemory,
            type = if (intermediate is LLVMType.Array) type else LLVMType.Pointer(type),
            index = index,
            total = intermediate is LLVMType.Array
        )
        if (type is LLVMType.Array || type is LLVMType.Struct) {
            return pointer
        }
        return assembler.loadPointer(pointer)
    }

    private fun generateDataStruct(block: SymbolBlock, node: DataStructureNode): MemoryUnit {
        assembler.declareStruct(
            name = node.name,
            fields = node.fields.associate { it.name to getProperReturnType(it.type.asLLVM()) }
        )
        return NullMemoryUnit
    }

    private fun generateInstantiation(block: SymbolBlock, node: InstantiationNode): MemoryUnit {
        val memorySignature = signatures.structs.find { it.name == node.name }
        val memoryType = LLVMType.Struct(
            name = node.name,
            fields = memorySignature?.fields?.mapValues { getProperReturnType(it.value.asLLVM()) }
                ?: error("Struct ${node.name} not found in signatures")
        )
        if (memorySignature.module != module) {
            assembler.declareStruct(
                name = node.name,
                fields = memoryType.fields
            )
        }

        val heap = false
        val allocation = if (heap) assembler.allocateHeapMemoryAndCast(
            size = node.arguments.sumOf { block.resolveExpression(it)?.asLLVM()?.size ?: 0 },
            type = LLVMType.Pointer(memoryType)
        ) else assembler.allocateStackMemory(
            type = memoryType,
            alignment = 8
        ) as MemoryUnit.Sized

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

    private fun generateStructAccess(block: SymbolBlock, node: StructAccessNode): MemoryUnit {
        val struct = acceptNode(block, node.struct) as MemoryUnit.Sized
        val pointerType = struct.type as LLVMType.Pointer
        val structType = pointerType.type as LLVMType.Struct
        val index = structType.fields.keys.indexOf(node.field)
        val type = structType.fields[node.field] ?: error("Field ${node.field} not found in struct ${node.struct}")

        val pointer = assembler.getElementFromStructure(
            struct = struct,
            type = type,
            index = LLVMConstant(index, LLVMType.I32),
        )
        return when (type) {
            is LLVMType.Array -> pointer
            else -> assembler.loadPointer(pointer)
        }
    }

    private fun generateMutation(block: SymbolBlock, node: MutationNode): MemoryUnit {
        val struct = if (node.struct is StructAccessNode) {
            acceptNode(block, (node.struct as StructAccessNode).struct) as? MemoryUnit ?: error("Struct ${(node.struct as StructAccessNode).struct} not found")
        } else {
            TODO()
        }
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

    private fun generateTrait(block: SymbolBlock, node: TraitNode): MemoryUnit {
        return NullMemoryUnit
    }

    private fun generateTraitImpl(block: SymbolBlock, node: TraitImplNode): MemoryUnit {
        val trait = MemoryUnit.Unsized(
            register = node.hashCode(),
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

        registerTraitObject(
            traitMem = trait,
            structName = node.type.signature,
            functions = node.functions.map { it.name }
        )

        val traitBlock = block.surfaceSearchChild(node)
            ?: error("Trait ${node.trait} not found in block ${block.name}")

        node.functions.forEach {
            acceptNode(traitBlock, it, self=node.type)
        }

        return NullMemoryUnit
    }

    private fun generateTraitCall(block: SymbolBlock, node: TraitFunctionCallNode, store: Boolean): MemoryUnit {
        val (trait, impl, function, _, variableType) = resolveTraitForExpression(
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
            val memory = acceptNode(block, node.trait, store) as? MemoryUnit.TraitData ?: error("Trait ${node.trait} not found")

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
        if (!(node.static || !function.parameters.contains(GwydionType.Self))) {
            val variableMemory = acceptNode(block, node.trait)
            if (variableType !is GwydionType.Trait) {
                arguments.add(0, variableMemory)
            } else {
                val traitMemory = variableMemory as? MemoryUnit.TraitData ?: error("Trait memory not found")
                arguments.add(0, traitMemory.data)
            }
        }

        node.arguments.map {
            val result = acceptNode(block, it)
            arguments.add(result)
        }

        assembler.callFunction(
            name = loadedFunction.register.toString(),
            arguments = arguments,
            assignment = assignment,
            local = true
        )

        return assignment
    }

    private fun generateFor(block: SymbolBlock, node: ForNode): NullMemoryUnit {
        val type = GwydionType.Int32
        val llvmType = type.asLLVM()
        val allocation = assembler.allocateStackMemory(
            type = llvmType,
            alignment = 4
        ) as MemoryUnit.Sized

        assembler.storeTo(
            value = acceptNode(block, node.iterable.from),
            address = allocation
        )
        val end = acceptNode(block, node.iterable.to)

        val conditionLabel = assembler.nextLabel()
        assembler.unconditionalBranchTo(conditionLabel)

        assembler.createBranch(conditionLabel)
        val loaded = assembler.loadPointer(allocation)
        val comparison = assembler.customComparison(Comparison.Integer.SignedLessThanOrEqual(
            left = loaded,
            right = end
        ))
        val bodyLabel = assembler.nextLabel()
        val endLabel = assembler.nextLabel()
        assembler.conditionalBranch(comparison, bodyLabel, endLabel)

        assembler.createBranch(bodyLabel)
        val loadedBodyValue = assembler.loadPointer(allocation)
        putMemoryUnit(block, node.variable, loadedBodyValue)

        acceptNode(block, node.body)
        val next = assembler.addNumber(
            type = llvmType,
            left = loaded,
            right = LLVMConstant(1, llvmType)
        )
        assembler.storeTo(
            value = next,
            address = allocation
        )
        assembler.unconditionalBranchTo(conditionLabel)

        assembler.createBranch(endLabel)

        return NullMemoryUnit
    }

    private fun generateArrayAssignment(block: SymbolBlock, node: ArrayAssignmentNode): MemoryUnit {
        val array = acceptNode(block, node.array) as MemoryUnit
        val index = acceptNode(block, node.index)
        val value = acceptNode(block, node.expression)

        val intermediate = (array.type as LLVMType.Pointer).descendOneLevel()
        val type = intermediate.descendOneLevel()
        assembler.setStructElementTo(
            struct = array,
            value = value,
            type = if (intermediate is LLVMType.Array) type else LLVMType.Pointer(type),
            index = index
        )
        return NullMemoryUnit
    }

    private fun generateLambda(block: SymbolBlock, node: LambdaNode, store: Boolean): Value {
        val lambdaBlock = block.surfaceSearchChild(node)
            ?: error("Block for lambda ${node.hashCode()} not found in block ${block.name}")
        val parameters = mutableListOf<MemoryUnit>()

        node.parameters.forEach { param ->
            val type = getProperReturnType(param.type)
            val unit = MemoryUnit.Sized(
                register = assembler.nextRegister(),
                type = type,
                size = type.size
            )
            putMemoryUnit(lambdaBlock, param.name, unit)
            parameters.add(unit)
        }
        val returnType = (block.resolveExpression(node) as? GwydionType.Lambda)?.returnType
            ?: error("Return type not found for lambda $node")

        lambdas.add(
            LambdaData(
                node = node,
                parameters = parameters,
                block = lambdaBlock,
                returnType = returnType
            )
        )

        return LLVMConstant("@lambda_${node.hashCode()}", LLVMType.Ptr)
    }

    private fun registerTraitObject(traitMem: MemoryUnit.Unsized, structName: String, functions: List<String>) {
        // Create new key if trait doesn't exist, otherwise add to the list of functions.
        traitObjects.getOrPut(traitMem) { mutableListOf() }
            .add(TraitObject(
                register = traitMem.register,
                name = structName,
                size = 8,
                alignment = 8,
                functions = functions
            ))
    }

    private fun getVirtualTableForTraitImpl(impl: SignatureTraitImpl): MemoryUnit.Unsized {
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

    private fun passReferenceToArray(array: Value): Value {
        val primitive = array.type.extractPrimitiveType()
        if (primitive is LLVMType.Array) {
            return assembler.getElementFromStructure(
                struct = array,
                type = primitive.type,
                index = LLVMConstant(0, LLVMType.I32),
                total = true
            )
        }
        return array
    }

    private fun putMemoryUnit(block: SymbolBlock, name: String, unit: MemoryUnit) {
        memory[block] = (memory[block] ?: mutableMapOf()).apply {
            this[name] = unit
        }
    }

    private fun resolveMemoryUnit(block: SymbolBlock, name: String): MemoryUnit? {
        var currentBlock: SymbolBlock? = block
        while (currentBlock != null) {
            memory[currentBlock]?.get(name)?.let { return it }
            currentBlock = currentBlock.parent
        }
        return null
    }

    private fun getBinaryOp(kind: TokenKind): BinaryOp {
        return when (kind) {
            TokenKind.PLUS -> BinaryOp.Addition
            TokenKind.MINUS -> BinaryOp.Subtraction
            TokenKind.TIMES -> BinaryOp.Multiplication
            TokenKind.DIVIDE -> BinaryOp.Division
            else -> error("Binary operation not supported")
        }
    }

    private data class LambdaData(
        val node: LambdaNode,
        val parameters: List<MemoryUnit>,
        val block: SymbolBlock,
        val returnType: GwydionType,
    )
}