package me.gabriel.gwydion.llvm.struct

enum class BinaryOp(val llvm: String, val floatLlvm: String) {
    Addition("add", "fadd"),
    Subtraction("sub", "fsub"),
    Multiplication("mul", "fmul"),
    Division("sdiv", "fdiv"),
    LessThan("slt", "olt"),
}