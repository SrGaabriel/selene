package me.gabriel.selene.ir.intrinsics

import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.parsing.CallNode

class SinFunction: IntrinsicFunction(name = "sin") {
    override fun llvmIr(): String {
        return """""".trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return "call double @sin(${arguments})"
    }

    override fun declarations(): List<String> {
        return listOf("declare double @sin(double)")
    }
}

class CosFunction: IntrinsicFunction(name = "cos") {
    override fun llvmIr(): String {
        return """""".trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return "call double @cos(${arguments})"
    }

    override fun declarations(): List<String> {
        return listOf("declare double @cos(double)")
    }
}

class TanFunction: IntrinsicFunction(name = "tan") {
    override fun llvmIr(): String {
        return """""".trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return "call double @tan(${arguments})"
    }

    override fun declarations(): List<String> {
        return listOf("declare double @tan(double)")
    }
}

class AsinFunction: IntrinsicFunction(name = "asin") {
    override fun llvmIr(): String {
        return """""".trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return "call double @asin(double ${arguments})"
    }

    override fun declarations(): List<String> {
        return listOf("declare double @asin(double)")
    }
}

class AcosFunction: IntrinsicFunction(name = "acos") {
    override fun llvmIr(): String {
        return """""".trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return "call double @acos(${arguments})"
    }

    override fun declarations(): List<String> {
        return listOf("declare double @acos(double)")
    }
}

class AtanFunction: IntrinsicFunction(name = "atan") {
    override fun llvmIr(): String {
        return """""".trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return "call double @atan(${arguments})"
    }

    override fun declarations(): List<String> {
        return listOf("declare double @atan(double)")
    }
}

class Atan2Function: IntrinsicFunction(name = "atan2") {
    override fun llvmIr(): String {
        return """""".trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return "call double @atan2(double ${arguments})"
    }

    override fun declarations(): List<String> {
        return listOf("declare double @atan2(double)")
    }
}

class SqrtFunction: IntrinsicFunction(name = "sqrt") {
    override fun llvmIr(): String {
        return """""".trimIndent()
    }

    override fun handleCall(call: CallNode, types: Collection<SeleneType>, arguments: String): String {
        return "call double @sqrt(double ${arguments})"
    }

    override fun declarations(): List<String> {
        return listOf("declare double @sqrt(double)")
    }
}