package me.gabriel.gwydion.compiler.llvm

import me.gabriel.gwydion.analyzer.getExpressionType
import me.gabriel.gwydion.compiler.MemoryBlock
import me.gabriel.gwydion.compiler.MemoryTable
import me.gabriel.gwydion.compiler.ProgramMemoryRepository
import me.gabriel.gwydion.exception.AnalysisError
import me.gabriel.gwydion.executor.IntrinsicFunction
import me.gabriel.gwydion.lexing.TokenKind
import me.gabriel.gwydion.parsing.*
import me.gabriel.gwydion.util.Either

class LLVMCodeGeneratorProcess(
    private val tree: SyntaxTree,
    private val repository: ProgramMemoryRepository,
    private val intrinsics: List<IntrinsicFunction>
) {
    private val ir = mutableListOf<String>()
    private var labelCounter = 0
    private var registerCounter = 1

    fun allocate(block: MemoryBlock, name: String, pointer: Int): Int {
        return block.memory.allocate(name, pointer)
    }

    fun lookup(block: MemoryBlock, name: String): Int? {
        return block.figureOutMemory(name)
    }

    fun setup() {
        intrinsics.forEach { intrinsic ->
            ir.add(intrinsic.llvmIr())
            val symbols = repository.root.symbols
            symbols.declare(intrinsic.name, intrinsic.returnType)
            intrinsic.params.forEachIndexed { index, type ->
                symbols.declare("param$index", type)
            }
        }
        ir.add("@format_s = private unnamed_addr constant [3 x i8] c\"%s\\00\"")
        ir.add("@format_n = private unnamed_addr constant [3 x i8] c\"%d\\00\"")
    }

    fun generateNode(node: SyntaxTreeNode, block: MemoryBlock): Int {
        return when (node) {
            is RootNode -> {
                node.getChildren().forEach { generateNode(it, block) }
                -1
            }
            is FunctionNode -> generateFunction(node)
            is BlockNode -> {
                node.getChildren().forEach { generateNode(it, block) }
                -1
            }
            is AssignmentNode -> generateAssignment(block, node)
            is BinaryOperatorNode -> generateBinaryOperator(block, node)
            is ReturnNode -> generateReturn(block, node)
            is CallNode -> generateFunctionCall(block, node)
            is IfNode -> generateIf(block, node)
            is BooleanNode -> generateBoolean(block, node)
            is StringNode -> generateString(block, node)
            is VariableReferenceNode -> generateVariableReference(block, node)
            is NumberNode -> generateNumber(block, node)
            else -> throw UnsupportedOperationException("Unsupported node type: ${node::class.simpleName}")
        }
    }

    fun generateBoolean(block: MemoryBlock, node: BooleanNode): Int {
        val resultReg = block.getNextRegister()
        ir.add("    %$resultReg = add i1 ${node.value}, 0")
        return resultReg
    }

    private fun generateFunction(node: FunctionNode): Int {
        val block = repository.root.surfaceSearchChild(node.name) ?: throw IllegalStateException("Undefined function: ${node.name}")
        val returnType = getLLVMType(node.returnType)
        val paramTypes = node.parameters.joinToString(", ") {
            val register = block.getNextRegister()
            block.memory.allocate(it.name, register)
            getLLVMType(it.type) + " %$register"
        }
        ir.add("define $returnType @${node.name}($paramTypes) {")
        ir.add("entry:")

        generateNode(node.body, block)

        if (node.returnType == Type.Void) {
            ir.add("    ret void")
        }

        ir.add("}")
        return -1
    }

    private fun generateAssignment(block: MemoryBlock, node: AssignmentNode): Int {
        val valueReg = generateNode(node.expression, block)
        val allocaReg = block.getNextRegister()
        val type = getLLVMType(if (node.type == Type.Unknown) {
            block.figureOutSymbol(node.name) ?: throw IllegalStateException("Undefined function: ${node.name}")
        } else {
            node.type
        })
        ir.add("    %$allocaReg = alloca $type")
        ir.add("    store $type %$valueReg, $type* %$allocaReg")
        allocate(block, node.name, valueReg)
        return valueReg
    }

    private fun generateBinaryOperator(block: MemoryBlock, node: BinaryOperatorNode): Int {
        val leftReg = generateNode(node.left, block)
        val rightReg = generateNode(node.right, block)
        val resultReg = block.getNextRegister()
        val instruction = when (node.operator) {
            TokenKind.PLUS -> "add"
            TokenKind.MINUS -> "sub"
            TokenKind.TIMES -> "mul"
            TokenKind.DIVIDE -> "sdiv"
            else -> throw UnsupportedOperationException("Unsupported binary operator: ${node.operator}")
        }
        ir.add("    %$resultReg = $instruction i32 %$leftReg, %$rightReg")
        return resultReg
    }

    private fun generateReturn(block: MemoryBlock, node: ReturnNode): Int {
        val valueReg = generateNode(node.expression, block)
        val type = getExpressionType(block, node.expression)
        if (type.isLeft()) {
            throw IllegalStateException("Unknown type for return value")
        }
        ir.add("    ret ${getLLVMType(type.unwrap())} %$valueReg")
        return -1
    }

    private fun generateFunctionCall(block: MemoryBlock, node: CallNode): Int {
        val argRegs = node.arguments.mapIndexed { index, syntaxTreeNode -> getExpressionType(block, syntaxTreeNode) to generateNode(syntaxTreeNode, block) }
        val functionType = block.figureOutSymbol(node.name) ?: throw IllegalStateException("Undefined function: ${node.name}")
        val resultReg = block.getNextRegister()
        var initialType: Type = Type.Unknown
        val argTypes = argRegs.joinToString(", ") {
            val (type, argReg) = it
            if (type.isLeft()) {
                throw IllegalStateException("Unknown type for argument")
            }
            if (initialType == Type.Unknown) {
                initialType = type.unwrap()
            }
            "${getLLVMType(type.unwrap())} %$argReg"
        }

        val intrinsic = intrinsics.find { it.name == node.name }
        val call = if (intrinsic != null) {
            intrinsic.handleCall(
                node,
                node.arguments.map { getExpressionType(block, it).unwrap() },
                argTypes
            )
        } else {
            "call ${getLLVMType(functionType)} @${node.name}($argTypes)"
        }

        if (functionType !== Type.Void) {
            ir.add("    %$resultReg = $call")
        } else {
            ir.add("    $call")
        }
        return resultReg
    }

    private fun generateIf(block: MemoryBlock, node: IfNode): Int {
        val conditionReg = generateNode(node.condition, block)
        val thenLabel = getNextLabel("then")
        val elseLabel = getNextLabel("else")
        val endLabel = getNextLabel("endif")

        ir.add("    %cmp = icmp ne i1 %$conditionReg, 0")
        ir.add("    br i1 %cmp, label %$thenLabel, label %$elseLabel")

        ir.add("$thenLabel:")
        generateNode(node.body, block)
        ir.add("    br label %$endLabel")

        ir.add("$elseLabel:")
        node.elseBody?.let { generateNode(it, block) }
        ir.add("    br label %$endLabel")

        ir.add("$endLabel:")
        return -1
    }

    private fun generateVariableReference(block: MemoryBlock, node: VariableReferenceNode): Int {
        val register = lookup(block, node.name)
        if (register == null) {
            val resultReg = block.getNextRegister()
            ir.add("    %$resultReg = load i32, i32* %$register")
            return resultReg
        }
        return register
    }

    private fun generateNumber(block: MemoryBlock, node: NumberNode): Int {
        val resultReg = block.getNextRegister()
        ir.add("    %$resultReg = add i32 ${node.value}, 0")
        return resultReg
    }

    private fun generateString(block: MemoryBlock, node: StringNode): Int {
        val pointerReg = block.getNextRegister()
        val length = node.value.length+1

        // Allocate space for the string
        ir.add("    %$pointerReg = alloca [$length x i8]")

        // Store each character individually
        node.value.forEachIndexed { index, char ->
            val charReg = block.getNextRegister()
            val ascii = char.code
            ir.add("    %$charReg = getelementptr inbounds [$length x i8], [$length x i8]* %$pointerReg, i32 0, i32 $index")
            ir.add("    store i8 $ascii, i8* %$charReg")
        }
        // null-terminate the string
        val nullReg = block.getNextRegister()
        ir.add("    %$nullReg = getelementptr inbounds [$length x i8], [$length x i8]* %$pointerReg, i32 0, i32 ${node.value.length}")

        // Return a pointer to the first character of the string
        val reg = block.getNextRegister()
        ir.add("    %$reg = getelementptr inbounds [$length x i8], [$length x i8]* %$pointerReg, i32 0, i32 0")

        return reg
    }

    private fun getNextLabel(prefix: String): String = "${prefix}_${labelCounter++}"

    private fun getLLVMType(type: Type): String {
        return when (type) {
            Type.Int32 -> "i32"
            Type.Boolean -> "i1"
            Type.String -> "i8*"
            Type.Void -> "void"
            else -> throw UnsupportedOperationException("Unsupported type: $type")
        }
    }

    fun finish(): String {
        return ir.joinToString("\n")
    }
}