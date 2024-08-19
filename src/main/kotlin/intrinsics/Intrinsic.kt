package me.gabriel.gwydion.intrinsics

import me.gabriel.gwydion.parsing.CallNode
import me.gabriel.gwydion.parsing.Type

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
