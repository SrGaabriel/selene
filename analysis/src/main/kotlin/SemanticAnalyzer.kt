package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode

interface SemanticAnalyzer {
    fun handles(node: SyntaxTreeNode): Boolean

    fun declare(node: SyntaxTreeNode) {}

    fun analyze(node: SyntaxTreeNode, results: AnalysisResult)
}