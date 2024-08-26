package me.gabriel.gwydion.analysis.analyzers.impl

import me.gabriel.gwydion.analysis.AnalysisError
import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.AnalysisWarning
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.signature.SignatureFunction
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.analysis.util.unknownReferenceSignatureToType
import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.FunctionNode
import me.gabriel.gwydion.frontend.parsing.Modifiers
import me.gabriel.gwydion.frontend.parsing.ReturnNode

class FunctionAnalyzer: SingleNodeAnalyzer<FunctionNode>(FunctionNode::class) {
    override fun register(
        block: SymbolBlock,
        node: FunctionNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        node.returnType = unknownReferenceSignatureToType(signatures, node.returnType)

        signatures.functions.add(SignatureFunction(
            name = node.name,
            returnType = node.returnType,
            parameters = node.parameters.map { param ->
                unknownReferenceSignatureToType(signatures, param.type).also { treatedType ->
                    param.type = treatedType
                }
            }
        ))

        val functionBlock = SymbolBlock(
            name = node.name,
            parent = block,
            children = mutableListOf()
        )
        block.children.add(functionBlock)
        return functionBlock
    }

    override fun analyze(
        block: SymbolBlock,
        node: FunctionNode,
        signatures: Signatures,
        results: AnalysisResult
    ): SymbolBlock {
        val functionBlock =
            block.surfaceSearchChild(node.name) ?: error("Block for function ${node.name} was not created (:${block.name})")
        if (node.modifiers.contains(Modifiers.INTRINSIC)) {
            return functionBlock
        }
        val returnNode = node.body.getChildren().filter { it is ReturnNode }
        if (returnNode.isEmpty() && node.returnType != GwydionType.Void) {
            results.errors.add(AnalysisError.MissingReturnStatement(node))
            return functionBlock
        }

        if (returnNode.size > 1) {
            returnNode.dropLast(1).forEach {
                results.warnings.add(AnalysisWarning.UnreachableCode(it))
            }
        }

        return functionBlock
    }
}