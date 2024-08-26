package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.InstantiationNode
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode
import me.gabriel.gwydion.frontend.parsing.TypedSyntaxTreeNode
import me.gabriel.gwydion.frontend.parsing.VariableReferenceNode

class SymbolRepository {
    val root = SymbolBlock(
        "root",
        null,
        mutableListOf()
    )

    fun createBlock(
        name: String,
        parent: SymbolBlock,
        self: GwydionType? = parent.self
    ): SymbolBlock {
        val block = SymbolBlock(name, parent, mutableListOf(), self)
        parent.children.add(block)
        return block
    }
}

class SymbolBlock(
    val name: String,
    val parent: SymbolBlock?,
    val children: MutableList<SymbolBlock> = mutableListOf(),
    var self: GwydionType? = parent?.self
) {
    private val symbols = mutableMapOf<String, GwydionType>()
    private val definitions = mutableMapOf<SyntaxTreeNode, GwydionType>()

    fun createChild(
        name: String,
        self: GwydionType? = this.self
    ): SymbolBlock {
        val block = SymbolBlock(name, this, mutableListOf(), self)
        children.add(block)
        return block
    }

    fun surfaceSearchChild(name: String): SymbolBlock? {
        return children.find { it.name == name }
    }

    fun declareSymbol(name: String, type: GwydionType) {
        symbols[name] = type
    }

    fun defineSymbol(node: SyntaxTreeNode, type: GwydionType) {
        definitions[node] = type
    }

    fun resolveSymbol(name: String): GwydionType? {
        return symbols[name] ?: parent?.resolveSymbol(name)
    }

    fun resolveExpression(node: SyntaxTreeNode): GwydionType? {
        return when (node) {
            is TypedSyntaxTreeNode -> node.type
            is VariableReferenceNode -> resolveSymbol(node.name)
            else -> definitions[node] ?: parent?.resolveExpression(node)
        }
    }

    override fun toString(): String {
        return "MemoryBlock(name='$name', symbols=$symbols, parent=$parent)"
    }
}