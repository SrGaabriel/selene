package me.gabriel.gwydion.ir.intrinsics

import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.CallNode

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
        return listOf(
            "declare i32 @str_length(i8*)",
            "declare i32 @array_len(i32*)"
        )
    }

    override fun handleCall(call: CallNode, types: Collection<GwydionType>, arguments: String): String {
        val type = types.firstOrNull()
        return when (type) {
            GwydionType.String -> return "call i32 @str_length(${arguments})"
            is GwydionType.FixedArray -> "add i32 ${type.length}, 0"
            else -> error("Invalid type (${type}) for arraylen function")
        }
    }
}