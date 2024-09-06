package me.gabriel.selene.llvm

import me.gabriel.selene.llvm.struct.*

class LLVMCodeGenerator: ILLVMCodeGenerator {
    private val dependencies = mutableSetOf<String>()

    override fun stackMemoryAllocation(type: LLVMType, alignment: Int): String {
        if (type !is LLVMType.Pointer) {
            error("Cannot allocate stack memory for non-pointer type")
        }
        return "alloca ${type.type.llvm}, align $alignment"
    }

    override fun heapMemoryAllocation(type: LLVMType, size: Int): String {
        dependencies.add("declare i8* @malloc(i32)")
        return "call i8* @malloc(i32 ${size})"
    }

    override fun heapMemoryDefinition(size: Int, value: Value): String {
        dependencies.add("declare void @memset(i8*, i32, i32)")
        return "call void @memset(i8* ${value.llvm()}, i32 0, i32 ${size})"
    }

    override fun cast(value: Value, type: LLVMType): String {
        return "bitcast ${value.type.llvm} ${value.llvm()} to ${type.llvm}"
    }

    override fun signedIntegerComparison(left: Value, right: Value): String {
        return "icmp eq i32 ${left.llvm()}, ${right.llvm()}"
    }

    override fun signedIntegerNotEqualComparison(left: Value, right: Value): String {
        return "icmp ne i32 ${left.llvm()}, ${right.llvm()}"
    }

    override fun stringLengthCalculation(value: Value): String {
        dependencies.add("declare i32 @strlen(i8*)")
        return "call i32 @strlen(i8* %${value.llvm()})"
    }

    override fun strictStringLengthCalculation(argument: Value): String {
        dependencies.add("declare i32 @strlen(i8*)")
        return "call i32 @strlen(${argument.type.llvm} %${argument.llvm()})"
    }

    override fun functionCall(name: String, returnType: LLVMType, arguments: Collection<Value>, local: Boolean): String {
        val argsString = arguments.joinToString(", ") { "${it.type.llvm} ${it.llvm()}" }
        return "call ${returnType.llvm} ${if (local) '%' else '@'}$name($argsString)"
    }

    override fun comparison(comparison: Comparison): String {
        return "${comparison.number} ${comparison.op} ${comparison.type.llvm} ${comparison.left.llvm()}, ${comparison.right.llvm()}"
    }

    override fun memoryCopy(source: MemoryUnit, destination: MemoryUnit, size: Value): String {
        val sourceType = source.type.extractPrimitiveType()
        val destinationType = destination.type.extractPrimitiveType()

        dependencies.add("declare void @memcpy(i8*, i8*, i32)")
        return "call void @memcpy(${LLVMType.Pointer(sourceType).llvm} ${source.llvm()}, ${LLVMType.Pointer(destinationType).llvm} ${destination.llvm()}, i32 ${size.llvm()})"
    }

    override fun stringCopy(source: Value, destination: Value): String {
        dependencies.add("declare i8* @strcpy(i8*, i8*)")
        return "call i8* @strcpy(i8* ${destination.llvm()}, i8* ${source.llvm()})"
    }

    override fun unsafeSubElementAddressTotalReading(struct: Value, index: Value): String {
        val originalType = if (struct.type !is LLVMType.Pointer) {
            struct.type
        } else {
            (struct.type as LLVMType.Pointer).type
        }
        val pointerType = LLVMType.Pointer(originalType)
        return "getelementptr inbounds ${originalType.llvm}, ${pointerType.llvm} ${struct.llvm()}, i32 0, i32 ${index.llvm()}"
    }

    override fun unsafeSubElementAddressDirectReading(struct: Value, index: Value): String {
        val originalType = if (struct.type !is LLVMType.Pointer) {
            struct.type
        } else {
            (struct.type as LLVMType.Pointer).type
        }
        val pointerType = LLVMType.Pointer(originalType)
        return "getelementptr inbounds ${originalType.llvm}, ${pointerType.llvm} ${struct.llvm()}, i32 ${index.llvm()}"
    }

    override fun virtualTableReading(table: String, tableType: LLVMType, index: Value): String {
        return "getelementptr inbounds ${tableType.llvm}, ptr $table, i32 0, i32 ${index.llvm()}"
    }

    override fun returnInstruction(type: LLVMType, value: Value): String {
        if (value == LLVMVoid) {
            return "ret void"
        }
        return "ret ${type.llvm} ${value.llvm()}"
    }

    override fun conditionalBranch(condition: Value, trueLabel: String, falseLabel: String): String {
        return "br i1 ${condition.llvm()}, label %$trueLabel, label %$falseLabel"
    }

    override fun unconditionalBranchTo(label: String): String {
        return "br label %$label"
    }

    override fun stringComparison(left: Value, right: Value): String {
        dependencies.add("declare i32 @strcmp(i8*, i8*)")
        return "call i32 @strcmp(i8* ${left.llvm()}, i8* ${right.llvm()})"
    }

    override fun createBranch(label: String): String {
        return "$label:"
    }

    override fun functionDeclaration(name: String, returnType: LLVMType, arguments: List<MemoryUnit>): String {
        val argsString = arguments.joinToString(", ") { "${it.type.llvm} %${it.register}" }
        return """
            define ${returnType.llvm} @$name($argsString) {
            entry:
            """.trimIndent()
    }

    override fun concatenateStrings(left: Value, right: Value): String {
        dependencies.add("declare i8* @strcat(i8*, i8*)")
        return "call i8* @strcat(i8* ${right.llvm()}, i8* ${left.llvm()})"
    }

    override fun createTraitObject(obj: TraitObject): String {
        println("Object: ${obj.register}")
        return """
            @${obj.prefix}${obj.register} = unnamed_addr constant <{ i16, i16, ${obj.functions.joinToString(", ") { "ptr" }} }> <{
                i16 ${obj.size},
                i16 ${obj.alignment},
                ${obj.functions.joinToString(", \n") { "ptr @${obj.name}.$it" }}
            }>, align 8
        """.trimIndent()
    }

    override fun binaryOp(left: Value, op: BinaryOp, right: Value, type: LLVMType): String {
        val opLlvm = if (type == LLVMType.F32 || type == LLVMType.F64) {
            op.floatLlvm
        } else {
            op.llvm
        }
        return "$opLlvm ${type.llvm} ${left.llvm()}, ${right.llvm()}"
    }

    override fun storage(value: Value, address: MemoryUnit): String {
        return "store ${value.type.llvm} ${value.llvm()}, ${address.type.llvm} ${address.llvm()}"
    }

    override fun loadPointer(type: LLVMType, value: MemoryUnit): String {
        val pointerType = if (type is LLVMType.Ptr) {
            type.llvm
        } else {
            type.llvm + "*"
        }
        return "load ${type.llvm}, $pointerType ${value.llvm()}"
    }

    override fun virtualTableDeclaration(name: String, functions: List<LLVMType.Function>): String {
        val functionsString = functions.joinToString(", ") { "${it.returnType.llvm} (${it.parameterTypes.joinToString(", ") { it.llvm }})*" }
        return "type { $functionsString }"
    }

    override fun structDeclaration(fields: Collection<LLVMType>): String {
        val fieldsString = fields.joinToString(", ") { it.llvm }
        return "type { $fieldsString }"
    }

    override fun getGeneratedDependencies(): Set<String> = dependencies
}