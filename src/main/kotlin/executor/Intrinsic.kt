package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.ParameterNode
import me.gabriel.gwydion.parsing.ParametersNode

abstract class IntrinsicFunction(
    val name: String,
    val parameters: ParametersNode,
    val modifiers: MutableList<TokenKind>
) {
   abstract fun execute(parameters: List<Int>): Int
}

class PrintFunction: IntrinsicFunction(
    "println",
    ParametersNode(mutableListOf(
        ParameterNode("value")
    )),
    mutableListOf()
) {
    override fun execute(parameters: List<Int>): Int {
        println(parameters.first())
        return 0
    }
}