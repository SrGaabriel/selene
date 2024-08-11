package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.parsing.Type

class SymbolTable {
    private val symbols = mutableMapOf<String, Type>()

    fun declare(name: String, type: Type) {
        if (type == Type.Unknown) {
            println("Unknown type for $name")
        }
        symbols[name] = type
    }

    fun lookup(name: String): Type? {
        val type = symbols[name]
        if (type != null) {
            return type
        }
        return null
    }

    fun merge(other: SymbolTable) {
        other.symbols.forEach { (name, type) ->
            declare(name, type)
        }
    }
}