package me.gabriel.selene.analysis.analyzers

import me.gabriel.selene.frontend.parsing.SyntaxTreeNode

class TypeInferenceVisitor {
    val queuedVisits = mutableListOf<Pair<SyntaxTreeNode, () -> Any>>()

    fun <T : Any> visit(node: SyntaxTreeNode, block: () -> T) {
        queuedVisits.add(node to block)
    }
}