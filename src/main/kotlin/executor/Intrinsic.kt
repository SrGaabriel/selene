package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind

abstract class IntrinsicFunction(
    val name: String,
    val modifiers: MutableList<TokenKind>
) {
   abstract fun execute(parameters: List<Any>): Any

   abstract fun llvmIr(): String
}

class PrintFunction: IntrinsicFunction(
    "println",
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        println(parameters.first())
        return Unit
    }

    override fun llvmIr(): String {
        return "declare i32 @printf(i8*, ...)"
    }
}

class ReadFunction: IntrinsicFunction(
    "readln",
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        return readln()
    }

    override fun llvmIr(): String {
        return "declare i32 @scanf(i8*, ...)"
    }
}

class StringifyFunction: IntrinsicFunction(
    "stringify",
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        return parameters.first().toString()
    }

    override fun llvmIr(): String {
        return ""
    }
}