package me.gabriel.gwydion.ir.intrinsics

import me.gabriel.gwydion.frontend.Type
import me.gabriel.gwydion.frontend.parsing.CallNode

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

    override fun handleCall(call: CallNode, types: Collection<Type>, arguments: String): String {
        val type = types.firstOrNull() ?: Type.Unknown
        return when (type) {
            Type.String -> "call void @println_str(${arguments})"
            Type.Int32 -> "call void @println_i32(${arguments})"
            Type.Float64 -> "call void @println_f64(${arguments})"
            Type.Boolean -> "call void @println_bool(${arguments})"
            else -> error("Unsupported type $type for intrinsic $name")
        }
    }

    override fun dependencies(): List<String> {
        return listOf(
            "declare i32 @printf(i8*, ...)",
            "declare i32 @putchar(i32)"
        )
    }
}