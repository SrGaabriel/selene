package me.gabriel.gwydion.ir.intrinsics

import me.gabriel.gwydion.frontend.GwydionType
import me.gabriel.gwydion.frontend.parsing.CallNode

abstract class IntrinsicFunction(
    val name: String,
) {
    abstract fun llvmIr(): String

    abstract fun handleCall(call: CallNode, types: Collection<GwydionType>, arguments: String): String

    abstract fun declarations(): List<String>

    open fun dependencies(): List<String> = emptyList()

}

abstract class IntrinsicMirrorFunction(
    name: String,
    val llvmName: String = name,
    val params: String
) : IntrinsicFunction(name) {
    override fun llvmIr(): String {
        return ""
    }

    override fun handleCall(call: CallNode, types: Collection<GwydionType>, arguments: String): String {
        return "call $llvmName($arguments)"
    }

    override fun declarations(): List<String> {
        return listOf("declare $name($params)")
    }
}

val INTRINSICS = arrayOf(
    ReadlineFunction(),
    PrintlnFunction(),
    ArrayLengthFunction(),
    SinFunction(),
    CosFunction(),
    TanFunction(),
    AsinFunction(),
    SqrtFunction(),
    SocketFunction(),
    SocketBindFunction(),
    SocketListenFunction(),
    SocketAcceptFunction(),
    SocketReceiveFunction(),
    SocketSendFunction(),
    SocketCloseFunction()
)
