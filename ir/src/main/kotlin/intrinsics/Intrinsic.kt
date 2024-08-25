package me.gabriel.gwydion.ir.intrinsics

import me.gabriel.gwydion.frontend.Type
import me.gabriel.gwydion.frontend.parsing.CallNode

abstract class IntrinsicFunction(
    val name: String,
) {
    abstract fun llvmIr(): String

    abstract fun handleCall(call: CallNode, types: Collection<Type>, arguments: String): String

    abstract fun declarations(): List<String>

    open fun dependencies(): List<String> = emptyList()

}

val INTRINSICS = arrayOf(
    ReadlineFunction(),
    PrintlnFunction(),
    ArrayLengthFunction(),
    SinFunction(),
    CosFunction(),
    TanFunction(),
    AsinFunction(),
    SqrtFunction()
)
