package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.exception.AnalysisResult
import me.gabriel.gwydion.parsing.SyntaxTreeNode

interface SemanticAnalyzer {
    fun handles(node: SyntaxTreeNode): Boolean

    fun declare(node: SyntaxTreeNode) {}

    fun analyze(node: SyntaxTreeNode, results: AnalysisResult)
}