package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.*

class SymbolRepository(val module: String,) {
    val root = SymbolBlock(
        module,
        null,
        null,
        mutableListOf()
    )

    fun createBlock(
        id: SyntaxTreeNode,
        parent: SymbolBlock,
        self: GwydionType? = parent.self
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
    var self: GwydionType? = parent?.self
) {
    private val symbols = mutableMapOf<String, GwydionType>()
    private val definitions = mutableMapOf<SyntaxTreeNode, GwydionType>()

    val name get() = when (id) {
        is FunctionNode -> id.name
        is TraitImplNode -> "(${id.type.signature} impls ${id.trait})"
        else -> id?.toString() ?: "root"
    }

    fun createChild(
        id: SyntaxTreeNode,
        self: GwydionType? = this.self
    ): SymbolBlock {
        val block = SymbolBlock(module, id, this, mutableListOf(), self)
        children.add(block)
        return block
    }

    fun surfaceSearchChild(id: SyntaxTreeNode): SymbolBlock? {
        return children.find { it.id == id }
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
        return "SymbolBlock(module='$module', self=$self)"
    }
}