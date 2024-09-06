package me.gabriel.selene.ir.intrinsics

import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.CallNode
import me.gabriel.selene.llvm.LLVMCodeAssembler
import me.gabriel.selene.llvm.struct.LLVMConstant
import me.gabriel.selene.llvm.struct.LLVMType
import me.gabriel.selene.llvm.struct.Value

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

    override fun handleCall(
        call: CallNode,
        assignment: Value,
        types: Collection<SeleneType>,
        llvmArguments: Collection<Value>,
        assembler: LLVMCodeAssembler
    ) {
        when (val type = types.firstOrNull()) {
            SeleneType.String -> assembler.callFunction(
                "str_length",
                llvmArguments,
                assignment,
                local = false
            )
            is SeleneType.FixedArray -> assembler.addNumber(
                type = LLVMType.I32,
                left = LLVMConstant(
                    type = LLVMType.I32,
                    value = type.length
                ),
                right = LLVMConstant(
                    type = LLVMType.I32,
                    value = 0
                )
            )
            else -> error("Invalid type (${type}) for arraylen function")
        }
    }
}