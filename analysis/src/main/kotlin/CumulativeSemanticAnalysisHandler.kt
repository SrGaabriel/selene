package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.analysis.signature.*
import me.gabriel.gwydion.frontend.Type
import me.gabriel.gwydion.frontend.isNumeric
import me.gabriel.gwydion.frontend.parsing.*
import me.gabriel.gwydion.frontend.workingBase
import me.gabriel.gwydion.tools.Either

class CumulativeSemanticAnalysisHandler(
    private val tree: SyntaxTree,
    private val repository: ProgramMemoryRepository,
    private val signatures: Signatures
): SemanticAnalysisHandler {
    private val errors = mutableListOf<AnalysisError>()
    private val warnings = mutableListOf<AnalysisWarning>()

    override fun analyzeTree(): AnalysisResult {
        findSymbols(tree.root, repository.root)
        if (errors.isNotEmpty()) {
            return AnalysisResult(errors)
        }
        analyzeNode(tree.root, repository.root)
        return AnalysisResult(errors)
    }

    fun findSymbols(node: SyntaxTreeNode, block: MemoryBlock): MemoryBlock? {
        when (node) {
            is RootNode -> {
                val parentBlocks = node.getChildren().map {
                    it to findSymbols(it, block)
                }
                parentBlocks.forEach { (parent, parentBlock) ->
                    parent.getChildren().forEach { findSymbols(it, parentBlock ?: block) }
                }
            }

            is DataStructureNode -> {
                signatures.structs.add(
                    SignatureStruct(
                        module = "so here we are, just tiny blips in the universe, desperately trying to make sense of something that doesn’t really have any answers. we’re born into this world with no manual, no clear purpose, and yet we’re expected to figure it all out like we’re somehow supposed to know what we’re doing. we go to school, get jobs, chase after some vague idea of success, but for what? to buy more stuff we don’t need, to impress people we don’t really care about, to distract ourselves from the fact that nothing truly lasts? every day is just another step closer to the end, and yet we keep going like we have all the time in the world, even though we know deep down that time is the one thing we can never hold onto. we fill our lives with people and things, hoping they’ll give us a sense of permanence, but the truth is everything and everyone we love will eventually slip away, leaving us alone with the void we’ve been trying so hard to ignore. we build these elaborate lives, full of routines and habits, to convince ourselves that we’re in control, but it’s all just a fragile illusion. one bad day, one wrong turn, and everything could come crashing down, and then what? we’re left staring into the abyss, wondering if anything we did ever really mattered. and maybe that’s the cruelest joke of all: we spend our whole lives searching for meaning, for something to hold onto, but in the end, all we’re left with is the cold, hard reality that we’re just here for a brief moment, and then we’re gone. the world keeps turning, life goes on, and we’re nothing more than a memory, if we’re even that lucky. it’s a cycle that’s been repeating for millennia, and it will keep going long after we’re gone, indifferent to our hopes, our dreams, and our fears. and yet, here we are, trying to find some glimmer of hope in the darkness, even though we know it’s only temporary, like a flicker of light that’s bound to fade.",
                        name = node.name,
                        fields = node.fields.associate { it.name to it.type }
                    )
                )
                block.symbols.declare(
                    node.name,
                    Type.Struct(node.name, node.fields.associate { it.name to it.type })
                )
            }
            is TraitImplNode -> {
                node.type = handleUnknownReference(
                    block = block,
                    node = node,
                    type = node.type
                ) ?: node.type
                val name = "${node.trait}_trait_${node.type.signature}"

                block.symbols.define(name, node)
                val newBlock = repository.createBlock(
                    name,
                    block,
                    self = node.type
                )

                val traitSignature = signatures.traits.find { it.name == node.trait } ?: error("Trait ${node.trait} not found")
                traitSignature.impls.add(
                    SignatureTraitImpl(
                        trait = node.trait,
                        struct = node.type.signature,
                        index = null,
                        module = null,
                        types = node.functions.map { it.returnType },
                    )
                )
                node.functions.forEach {
                    val treatedType = handleUnknownReference(
                        block = newBlock,
                        node = it,
                        type = it.returnType
                    ) ?: it.returnType
                    newBlock.symbols.declare("${node.type.signature}_${it.name}", treatedType)
                    newBlock.symbols.define("${node.type.signature}_${it.name}", it)
                }
                node.functions.forEach { findSymbols(it, newBlock) }
                return newBlock
            }
            is TraitNode -> {
                val functions = node.functions.map {
                    SignatureFunction(
                        name = it.name,
                        returnType = it.returnType,
                        parameters = it.parameters.map { it.type }
                    )
                }

                block.symbols.define(node.name, node)
                block.symbols.declare(node.name, Type.Trait(
                    node.name,
                    functions.map { Type.VirtualFunction(
                        it.name,
                        it.returnType,
                        it.parameters
                    )}
                ))

                signatures.traits.add(
                    SignatureTrait(
                        name = node.name,
                        functions = functions
                    )
                )
            }
            is BlockNode -> {
                node.getChildren().forEach { findSymbols(it, block) }
            }
            is FunctionNode -> {
                node.returnType = handleUnknownReference(
                    block = block,
                    node = node,
                    type = node.returnType
                ) ?: node.returnType
                block.symbols.declare(node.name, node.returnType)
                block.symbols.define(node.name, node)

                signatures.functions.add(SignatureFunction(
                    name = node.name,
                    returnType = node.returnType,
                    parameters = node.parameters.map {
                        handleUnknownReference(
                            block = block,
                            node = it,
                            type = it.type
                        ) ?: it.type
                    }
                ))

                val block = repository.createBlock(node.name, block)
                node.parameters.forEach { findSymbols(it, block) }
                return block
            }
            is ParameterNode -> {
                node.type = handleUnknownReference(
                    block = block,
                    node = node,
                    type = node.type
                ) ?: node.type
                if (node.type == Type.Unknown) {
                    errors.add(AnalysisError.UnknownType(node, node.type))
                    return null
                }
                block.symbols.declare(node.name, node.type)
                node.getChildren().forEach { findSymbols(it, block) }
            }

            is AssignmentNode -> {
                val type = if (node.type == Type.Unknown) {
                    val expr = getExpressionType(block, node.expression, signatures)
                    if (expr is Either.Left) {
                        errors.add(expr.value)
                        return null
                    }
                    expr.unwrap()
                } else {
                    node.type
                }
                println("Type $type for ${node.expression}")
                if (node.mutable) {
                    block.symbols.declare(node.name, Type.Mutable(type))
                } else {
                    block.symbols.declare(node.name, type)
                }
                block.symbols.define(node.name, node)
                node.getChildren().forEach { findSymbols(it, block) }
            }
            is DataFieldNode -> {
                node.type = handleUnknownReference(
                    block = block,
                    node = node,
                    type = node.type
                ) ?: node.type
            }
            else -> {}
        }
        return null
    }

    fun analyzeNode(node: SyntaxTreeNode, block: MemoryBlock) {
        val deeperBlock: MemoryBlock = when (node) {
            is BinaryOperatorNode -> {
                val getLeftType = getExpressionType(block, node.left, signatures)
                val getRightType = getExpressionType(block, node.right, signatures)

                if (getLeftType is Either.Left) {
                    errors.add(getLeftType.value)
                    return
                } else if (getRightType is Either.Left) {
                    errors.add(getRightType.value)
                    return
                }
                val leftType = getLeftType.unwrap()
                val rightType = getRightType.unwrap()
                if (leftType != rightType) {
                    errors.add(AnalysisError.InvalidOperation(node, leftType, node.operator.kind, rightType))
                }
                block
            }

            is AssignmentNode -> {
                val symbol = block.figureOutSymbol(node.name)
                if (symbol == null) {
                    block.symbols.declare(node.name, node.type)
                    block.symbols.define(node.name, node)
                }
                block
            }

            is ForNode -> {
                block.symbols.declare(node.variable, Type.Int32)

                block
            }

            is FunctionNode -> {
                val functionBlock =
                    block.surfaceSearchChild(node.name) ?: error("Block ${node.name} not found")
                if (node.modifiers.contains(Modifiers.INTRINSIC)) {
                    return
                }
                val returnNode = node.body.getChildren().filter { it is ReturnNode }
                if (returnNode.isEmpty() && node.returnType != Type.Void) {
                    errors.add(AnalysisError.MissingReturnStatement(node))
                    return
                }

                if (returnNode.size > 1) {
                    // TODO: add warning

                }

                functionBlock
            }

            is IfNode -> {
                val conditionType = getExpressionType(block, node.condition, signatures)
                if (conditionType is Either.Left) {
                    errors.add(conditionType.value)
                    return
                }
                if (conditionType.unwrap() != Type.Boolean) {
                    errors.add(AnalysisError.InvalidCondition(node, conditionType.unwrap()))
                }
                block
            }
            is TraitImplNode -> {
                val traitBlock = block.surfaceSearchChild("${node.trait}_trait_${node.type.signature}")
                    ?: error("Block ${node.trait}_trait_${node.type.signature} not found")
                traitBlock
            }
            is MutationNode -> {
                val struct = getExpressionType(block, node.struct, signatures).getRightOrNull()
                if (struct == null) {
                    errors.add(AnalysisError.UndefinedVariable(node, node.struct.mark.value, block.name))
                    return
                }
                if (struct !is Type.Mutable && false) { // TODO: implement mut self
                    errors.add(AnalysisError.ImmutableVariableMutation(node, struct.signature))
                }
                block
            }

            is VariableReferenceNode -> {
                val symbol = if (node.name == "self") block.self else block.figureOutSymbol(node.name)
                if (symbol == null) {
                    errors.add(AnalysisError.UndefinedVariable(node, node.name, block.name))
                }
                block
            }

            is CallNode -> {
                val definition = block.figureOutDefinition(node.name) as FunctionNode?
                val signature = signatures.functions
                    .find { it.name == node.name }

                val (returnType, expectedParameters) = if (definition != null) {
                    definition.returnType to definition.parameters.map { it.type }
                } else if (signature != null) {
                    signature.returnType to signature.parameters
                } else {
                    errors.add(AnalysisError.UndefinedFunction(node, block.name))
                    return
                }

                if (!checkFunctionCall(node, returnType, expectedParameters, block)) {
                    return
                }
                block
            }

            is InstantiationNode -> {
                val struct = block.figureOutSymbol(node.name)?.workingBase()
                val signature = signatures.structs.find { it.name == node.name }

                if (struct == null && signature == null) {
                    errors.add(AnalysisError.UndefinedDataStructure(node, node.name))
                    return
                }

                if (struct != null) {
                    if (struct !is Type.Struct) {
                        errors.add(AnalysisError.InvalidStructAccess(node, struct))
                        return
                    }
                    if (struct.fields.size != node.arguments.size) {
                        errors.add(AnalysisError.MissingArgumentsForInstantiation(node, node.name))
                        return
                    }
                    // TODO: Check if the types match
                }
                if (signature != null) {
                    if (signature.fields.size != node.arguments.size) {
                        errors.add(AnalysisError.MissingArgumentsForInstantiation(node, node.name))
                        return
                    }
                    // TODO: Check if the types match
                }

                block
            }

            is StructAccessNode -> {
                if (isNodeSelf(node.struct)) return
                val struct = getExpressionType(block, node.struct, signatures).getRightOrNull()?.workingBase()
                if (struct == null) {
                    errors.add(AnalysisError.UndefinedDataStructure(node, node.struct.mark.value))
                    return
                }
                if (struct !is Type.Struct) {
                    errors.add(AnalysisError.InvalidStructAccess(node, struct))
                    return
                }
                val field = struct.fields[node.field]
                if (field == null) {
                    errors.add(AnalysisError.UndefinedField(node, node.field))
                    return
                }
                block
            }
            is ArrayAssignmentNode -> {
                val array = getExpressionType(block, node.array, signatures).getRightOrNull()
                if (array == null) {
                    errors.add(AnalysisError.UndefinedVariable(node, node.array.mark.value, block.name))
                    return
                }

                if (array !is Type.Mutable && false) { // TODO: implement mut self
                    errors.add(AnalysisError.ImmutableVariableMutation(node, array.signature))
                    return
                }

                block
            }
            is ReturnNode -> {
                val result = getExpressionType(block, node.expression, signatures)
                if (result is Either.Left) {
                    errors.add(result.value)
                    return
                }
                if (block.parent == null) {
                    errors.add(AnalysisError.ReturnOutsideFunction(node))
                    return
                }
                // TODO: remove _trait_ declarations
//                val expectedReturnType = block.parent.parent.figureOutSymbol(block.parent.name)
//                    ?: error("Symbol for function ${block.parent.name} not defined")
//                result.unwrap()
//                val receivedReturnType = result.unwrap()
//
//                // If the user is returning an unknown type, we will assume that the function is returning that type
//                val inferredType = if (receivedReturnType == Type.Unknown) {
//                    expectedReturnType
//                } else {
//                    receivedReturnType
//                }
//                if (!doTypesMatch(expectedReturnType, inferredType)) {
//                    errors.add(AnalysisError.ReturnTypeMismatch(node, expectedReturnType, inferredType))
//                    return
//                }
                block
            }

            else -> {
                block
            }
        }
        node.getChildren().forEach { analyzeNode(it, deeperBlock) }
    }

    fun doTypesMatch(required: Type, provided: Type): Boolean {
        if (required == Type.Any) {
            return true
        }
        if (required is Type.Mutable || provided is Type.Mutable) {
            if (provided is Type.Mutable && required !is Type.Mutable) {
                return doTypesMatch(required, provided.baseType)
            } else if (provided !is Type.Mutable) {
                return false
            }
        }
        if (required is Type.Trait && provided !is Type.Trait) {
            val traitSignature = signatures.traits.find { it.name == required.identifier } ?: return false
            return traitSignature.impls.any {
                it.struct == provided.signature
            }
        }
        return required == provided
    }

    fun handleUnknownReference(
        block: MemoryBlock,
        node: SyntaxTreeNode,
        type: Type
    ): Type? {
        when (type) {
            is Type.Mutable -> {
                val actualType = handleUnknownReference(block, node, type.baseType) ?: return null
                return Type.Mutable(actualType)
            }
            is Type.UnknownReference -> {
                val actualType = block.figureOutSymbol(type.reference)
                if (actualType == null || (actualType !is Type.Struct && actualType !is Type.Trait)) {
                    errors.add(AnalysisError.UnknownType(node, type))
                    return null
                }
                return actualType
            }
            is Type.FixedArray -> {
                val actualType = handleUnknownReference(block, node, type.baseType) ?: return null
                return Type.FixedArray(actualType, type.length)
            }

            is Type.DynamicArray -> {
                val actualType = handleUnknownReference(block, node, type.baseType) ?: return null
                return Type.DynamicArray(actualType)
            }
            else -> return type
        }
    }

    fun checkFunctionCall(
        node: CallNode,
        returnType: Type,
        expectedParameters: List<Type>,
        block: MemoryBlock
    ): Boolean {
        if (node.arguments.size != expectedParameters.size) {
            errors.add(
                AnalysisError.MissingArgumentsForFunctionCall(
                    node,
                    expectedParameters.size,
                    node.arguments.size
                )
            )
            return false
        }

        expectedParameters.forEachIndexed { index, parameter ->
            val paramNode = node.arguments[index]
            val argumentTypeResult = getExpressionType(block, paramNode, signatures)
            if (argumentTypeResult is Either.Left) {
                errors.add(argumentTypeResult.value)
                return false
            }
            var argumentType = argumentTypeResult.unwrap()
            // This means we can afford to cast the number to the expected type
            if (paramNode is NumberNode && !paramNode.explicit) {
                argumentType = expectedParameters[index]
                if (!argumentType.isNumeric()) {
                    errors.add(AnalysisError.WrongArgumentTypeForFunctionCall(
                        paramNode,
                        argumentType,
                        paramNode.type
                    ))
                    return false
                }
                paramNode.type = argumentType
                paramNode.value = paramNode.value.castToType(argumentType)
            }

            if (!doTypesMatch(parameter, argumentType)) {
                errors.add(
                    AnalysisError.WrongArgumentTypeForFunctionCall(
                        node,
                        parameter,
                        argumentType
                    )
                )
                return false
            }
        }
        return true
    }
}