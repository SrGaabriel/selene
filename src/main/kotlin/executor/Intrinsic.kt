package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.ParameterNode
import me.gabriel.gwydion.parsing.ParametersNode

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