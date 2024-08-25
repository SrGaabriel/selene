package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.Type
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode

data class MemoryBlock(
    val name: String,
    val symbols: SymbolTable,
    val parent: MemoryBlock?,
    val children: MutableList<MemoryBlock> = mutableListOf(),
    var self: Type? = null
) {
    fun surfaceSearchChild(name: String): MemoryBlock? {
        return children.find { it.name == name }
    }

    fun figureOutSymbol(name: String): Type? {
        val symbol = symbols.lookup(name)
        return symbol ?: parent?.figureOutSymbol(name)
    }

    fun figureOutDefinition(name: String): SyntaxTreeNode? {
        val definition = symbols.lookupDefinition(name)
        return definition ?: parent?.figureOutDefinition(name)
    }

    fun merge(other: MemoryBlock) {
        symbols.merge(other.symbols)
        children.addAll(other.children)
    }

    override fun toString(): String {
        return "MemoryBlock(name='$name', symbols=$symbols, parent=$parent)"
    }
}

class ProgramMemoryRepository {
    val root = MemoryBlock(
        "root",
        SymbolTable(),
        null,
        mutableListOf()
    )

    fun createBlock(
        name: String,
        parent: MemoryBlock,
        symbols: SymbolTable = SymbolTable(),
        self: Type? = parent.self
    ): MemoryBlock {
        val block = MemoryBlock(name, symbols, parent, mutableListOf(), self)
        parent.children.add(block)
        return block
    }

    fun merge(other: ProgramMemoryRepository) {
        root.merge(other.root)
    }
}