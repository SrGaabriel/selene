package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.parsing.Type

class SymbolTable {
    private val scopes = mutableListOf(mutableMapOf<String, Type>())

    fun enterScope() {
        scopes.add(mutableMapOf())
    }

    fun exitScope() {
        scopes.removeAt(scopes.size - 1)
    }

    fun declare(name: String, type: Type) {
        if (type == Type.UNKNOWN) {
            println("Unknown type for $name")
        }
        scopes.last()[name] = type
    }

    fun lookup(name: String): Type? {
        for (scope in scopes.reversed()) {
            val type = scope[name]
            if (type != null) {
                return type
            }
        }
        return null
    }
}