package me.gabriel.selene.analysis.analyzers.impl

import me.gabriel.selene.analysis.AnalysisError
import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.AnalysisWarning
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.analyzers.SingleNodeAnalyzer
import me.gabriel.selene.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.selene.analysis.signature.SignatureFunction
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.analysis.util.unknownReferenceSignatureToType
import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.FunctionNode
import me.gabriel.selene.frontend.parsing.Modifiers
import me.gabriel.selene.frontend.parsing.ReturnNode

class FunctionAnalyzer: SingleNodeAnalyzer<FunctionNode>(FunctionNode::class) {
    override fun register(
        block: SymbolBlock,
        node: FunctionNode,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock {
        node.returnType = unknownReferenceSignatureToType(signatures, node.returnType)

        signatures.functions.add(SignatureFunction(
            module = block.module,
            name = node.name,
            returnType = node.returnType,
            parameters = node.parameters.map { param ->
                unknownReferenceSignatureToType(signatures, param.type).also { treatedType ->
                    param.type = treatedType
                }
            },
            modifiers = node.modifiers
        ))

        val functionBlock = SymbolBlock(
            module = block.module,
            id = node,
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
            block.surfaceSearchChild(node) ?: error("Block for function ${node.name} was not created (:${block.id})")
        if (node.modifiers.contains(Modifiers.INTRINSIC)) {
            return functionBlock
        }
        val returnNode = node.body.getChildren().filter { it is ReturnNode }
        if (returnNode.isEmpty() && node.returnType != SeleneType.Void) {
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