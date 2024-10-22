package me.gabriel.selene.analysis

import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.*

class SymbolRepository(val module: String) {
    val root = SymbolBlock(
        module,
        null,
        null,
        mutableListOf()
    )

    fun createBlock(
        id: SyntaxTreeNode,
        parent: SymbolBlock,
        self: SeleneType? = parent.self
    ): SymbolBlock {
        val block = SymbolBlock(module, id, parent, mutableListOf(), self)
        parent.children.add(block)
        return block
    }
}

class SymbolBlock(
    val module: String,
    val id: SyntaxTreeNode?,
    val parent: SymbolBlock?,
    val children: MutableList<SymbolBlock> = mutableListOf(),
    var self: SeleneType? = parent?.self
) {
    private val symbols = mutableMapOf<String, SeleneType>()
    private val assignments = mutableMapOf<String, SyntaxTreeNode>()
    private val definitions = mutableMapOf<SyntaxTreeNode, SeleneType>()

    val name get() = when (id) {
        is FunctionNode -> id.name
        is TraitImplNode -> "(${id.type.signature} impls ${id.trait})"
        else -> id?.toString() ?: "root"
    }

    fun createChild(
        id: SyntaxTreeNode,
        self: SeleneType? = this.self
    ): SymbolBlock {
        val block = SymbolBlock(module, id, this, mutableListOf(), self)
        children.add(block)
        return block
    }

    fun surfaceSearchChild(id: SyntaxTreeNode): SymbolBlock? {
        return children.find { it.id == id }
    }

    fun declareSymbol(name: String, type: SeleneType) {
        symbols[name] = type
    }

    fun defineSymbol(node: SyntaxTreeNode, type: SeleneType) {
        definitions[node] = type
    }

    fun assignSymbol(name: String, node: SyntaxTreeNode) {
        assignments[name] = node
    }

    fun resolveSymbol(name: String): SeleneType? {
        return symbols[name] ?: parent?.resolveSymbol(name)
    }

    fun resolveAssignment(name: String): SyntaxTreeNode? {
        return assignments[name] ?: parent?.resolveAssignment(name)
    }

    fun resolveExpression(node: SyntaxTreeNode): SeleneType? {
        return when (node) {
            is TypedSyntaxTreeNode -> node.type
            is VariableReferenceNode -> resolveSymbol(node.name)
            else -> definitions[node] ?: parent?.resolveExpression(node)
        }
    }

    fun findCurrentFunction(): FunctionNode? {
        return when {
            id is FunctionNode -> id
            parent != null -> parent.findCurrentFunction()
            else -> null
        }
    }

    override fun toString(): String {
        return "SymbolBlock(module='$module', self=$self)"
    }
}