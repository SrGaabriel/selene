package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.CallNode
import me.gabriel.gwydion.parsing.Type

abstract class IntrinsicFunction(
    val name: String,
    val params: List<Type>,
    val returnType: Type,
    val modifiers: MutableList<TokenKind>
) {
   abstract fun execute(parameters: List<Any>): Any

   abstract fun llvmIr(): String

   abstract fun handleCall(call: CallNode, types: Collection<Type>, arguments: String): String
}

class PrintFunction: IntrinsicFunction(
    "printf",
    listOf(Type.Any),
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

    override fun handleCall(call: CallNode, types: Collection<Type>, arguments: String): String {
        when (types.firstOrNull()) {
            Type.String -> {
                return "call i32 @printf(${arguments})"
            }
            Type.Int32 -> {
                return "call i32 @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @format_n, i32 0, i32 0), ${arguments})"
            }
            else -> error("Unknown type ${types.firstOrNull()}")
        }
    }
}

class PrintlnFunction: IntrinsicFunction(
    "println",
    listOf(Type.Any),
    Type.Void,
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        println(parameters.first())
        return Unit
    }

    override fun llvmIr(): String {
        return """
            define void @println_str(i8* %str) {
            entry:
                %format = alloca [4 x i8], align 1
                store [4 x i8] [i8 37, i8 115, i8 10, i8 0], [4 x i8]* %format, align 1
            
                %formatStr = getelementptr [4 x i8], [4 x i8]* %format, i32 0, i32 0
            
                call i32 @printf(i8* %formatStr, i8* %str)
            
                ret void
            }
            
            define void @println_i32(i32 %num) {
            entry:
                %format = getelementptr [3 x i8], [3 x i8]* @format_n, i32 0, i32 0
                call i32 (i8*, ...) @printf(i8* %format, i32 %num)
                ret void
            }
        """.trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<Type>, arguments: String): String {
        val type = types.firstOrNull() ?: Type.Unknown
        return when (type) {
            Type.String -> "call void @println_str(${arguments})"
            Type.Int32 -> "call void @println_i32(${arguments})"
            else -> error("Unknown type $type")
        }
    }
}