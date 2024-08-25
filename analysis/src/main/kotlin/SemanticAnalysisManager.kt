package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.analysis.analyzers.ISemanticAnalyzer
import me.gabriel.gwydion.analysis.analyzers.impl.FunctionAnalyzer
import me.gabriel.gwydion.analysis.analyzers.impl.StructAnalyzer
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.parsing.SyntaxTree
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode

class SemanticAnalysisManager(
    private val symbols: SymbolRepository,
    private val signatures: Signatures
) {
    private val analyzers = mutableListOf<ISemanticAnalyzer<out SyntaxTreeNode>>()

    fun registerInternal() {
        registerAnalyzers(
            FunctionAnalyzer(),
            StructAnalyzer()
        )
    }

    fun registerAnalyzer(analyzer: ISemanticAnalyzer<out SyntaxTreeNode>) {
        analyzers.add(analyzer)
    }

    fun registerAnalyzers(vararg analyzers: ISemanticAnalyzer<out SyntaxTreeNode>) {
        analyzers.forEach {
            registerAnalyzer(it)
        }
    }

    fun getAnalyzersFor(node: SyntaxTreeNode): List<ISemanticAnalyzer<out SyntaxTreeNode>> {
        return analyzers.filter { it.handles(node) }
    }

    fun registerTreeSymbols(tree: SyntaxTree) {
        registerNodeSymbols(symbols.root, tree.root)
    }

    fun registerNodeSymbols(block: SymbolBlock, node: SyntaxTreeNode) {
        val newBlock = getAnalyzersFor(node).map { analyzer ->
            @Suppress("UNCHECKED_CAST")
            (analyzer as ISemanticAnalyzer<SyntaxTreeNode>).register(block, node, signatures)
        }.first()

        node.getChildren().forEach {
            registerNodeSymbols(newBlock, it)
        }
    }

    fun analyzeTree(tree: SyntaxTree): AnalysisResult {
        val result = AnalysisResult()
        analyzeNode(symbols.root, tree.root, result)
        return result
    }

    fun analyzeNode(block: SymbolBlock, node: SyntaxTreeNode, result: AnalysisResult) {
        val newBlock = getAnalyzersFor(node).map { analyzer ->
            @Suppress("UNCHECKED_CAST")
            (analyzer as ISemanticAnalyzer<SyntaxTreeNode>).analyze(block, node, signatures, result)
        }.first()

        node.getChildren().forEach {
            analyzeNode(newBlock, it, result)
        }
    }
}