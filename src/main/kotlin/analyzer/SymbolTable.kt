package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.parsing.SyntaxTreeNode
import me.gabriel.gwydion.parsing.Type

class SymbolTable {
    private val symbols = mutableMapOf<String, Type>()
    private val definitions = mutableMapOf<String, SyntaxTreeNode>()

    fun declare(name: String, type: Type) {
        if (type == Type.Unknown) {
            println("Unknown type for $name")
        }
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
}