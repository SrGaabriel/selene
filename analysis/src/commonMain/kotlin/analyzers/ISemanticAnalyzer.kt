package me.gabriel.selene.analysis.analyzers

import me.gabriel.selene.analysis.AnalysisResult
import me.gabriel.selene.analysis.SymbolBlock
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.parsing.SyntaxTreeNode

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