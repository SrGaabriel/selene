package me.gabriel.gwydion.analysis.analyzers

import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode

class TypeInferenceVisitor {
    val queuedVisits = mutableListOf<Pair<SyntaxTreeNode, () -> Any>>()

    fun <T : Any> visit(node: SyntaxTreeNode, block: () -> T) {
        queuedVisits.add(node to block)
    }
}