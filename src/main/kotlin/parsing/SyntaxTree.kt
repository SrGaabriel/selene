package me.gabriel.gwydion.parsing;

import me.gabriel.gwydion.lexing.TokenKind

data class SyntaxTree(val root: RootNode = RootNode(mutableListOf())) {
    fun addNode(node: SyntaxTreeNode) {
        root.addChild(node)
    }

    fun addAllNodes(nodes: List<SyntaxTreeNode>) {
        nodes.forEach { addNode(it) }
    }

    fun join(tree: SyntaxTree) {
        addAllNodes(tree.root.getChildren())
    }
}

sealed class SyntaxTreeNode {
    abstract fun getChildren(): List<SyntaxTreeNode>
}

open class BlockNode(private val children: MutableList<SyntaxTreeNode>): SyntaxTreeNode() {
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

class FunctionNode(
    val name: String,
    val parameters: ParametersNode,
    val block: BlockNode,
    val modifiers: MutableList<TokenKind>
): SyntaxTreeNode() {
    fun addModifier(modifier: TokenKind) {
        modifiers.add(modifier)
    }

    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(block)
}

class CallNode(val name: String, val parameters: CallParametersNode): SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> = parameters.parameters
}

class AssignmentNode(val name: String, val expression: SyntaxTreeNode): SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(expression)
}

class CompoundAssignmentNode(val name: String, val operator: TokenKind, val expression: SyntaxTreeNode): SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(expression)
}

class VariableNode(val name: String): ConstantNode()

class ParametersNode(val parameters: List<ParameterNode>): SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> = parameters
}

class ParameterNode(val name: String): SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class CallParametersNode(val parameters: List<SyntaxTreeNode>): SyntaxTreeNode() {
    override fun getChildren(): List<SyntaxTreeNode> = parameters
}

class StringNode(val value: String): ConstantNode()