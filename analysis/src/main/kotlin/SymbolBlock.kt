package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode

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

data class SymbolBlock(
    val name: String,
    val parent: SymbolBlock?,
    val children: MutableList<SymbolBlock> = mutableListOf(),
    var self: GwydionType? = parent?.self
) {
    private val symbols = mutableMapOf<String, GwydionType>()
    private val definitions = mutableMapOf<SyntaxTreeNode, GwydionType>()

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
        return definitions[node] ?: parent?.resolveExpression(node)
    }

    override fun toString(): String {
        return "MemoryBlock(name='$name', symbols=$symbols, parent=$parent)"
    }
}