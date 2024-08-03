package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind

abstract class IntrinsicFunction(
    val name: String,
    val modifiers: MutableList<TokenKind>
) {
   abstract fun execute(parameters: List<Any>): Any
}

class PrintFunction: IntrinsicFunction(
    "println",
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        println(parameters.first())
        return Unit
    }
}

class ReadFunction: IntrinsicFunction(
    "readln",
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        return readln()
    }
}

class StringifyFunction: IntrinsicFunction(
    "stringify",
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        return parameters.first().toString()
    }
}