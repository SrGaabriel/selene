package me.gabriel.gwydion.parsing;

import me.gabriel.gwydion.lexing.Token
import me.gabriel.gwydion.lexing.TokenKind

data class SyntaxTree(val root: RootNode = RootNode(mutableListOf())) {
    fun addAllNodes(nodes: List<SyntaxTreeNode>) {
        root.addNodes(nodes)
    }

    fun join(tree: SyntaxTree) {
        addAllNodes(tree.root.getChildren())
    }
}

sealed class SyntaxTreeNode(
    val start: Token?,
    val end: Token?
) {
    abstract fun getChildren(): List<SyntaxTreeNode>
}

sealed class TypedSyntaxTreeNode(
    start: Token?,
    end: Token?,
    val type: Type
) : SyntaxTreeNode(start, end)

sealed class SizedSyntaxTreeNode(
    start: Token?,
    end: Token?,
    type: Type,
) : TypedSyntaxTreeNode(start, end, type) {
    abstract val size: Int
}

data class RootNode(private val children: MutableList<SyntaxTreeNode>) : SyntaxTreeNode(null, null) {
    fun addNode(node: SyntaxTreeNode) {
        children.add(node)
    }

    fun addNodes(nodes: List<SyntaxTreeNode>) {
        children.addAll(nodes)
    }

    override fun getChildren(): List<SyntaxTreeNode> = children
}

class FunctionNode(
    val name: String,
    val returnType: Type,
    val parameters: List<ParameterNode>,
    val body: BlockNode,
    val modifiers: MutableList<Modifiers>,
    start: Token
) : SyntaxTreeNode(start, body.end) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters + body
}

class BlockNode(
    private val children: List<SyntaxTreeNode>,
    start: Token,
    end: Token
) : SyntaxTreeNode(start, end) {
    override fun getChildren(): List<SyntaxTreeNode> = children
}

class ParameterNode(
    val name: String,
    type: Type,
    token: Token
) : TypedSyntaxTreeNode(token, token, type) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class AssignmentNode(
    val name: String,
    val expression: SyntaxTreeNode,
    type: Type,
    start: Token
) : TypedSyntaxTreeNode(start, expression.end, type) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(expression)
}

class BinaryOperatorNode(
    val left: SyntaxTreeNode,
    val operator: TokenKind,
    val right: SyntaxTreeNode,
) : SyntaxTreeNode(left.start, right.end) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(left, right)
}

class EqualsNode(
    val left: SyntaxTreeNode,
    val right: SyntaxTreeNode,
    start: Token?
) : SyntaxTreeNode(start, right.end) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(left, right)
}

class ReturnNode(
    val expression: SyntaxTreeNode,
    start: Token
) : SyntaxTreeNode(start, expression.end) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(expression)
}

class CallNode(
    val name: String,
    val arguments: List<SyntaxTreeNode>
) : SyntaxTreeNode(arguments.firstOrNull()?.start, arguments.lastOrNull()?.end) {
    override fun getChildren(): List<SyntaxTreeNode> = arguments
}

class CompoundAssignmentNode(
    val name: String,
    val operator: TokenKind,
    val expression: SyntaxTreeNode,
    start: Token
) : SyntaxTreeNode(start, expression.end) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(expression)
}

class VariableReferenceNode(
    val name: String,
    start: Token
) : SyntaxTreeNode(start, start) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class NumberNode(
    val value: String,
    type: Type,
    start: Token
) : SizedSyntaxTreeNode(start, start, type) {
    override val size: Int = value.length // todo: fix
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class StringNode(
    val value: String,
    val segments: List<Segment>,
    start: Token
) : SizedSyntaxTreeNode(start, start, Type.String) {
    override val size: Int = value.length
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    sealed class Segment {
        data class Text(val text: String) : Segment()
        data class Reference(val node: VariableReferenceNode) : Segment()
        data class Expression(val node: SyntaxTreeNode) : Segment()
    }
}

class BooleanNode(
    val value: Boolean,
    start: Token
) : TypedSyntaxTreeNode(start, start, Type.Boolean) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class IfNode(
    val condition: SyntaxTreeNode,
    val body: BlockNode,
    val elseBody: BlockNode?,
    start: Token
) : SyntaxTreeNode(start, body.end) {
    override fun getChildren(): List<SyntaxTreeNode> = listOfNotNull(condition, body, elseBody)
}

class ArrayNode(
    val elements: List<SyntaxTreeNode>,
    start: Token,
    end: Token
) : SyntaxTreeNode(start, end) {
    override fun getChildren(): List<SyntaxTreeNode> = elements
}

class ArrayAccessNode(
    val identifier: String,
    val index: SyntaxTreeNode,
    start: Token,
    end: Token
) : SyntaxTreeNode(start, end) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(index)
}