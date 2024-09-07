package me.gabriel.selene.llvm.struct

sealed class Comparison(
    val number: String,
    val op: String,
    val type: LLVMType,
    val left: Value,
    val right: Value
) {
    sealed class Integer(
        op: String,
        left: Value,
        right: Value
    ): Comparison("icmp", op, left.type, left, right) {
        class Equal(left: Value, right: Value): Integer("eq", left, right)
        class NotEqual(left: Value, right: Value): Integer("ne", left, right)
        class SignedGreaterThan(left: Value, right: Value): Integer("sgt", left, right)
        class SignedGreaterThanOrEqual(left: Value, right: Value): Integer("sge", left, right)
        class SignedLessThan(left: Value, right: Value): Integer("slt", left, right)
        class SignedLessThanOrEqual(left: Value, right: Value): Integer("sle", left, right)
        class UnsignedGreaterThan(left: Value, right: Value): Integer("ugt", left, right)
        class UnsignedGreaterThanOrEqual(left: Value, right: Value): Integer("uge", left, right)
        class UnsignedLessThan(left: Value, right: Value): Integer("ult", left, right)
        class UnsignedLessThanOrEqual(left: Value, right: Value): Integer("ule", left, right)
    }

}