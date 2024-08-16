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
    var self: Type.Struct? = null
) {
    fun getNextRegister(): Int {
        return memory.registerCounter++
    }

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
        self: Type.Struct? = parent.self
    ): MemoryBlock {
        val block = MemoryBlock(name, symbols, table, parent, mutableListOf(), self)
        parent.children.add(block)
        return block
    }
}

class MemoryTable {
    private val memory = mutableMapOf<String, MemoryUnit>()
    var registerCounter = 1

    fun allocate(name: String, unit: MemoryUnit): MemoryUnit {
        memory[name] = unit
        return unit
    }

    fun lookup(name: String): MemoryUnit? {
        return memory[name]
    }
}