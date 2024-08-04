package me.gabriel.gwydion.compiler

import me.gabriel.gwydion.analyzer.SymbolTable
import me.gabriel.gwydion.parsing.Type

data class MemoryBlock(
    val name: String,
    val symbols: SymbolTable,
    val memory: MemoryTable,
    val parent: MemoryBlock?,
    val children: MutableList<MemoryBlock> = mutableListOf()
) {
    fun getNextRegister(): Int {
        return memory.registerCounter++
    }

    fun surfaceSearchChild(name: String): MemoryBlock? {
        return children.find { it.name == name }
    }

    fun figureOutMemory(name: String): Int? {
        val memory = memory.lookup(name)
        return memory ?: parent?.figureOutMemory(name)
    }

    fun figureOutSymbol(name: String): Type? {
        val symbol = symbols.lookup(name)
        return symbol ?: parent?.figureOutSymbol(name)
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
        table: MemoryTable = MemoryTable()
    ): MemoryBlock {
        val block = MemoryBlock(name, symbols, table, parent)
        parent.children.add(block)
        return block
    }
}

class MemoryTable {
    private val memory = mutableMapOf<String, Int>()
    var registerCounter = 1

    fun allocate(name: String, pointer: Int): Int {
        memory[name] = pointer
        return pointer
    }

    fun lookup(name: String): Int? {
        return memory[name]
    }
}