package me.gabriel.selene.ir.intrinsics

import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.CallNode

class AllocateIntrinsic: IntrinsicFunction(
    "allocate"
) {
    override fun llvmIr(): String {
        return ""
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return ""
    }

    override fun declarations(): List<String> {
        TODO("Not yet implemented")
    }
}