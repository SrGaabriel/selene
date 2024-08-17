package me.gabriel.gwydion.compiler

import me.gabriel.gwydion.analyzer.SymbolTable
import me.gabriel.gwydion.llvm.struct.MemoryUnit
import me.gabriel.gwydion.parsing.SyntaxTreeNode
import me.gabriel.gwydion.parsing.Type

data class MemoryBlock(
    val name: String,
    val symbols: SymbolTable,
    val memory: MemoryTable,
    val parent: MemoryBlock?,
    val children: MutableList<MemoryBlock> = mutableListOf(),
    var self: Type? = null
) {
    fun surfaceSearchChild(name: String): MemoryBlock? {
        return children.find { it.name == name }
    }

    fun figureOutMemory(name: String): MemoryUnit? {
        val memory = memory.lookup(name)
        return memory ?: parent?.figureOutMemory(name)
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
        memory.allocateAll(other.memory)
        children.addAll(other.children)
    }

    override fun toString(): String {
        return "MemoryBlock(name='$name', symbols=$symbols, memory=$memory, parent=$parent)"
    }
}

class ProgramMemoryRepository {
    val root = MemoryBlock(
        "root",
        SymbolTable(),
        MemoryTable(),
        null,
        mutableListOf()
    )

    fun createBlock(
        name: String,
        parent: MemoryBlock,
        symbols: SymbolTable = SymbolTable(),
        table: MemoryTable = MemoryTable(),
        self: Type? = parent.self
    ): MemoryBlock {
        val block = MemoryBlock(name, symbols, table, parent, mutableListOf(), self)
        parent.children.add(block)
        return block
    }

    fun merge(other: ProgramMemoryRepository) {
        root.merge(other.root)
    }
}

class MemoryTable {
    private val memory = mutableMapOf<String, MemoryUnit>()

    fun allocate(name: String, unit: MemoryUnit): MemoryUnit {
        memory[name] = unit
        return unit
    }

    fun allocateAll(other: MemoryTable) {
        memory.putAll(other.memory)
    }

    fun lookup(name: String): MemoryUnit? {
        return memory[name]
    }

    override fun toString(): String = memory.toString()
}