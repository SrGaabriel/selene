package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.exception.AnalysisError
import me.gabriel.gwydion.parsing.*
import me.gabriel.gwydion.signature.SignatureFunction
import me.gabriel.gwydion.signature.SignatureTrait
import me.gabriel.gwydion.signature.SignatureTraitImpl
import me.gabriel.gwydion.signature.Signatures
import me.gabriel.gwydion.util.Either

class CumulativeSemanticAnalyzer(
    private val tree: SyntaxTree,
    private val repository: ProgramMemoryRepository,
    private val signatures: Signatures
): SemanticAnalyzer {
    private val errors = mutableListOf<AnalysisError>()

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
                block.symbols.declare(
                    node.name,
                    Type.Struct(node.name, node.fields.associate { it.name to it.type }, false)
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
                println("Adding trait impl for ${node.trait} and ${node.type.signature}")
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
                return newBlock
            }
            is TraitNode -> {
                block.symbols.define(node.name, node)
                block.symbols.declare(node.name, Type.Trait(
                    node.name,
                    node.functions
                ))

                signatures.traits.add(
                    SignatureTrait(
                        name = node.name,
                        functions = node.functions.map {
                            SignatureFunction(
                                name = it.name,
                                returnType = it.returnType,
                                parameters = it.parameters.map { it.type }
                            )
                        }
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
                return repository.createBlock(node.name, block)
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
                    getExpressionType(block, node.expression, signatures).getRightOrNull() ?: Type.Unknown
                } else {
                    node.type
                }
                if (node.mutable && type is Type.Struct) {
                    block.symbols.declare(node.name, type.copy(mutable = true))
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
                    errors.add(AnalysisError.InvalidOperation(node, leftType, node.operator, rightType))
                }
                block
            }

            is FunctionNode -> {
                val functionBlock =
                    block.surfaceSearchChild(node.name) ?: error("Block ${node.name} not found")
                if (node.modifiers.contains(Modifiers.INTRINSIC)) {
                    return
                }

                val returnNode = node.body.getChildren().find { it is ReturnNode }
                val returnType = if (returnNode != null) {
                    val result = getExpressionType(functionBlock, (returnNode as ReturnNode).expression, signatures)
                    if (result is Either.Left) {
                        errors.add(result.value)
                        return
                    }
                    result.unwrap()
                } else {
                    Type.Void
                }
                // If the user is returning an unknown type, we will assume that the function is returning that type
                val inferredType = if (returnType == Type.Unknown) {
                    node.returnType
                } else {
                    returnType
                }
                if (inferredType != node.returnType) {
                    errors.add(AnalysisError.ReturnTypeMismatch(node, node.returnType, returnType))
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
                val variable = block.figureOutSymbol(node.struct)
                if (variable == null) {
                    errors.add(AnalysisError.UndefinedVariable(node, node.struct, block))
                    return
                }
                if (variable is Type.Struct && !variable.mutable) {
                    errors.add(AnalysisError.ImmutableVariableMutation(node, variable.id))
                }
                block
            }

            is VariableReferenceNode -> {
                val symbol = if (node.name == "self") block.self else block.figureOutSymbol(node.name)
                if (symbol == null) {
                    errors.add(AnalysisError.UndefinedVariable(node, node.name, block))
                }
                block
            }

            is CallNode -> {
                val function: Type? = block.figureOutSymbol(node.name)
                val definition = block.figureOutDefinition(node.name) as FunctionNode?
                if (function == null || definition == null) {
                    errors.add(AnalysisError.UndefinedFunction(node, block))
                    return
                }
                if (function != definition.returnType) {
                    errors.add(AnalysisError.ReturnTypeMismatch(node, definition.returnType, function))
                    return
                }

                if (node.arguments.size != definition.parameters.size) {
                    errors.add(
                        AnalysisError.MissingArgumentsForFunctionCall(
                            node,
                            definition.parameters.size,
                            node.arguments.size
                        )
                    )
                    return
                }

                definition.parameters.forEachIndexed { index, parameter ->
                    val argumentType = getExpressionType(block, node.arguments[index], signatures)
                    if (argumentType is Either.Left) {
                        errors.add(argumentType.value)
                        return
                    }
                    if (!doTypesMatch(parameter.type, argumentType.unwrap())) {
                        errors.add(
                            AnalysisError.WrongArgumentTypeForFunctionCall(
                                node,
                                parameter.type,
                                argumentType.unwrap()
                            )
                        )
                    }
                }
                block
            }

            is InstantiationNode -> {
                val struct = block.figureOutSymbol(node.name)
                if (struct == null) {
                    errors.add(AnalysisError.UndefinedDataStructure(node, node.name))
                    return
                }
                // Check if passed types match the struct
                if (struct is Type.Struct) {
                    if (struct.fields.size != node.arguments.size) {
                        errors.add(AnalysisError.MissingArgumentsForInstantiation(node, node.name))
                        return
                    }
                    // TODO: Check if the types match
                }
                block
            }

            is StructAccessNode -> {
                if (node.struct == "self") return
                val struct = block.figureOutSymbol(node.struct)
                if (struct == null) {
                    errors.add(AnalysisError.UndefinedDataStructure(node, node.struct))
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
        if (required is Type.Struct && provided is Type.Struct) {
            return required.id == provided.id && !(required.mutable && !provided.mutable) && required.fields == provided.fields
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
            is Type.UnknownReference -> {
                val actualType = block.figureOutSymbol(type.reference)
                if (actualType == null || (actualType !is Type.Struct && actualType !is Type.Trait)) {
                    errors.add(AnalysisError.UnknownType(node, type))
                    return null
                }
                if (actualType is Type.Struct) {
                    return actualType.copy(
                        mutable = type.mutable
                    )
                }
                return actualType
            }
            is Type.FixedArray -> {
                val actualType = handleUnknownReference(block, node, type.type) ?: return null
                return Type.FixedArray(actualType, type.length)
            }

            is Type.DynamicArray -> {
                val actualType = handleUnknownReference(block, node, type.type) ?: return null
                return Type.DynamicArray(actualType)
            }
            else -> return type
        }
    }
}