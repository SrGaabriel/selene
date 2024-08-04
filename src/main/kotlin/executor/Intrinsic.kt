package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.Type

abstract class IntrinsicFunction(
    val name: String,
    val params: List<Type>,
    val returnType: Type,
    val modifiers: MutableList<TokenKind>
) {
   abstract fun execute(parameters: List<Any>): Any

   abstract fun llvmIr(): String
}

class PrintFunction: IntrinsicFunction(
    "printf",
    listOf(Type.String),
    Type.Int32,
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
    listOf(),
    Type.Int32,
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
    listOf(Type.Any),
    Type.String,
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        return parameters.first().toString()
    }

    override fun llvmIr(): String {
        return ""
    }
}