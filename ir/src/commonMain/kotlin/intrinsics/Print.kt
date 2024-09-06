package me.gabriel.selene.ir.intrinsics

import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.CallNode
import me.gabriel.selene.llvm.LLVMCodeAssembler
import me.gabriel.selene.llvm.struct.Value

class PrintlnFunction: IntrinsicFunction(
    "println",
) {
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
            
            define void @println_f64(double %num) {
            entry:
                %format = getelementptr [4 x i8], [4 x i8]* @format_f, i32 0, i32 0
                call i32 (i8*, ...) @printf(i8* %format, double %num)
                call i32 @putchar(i32 10)
                ret void
            }

            define void @println_bool(i1 %bool) {
            entry:
                %format = getelementptr [3 x i8], [3 x i8]* @format_n, i32 0, i32 0
                %num = zext i1 %bool to i32
                call i32 (i8*, ...) @printf(i8* %format, i32 %num)
                call i32 @putchar(i32 10)
                ret void
            }
        """.trimIndent()
    }

    override fun declarations(): List<String> {
        return listOf(
            "declare void @println_str(i8*)",
            "declare void @println_i32(i32)",
            "declare void @println_bool(i1)",
            "declare void @println_f64(double)",
        )
    }

    override fun handleCall(
        call: CallNode,
        assignment: Value,
        types: Collection<SeleneType>,
        llvmArguments: Collection<Value>,
        assembler: LLVMCodeAssembler
    ) {
        val name = when (types.firstOrNull()) {
            SeleneType.String -> "println_str"
            SeleneType.Int32 -> "println_i32"
            SeleneType.Float64 -> "println_f64"
            SeleneType.Boolean -> "println_bool"
            else -> error("Unsupported type for println")
        }
        assembler.callFunction(
            name,
            llvmArguments,
            assignment,
            local = false
        )
    }

    override fun dependencies(): List<String> {
        return listOf(
            "declare i32 @printf(i8*, ...)",
            "declare i32 @putchar(i32)"
        )
    }
}