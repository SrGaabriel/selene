package me.gabriel.gwydion.analysis.analyzers

import me.gabriel.gwydion.analysis.AnalysisResult
import me.gabriel.gwydion.analysis.SymbolBlock
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode

interface ISemanticAnalyzer<T : SyntaxTreeNode> {
    fun handles(node: SyntaxTreeNode): Boolean

    fun register(
        block: SymbolBlock,
        node: T,
        signatures: Signatures,
        visitor: TypeInferenceVisitor
    ): SymbolBlock = block

    fun analyze(block: SymbolBlock, node: T, signatures: Signatures, results: AnalysisResult): SymbolBlock = block
}