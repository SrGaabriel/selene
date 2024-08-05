package me.gabriel.gwydion.llvm

import me.gabriel.gwydion.llvm.struct.*

class LLVMCodeGenerator: ILLVMCodeGenerator {
    private val dependencies = mutableSetOf<String>()

    override fun stackMemoryAllocation(type: LLVMType, alignment: Int): String {
        return "alloca ${type.llvm}, align $alignment"
    }

    override fun heapMemoryAllocation(type: LLVMType, size: Int): String {
        return "call i8* @malloc(${type.llvm} ${size})"
    }

    override fun addition(left: Value, right: Value, type: LLVMType): String {
        return "add $type %${left.llvm()}, %${right.llvm()}"
    }

    override fun signedIntegerComparison(left: Value, right: Value): String {
        return "icmp slt i1 %${left.llvm()}, %${right.llvm()}"
    }

    override fun stringLengthCalculation(value: Value): String {
        dependencies.add("declare i32 @strlen(i8*)")
        return "call i32 @strlen(i8* %${value.llvm()})"
    }

    override fun strictStringLengthCalculation(argument: Value): String {
        dependencies.add("declare i32 @strlen(i8*)")
        return "call i32 @strlen(${argument.type.llvm} %${argument.llvm()})"
    }

    override fun functionCall(name: String, returnType: LLVMType, arguments: Collection<Value>): String {
        val argsString = arguments.joinToString(", ") { "${it.type} ${it.llvm()}" }
        return "call ${returnType.llvm} @$name($argsString)"
    }

    override fun memoryCopy(source: LLVMType.Pointer, destination: LLVMType.Pointer, size: Value): String {
        dependencies.add("declare void @memcpy(i8*, i8*, i32)")
        return "call void @memcpy(${source.llvm} %${source}, ${destination.llvm} %${destination.llvm}, i32 %${size.llvm()})"
    }

    override fun unsafeSubElementAddressReading(struct: Value, index: Value): String {
        return "getelementptr inbounds ${struct.type.llvm}, ${struct.type.llvm}* ${struct.llvm()}, i32 0, i32 ${index.llvm()}"
    }

    override fun returnInstruction(type: LLVMType, value: Value): String {
        if (value == LLVMVoid) {
            return "ret void"
        }
        return "ret ${type.llvm} %${value.llvm()}"
    }

    override fun conditionalBranch(condition: Value, trueLabel: String, falseLabel: String): String {
        return "br i1 %${condition.llvm()}, label %$trueLabel, label %$falseLabel"
    }

    override fun unconditionalBranchTo(label: String): String {
        return "br label %$label"
    }

    override fun createBranch(label: String): String {
        return "$label:"
    }

    override fun functionDeclaration(name: String, returnType: LLVMType, arguments: List<MemoryUnit>): String {
        val argsString = arguments.joinToString(", ") { "${it.type} %${it.register}" }
        return """
            define ${returnType.llvm} @$name($argsString) {
            entry:
            """.trimIndent()
    }

    override fun addNumber(type: LLVMType, left: Value, right: Value): String {
        return "add ${type.llvm} ${left.llvm()}, ${right.llvm()}"
    }

    override fun storage(value: Value, address: MemoryUnit): String {
        return "store ${value.type.llvm} ${value.llvm()}, ${address.type.llvm} ${address.llvm()}"
    }

    override fun getGeneratedDependencies(): Set<String> = dependencies
}