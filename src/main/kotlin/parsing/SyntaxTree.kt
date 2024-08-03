package me.gabriel.gwydion.parsing;

import me.gabriel.gwydion.lexing.TokenKind

data class SyntaxTree(val root: RootNode = RootNode(mutableListOf())) {
    fun addNode(node: SyntaxTreeNode) {
        root.addChild(node)
    }

    fun addAllNodes(nodes: List<SyntaxTreeNode>) {
        nodes.forEach { addNode(it) }
    }
}

sealed class SyntaxTreeNode {
    abstract fun getChildren(): List<SyntaxTreeNode>
}

abstract class BlockNode(private val children: MutableList<SyntaxTreeNode>): SyntaxTreeNode() {
    fun addChild(node: SyntaxTreeNode) {
        children.add(node)
    }

    override fun getChildren(): List<SyntaxTreeNode> = children
}

abstract class ConstantNode: SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class RootNode(children: MutableList<SyntaxTreeNode>): BlockNode(children)

class ReturnNode(val expression: SyntaxTreeNode): SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(expression)
}

class NumberNode(val value: Int): ConstantNode()

class BinaryOperatorNode(val left: SyntaxTreeNode, val operator: TokenKind, val right: SyntaxTreeNode): SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(left, right)
}