package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.analysis.analyzers.ISemanticAnalyzer
import me.gabriel.gwydion.analysis.analyzers.TypeInferenceVisitor
import me.gabriel.gwydion.analysis.analyzers.impl.*
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.frontend.parsing.SyntaxTree
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode
import me.gabriel.gwydion.tools.GwydionLogger
import me.gabriel.gwydion.tools.LogLevel

class SemanticAnalysisManager(
    private val logger: GwydionLogger,
    private val symbols: SymbolRepository,
    private val signatures: Signatures,
) {
    private val analyzers = mutableListOf<ISemanticAnalyzer<out SyntaxTreeNode>>()
    private val unknown = mutableSetOf<String>()

    fun registerInternal() {
        registerAnalyzers(
            FunctionAnalyzer(),
            StructAnalyzer(),
            TraitAnalyzer(),
            TraitImplAnalyzer(),
            BinaryOpAnalyzer(),
            StructAccessAnalyzer(),
            AssignmentAnalyzer(),
            ParameterAnalyzer(),
            ArrayAnalyzer(),
            TraitFunctionCallAnalyzer(),
            InstantiationAnalyzer(),
            ArrayAccessAnalyzer(),
            DataFieldAnalyzer(),
            CallAnalyzer()
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
        val alreadyVisitedChildren = mutableSetOf<SyntaxTreeNode>()
        val analyzers = getAnalyzersFor(node)
        if (analyzers.isEmpty()) {
            unknown.add(node::class.simpleName!!)
        }

        val newBlock = analyzers.map { analyzer ->
            val visitor = TypeInferenceVisitor(node)

            val switchBlock = @Suppress("UNCHECKED_CAST")
                (analyzer as ISemanticAnalyzer<SyntaxTreeNode>).register(block, node, signatures, visitor)
            visitor.queuedVisits.forEach { (node, callback) ->
                alreadyVisitedChildren.add(node)
                registerNodeSymbols(switchBlock, node)
                callback()
            }
            switchBlock
        }.firstOrNull() ?: block

        (node.getChildren() - alreadyVisitedChildren).forEach {
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
        }.firstOrNull() ?: block

        node.getChildren().forEach {
            analyzeNode(newBlock, it, result)
        }
    }

    fun issueWarnings() {
        unknown.forEach { nodeName ->
            logger.log(LogLevel.WARN) { +"No analyzers found for node $nodeName" }
        }
    }
}