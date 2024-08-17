package me.gabriel.gwydion.llvm.struct

enum class BinaryOp(val llvm: String) {
    Addition("add"),
    Subtraction("sub"),
    Multiplication("mul"),
    Division("sdiv"),
}