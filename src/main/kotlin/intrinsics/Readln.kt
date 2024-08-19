package me.gabriel.gwydion.intrinsics

import me.gabriel.gwydion.parsing.CallNode
import me.gabriel.gwydion.parsing.Type


class ReadlineFunction: IntrinsicFunction(
    "readln",
) {
    override fun dependencies(): List<String> {
        return listOf(
            "@buffer = global [256 x i8] zeroinitializer",
            "declare i32 @getchar()"
        )
    }

    override fun declarations(): List<String> {
        return listOf("declare i8* @readln()")
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