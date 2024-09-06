package me.gabriel.selene.ir.intrinsics

import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.CallNode
import me.gabriel.selene.llvm.LLVMCodeAssembler
import me.gabriel.selene.llvm.struct.Value

abstract class IntrinsicFunction(
    val name: String,
) {
    abstract fun llvmIr(): String

    open fun handleCall(
        call: CallNode,
        assignment: Value,
        types: Collection<SeleneType>,
        llvmArguments: Collection<Value>,
        assembler: LLVMCodeAssembler
    ) = assembler.callFunction(
        name,
        llvmArguments,
        assignment,
        local = false
    )

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

    override fun handleCall(
        call: CallNode,
        assignment: Value,
        types: Collection<SeleneType>,
        llvmArguments: Collection<Value>,
        assembler: LLVMCodeAssembler
    ) = assembler.callFunction(
        llvmName,
        llvmArguments,
        assignment,
        local = false
    )

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
    AcosFunction(),
    AtanFunction(),
    Atan2Function(),
    SqrtFunction(),
    SocketFunction(),
    SocketBindFunction(),
    SocketListenFunction(),
    SocketAcceptFunction(),
    SocketReceiveFunction(),
    SocketSendFunction(),
    SocketCloseFunction()
)
