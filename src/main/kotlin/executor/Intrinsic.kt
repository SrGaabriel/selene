package me.gabriel.gwydion.executor

import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.llvm.struct.LLVMType
import me.gabriel.gwydion.parsing.CallNode
import me.gabriel.gwydion.parsing.Type

abstract class IntrinsicFunction(
    val name: String,
    val params: List<LLVMType>,
    val returnType: LLVMType,
    val modifiers: MutableList<TokenKind>
) {
   abstract fun execute(parameters: List<Any>): Any

   abstract fun llvmIr(): String

   abstract fun handleCall(call: CallNode, types: Collection<Type>, arguments: String): String

   open fun dependencies(): List<String> = emptyList()
}

class PrintFunction: IntrinsicFunction(
    "printf",
    listOf(LLVMType.Pointer(LLVMType.I8)),
    LLVMType.I32,
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
    listOf(LLVMType.Pointer(LLVMType.I8)),
    LLVMType.Void,
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
                call i32 @putchar(i32 10)
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

    override fun dependencies(): List<String> {
        return listOf("declare i32 @putchar(i32)")
    }
}

class ReadlineFunction: IntrinsicFunction(
    "readln",
    listOf(),
    LLVMType.Pointer(LLVMType.I8),
    mutableListOf()
) {
    override fun execute(parameters: List<Any>): Any {
        return readln()
    }

    override fun dependencies(): List<String> {
        return listOf(
            "@buffer = global [256 x i8] zeroinitializer",
            "declare i32 @getchar()"
        )
    }

    override fun llvmIr(): String {
        return """
define i8* @readln() {
entry:
    %buffer_ptr = getelementptr inbounds [256 x i8], [256 x i8]* @buffer, i32 0, i32 0
    %i = alloca i32
    store i32 0, i32* %i
    br label %read_loop

read_loop:
    %idx = load i32, i32* %i
    %char_ptr = getelementptr inbounds i8, i8* %buffer_ptr, i32 %idx
    %char = call i32 @getchar()
    %char_i8 = trunc i32 %char to i8
    store i8 %char_i8, i8* %char_ptr
    %is_newline = icmp eq i32 %char, 10
    %next_idx = add i32 %idx, 1
    store i32 %next_idx, i32* %i
    br i1 %is_newline, label %end_read, label %read_loop

end_read:
    %last_idx = sub i32 %next_idx, 1
    %last_char_ptr = getelementptr inbounds i8, i8* %buffer_ptr, i32 %last_idx
    store i8 0, i8* %last_char_ptr
    ret i8* %buffer_ptr
}

    """.trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<Type>, arguments: String): String {
        return "call i8* @readln()"
    }
}