package me.gabriel.gwydion.intrinsics

import me.gabriel.gwydion.parsing.CallNode
import me.gabriel.gwydion.parsing.Type


class ArrayLengthFunction: IntrinsicFunction(
    name = "arraylen",
) {
    override fun llvmIr(): String {
        return """
            define i32 @str_length(i8* %str) {
            entry:
                %len = alloca i32
                store i32 0, i32* %len
                br label %loop
            
            loop:
                %idx = load i32, i32* %len
                %char_ptr = getelementptr inbounds i8, i8* %str, i32 %idx
                %char = load i8, i8* %char_ptr
                %is_null = icmp eq i8 %char, 0
                br i1 %is_null, label %end, label %next
            
            next:
                %next_idx = add i32 %idx, 1
                store i32 %next_idx, i32* %len
                br label %loop
            
            end:
                %final_len = load i32, i32* %len
                ret i32 %final_len
            }
           """
    }

    override fun dependencies(): List<String> {
        return listOf()
    }

    override fun declarations(): List<String> {
        return listOf("declare i32 @str_length(i8*)")
    }

    override fun handleCall(call: CallNode, types: Collection<Type>, arguments: String): String {
        return "call i32 @str_length(${arguments})"
    }
}