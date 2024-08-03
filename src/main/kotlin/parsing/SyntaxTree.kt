package me.gabriel.gwydion.parsing;

import me.gabriel.gwydion.lexing.Token
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

interface AlgebraicNode

sealed class SyntaxTreeNode(
    val startToken: Token?,
    val endToken: Token?
) {
    abstract fun getChildren(): List<SyntaxTreeNode>
}

open class BlockNode(
    startToken: Token,
    endToken: Token,
    private val children: MutableList<SyntaxTreeNode>
): SyntaxTreeNode(startToken, endToken) {
    fun addChild(node: SyntaxTreeNode) {
        children.add(node)
    }

    override fun getChildren(): List<SyntaxTreeNode> = children
}

abstract class ConstantNode(
    token: Token,
    val type: Type
): SyntaxTreeNode(token, token) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class RootNode(
    private val children: MutableList<SyntaxTreeNode>
): SyntaxTreeNode(null, null) {
    fun addChild(node: SyntaxTreeNode) {
        children.add(node)
    }

    override fun getChildren(): List<SyntaxTreeNode> = children
}

class ReturnNode(
    token: Token,
    val expression: SyntaxTreeNode
): SyntaxTreeNode(token, expression.endToken) {
    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(expression)
}

class NumberNode(val value: String, type: Type, token: Token): ConstantNode(token, type), AlgebraicNode

class BinaryOperatorNode(
    val left: SyntaxTreeNode,
    val operator: TokenKind,
    val right: SyntaxTreeNode
): SyntaxTreeNode(
    left.startToken,
    right.endToken
), AlgebraicNode {
    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(left, right)
}

class FunctionNode(
    val name: String,
    val parameters: ParametersNode,
    val block: BlockNode,
    val modifiers: MutableList<TokenKind>,
    val returnType: Type,
    startToken: Token
): SyntaxTreeNode(
    startToken,
    block.endToken
) {
    fun addModifier(modifier: TokenKind) {
        modifiers.add(modifier)
    }

    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(block, parameters)
}

class CallNode(val name: String, val parameters: CallParametersNode): SyntaxTreeNode(parameters.startToken, parameters.endToken) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters.parameters
}

class AssignmentNode(val name: String, val expression: SyntaxTreeNode, variableToken: Token): SyntaxTreeNode(
    variableToken,
    expression.endToken
) {
    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(expression)
}

class CompoundAssignmentNode(
    val name: String, val operator: TokenKind, val expression: SyntaxTreeNode,
    variableToken: Token
): SyntaxTreeNode(variableToken, expression.endToken) {
    override fun getChildren(): List<SyntaxTreeNode> =
        listOf(expression)
}

class VariableNode(val name: String, token: Token, type: Type): ConstantNode(token, type)

class ParametersNode(val parameters: List<ParameterNode>): SyntaxTreeNode(
    parameters.firstOrNull()?.startToken,
    parameters.lastOrNull()?.endToken
) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters
}

class ParameterNode(val name: String, token: Token, type: Type): ConstantNode(token, type)

class CallParametersNode(val parameters: List<SyntaxTreeNode>): SyntaxTreeNode(
    parameters.firstOrNull()?.startToken,
    parameters.lastOrNull()?.endToken
) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters
}

class StringNode(val value: String, token: Token, type: Type): ConstantNode(token, type)

class TypeNode(val name: String, type: Type, token: Token): ConstantNode(token, type)

enum class Type {
    INT64,
    INT32,
    INT16,
    INT8,
    FLOAT32,
    FLOAT64,
    UINT64,
    UINT32,
    UINT16,
    UINT8,
    STRING,
    VOID,
    UNKNOWN
}