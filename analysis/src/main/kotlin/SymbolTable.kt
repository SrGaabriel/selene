package me.gabriel.gwydion.analysis

import me.gabriel.gwydion.frontend.Type
import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode

class SymbolTable {
    @PublishedApi
    internal val symbols = mutableMapOf<String, Type>()
    private val definitions = mutableMapOf<String, SyntaxTreeNode>()

    fun declare(name: String, type: Type) {
        symbols[name] = type
    }

    fun define(name: String, node: SyntaxTreeNode) {
        definitions[name] = node
    }

    fun lookup(name: String): Type? {
        val type = symbols[name]
        if (type != null) {
            return type
        }
        return null
    }

    fun lookupDefinition(name: String): SyntaxTreeNode? {
        return definitions[name]
    }

    fun merge(other: SymbolTable) {
        other.symbols.forEach { (name, type) ->
            declare(name, type)
        }
        other.definitions.forEach { (name, node) ->
            define(name, node)
        }
    }

    inline fun <reified T : Type> filterIsInstance(): Collection<T> {
        return symbols.values.filterIsInstance<T>()
    }

    override fun toString(): String = symbols.toString()
}