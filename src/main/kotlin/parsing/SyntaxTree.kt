package me.gabriel.gwydion.parsing

import kotlinx.serialization.Serializable
import me.gabriel.gwydion.lexing.Token
import me.gabriel.gwydion.lexing.TokenKind

data class SyntaxTree(val root: RootNode = RootNode(mutableListOf())) {
    fun addAllNodes(nodes: List<SyntaxTreeNode>) {
        root.addNodes(nodes)
    }
}

sealed class SyntaxTreeNode(val mark: Token) {
    abstract fun getChildren(): List<SyntaxTreeNode>
}

sealed class TypedSyntaxTreeNode(open var type: Type, mark: Token) : SyntaxTreeNode(mark)

class RootNode(private val children: MutableList<SyntaxTreeNode>) : SyntaxTreeNode(Token(TokenKind.BOF, "", 0)) {
    fun addNode(node: SyntaxTreeNode) {
        children.add(node)
    }

    fun addNodes(nodes: List<SyntaxTreeNode>) {
        children.addAll(nodes)
    }

    override fun getChildren(): List<SyntaxTreeNode> = children
}

class FunctionNode(
    val name: String,
    var returnType: Type,
    val parameters: List<ParameterNode>,
    val body: BlockNode,
    val modifiers: MutableList<Modifiers>,
    val blockName: String = name,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters + body

    fun copy(
        name: String = this.name,
        returnType: Type = this.returnType,
        parameters: List<ParameterNode> = this.parameters,
        body: BlockNode = this.body,
        modifiers: MutableList<Modifiers> = this.modifiers,
        blockName: String = this.blockName,
        mark: Token = this.mark
    ) = FunctionNode(name, returnType, parameters, body, modifiers, blockName, mark)
}

class BlockNode(
    private val children: List<SyntaxTreeNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = children
}

class ParameterNode(
    val name: String,
    type: Type,
    mark: Token
) : TypedSyntaxTreeNode(type, mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class AssignmentNode(
    val name: String,
    val expression: SyntaxTreeNode,
    val mutable: Boolean,
    type: Type,
    mark: Token
) : TypedSyntaxTreeNode(type, mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(expression)

    override fun toString(): String = "AssignmentNode(name='$name', expression=$expression, mutable=$mutable)"
}

class BinaryOperatorNode(
    val left: SyntaxTreeNode,
    val operator: Token,
    val right: SyntaxTreeNode,
) : SyntaxTreeNode(operator) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(left, right)

    override fun toString(): String = "BinaryOperatorNode(left=$left operator=$operator right=$right)"
}

class EqualsNode(
    val left: SyntaxTreeNode,
    val right: SyntaxTreeNode,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(left, right)
}

class ReturnNode(
    val expression: SyntaxTreeNode,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(expression)
}

class CallNode(
    val name: String,
    val arguments: List<SyntaxTreeNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = arguments
}

class CompoundAssignmentNode(
    val name: String,
    val operator: Token,
    val expression: SyntaxTreeNode,
) : SyntaxTreeNode(operator) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(expression)
}

class VariableReferenceNode(
    val name: String,
    mark: Token
) : SyntaxTreeNode(mark) {

    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    override fun toString(): String = "VariableReferenceNode(name='$name')"
}

class NumberNode(
    var value: String,
    val explicit: Boolean,
    override var type: Type,
    mark: Token
) : TypedSyntaxTreeNode(type, mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()
}

class StringNode(
    val value: String,
    val segments: List<Segment>,
    mark: Token
) : TypedSyntaxTreeNode(Type.String, mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    sealed class Segment {
        data class Text(val text: String) : Segment()
        data class Reference(val node: VariableReferenceNode) : Segment()
        data class Expression(val node: SyntaxTreeNode) : Segment()
    }
}

class BooleanNode(
    val value: Boolean,
    mark: Token
) : TypedSyntaxTreeNode(Type.Boolean, mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    override fun toString(): String = "BooleanNode(value=$value)"
}

class IfNode(
    val condition: SyntaxTreeNode,
    val body: BlockNode,
    val elseBody: BlockNode?,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOfNotNull(condition, body, elseBody)

    override fun toString(): String = "IfNode(condition=$condition, body=$body, elseBody=$elseBody)"
}

class ArrayNode(
    val elements: List<SyntaxTreeNode>,
    val dynamic: Boolean,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = elements

    override fun toString(): String = "ArrayNode(elements=$elements, dynamic=$dynamic)"
}

class ArrayAccessNode(
    val array: SyntaxTreeNode,
    val index: SyntaxTreeNode,
    mark: Token,
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(index)

    override fun toString(): String = "ArrayAccessNode(array='$array', index=$index)"
}

class DataStructureNode(
    val name: String,
    val fields: List<DataFieldNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = fields

    override fun toString(): String = "DataStructureNode(name='$name', fields=$fields)"
}

class DataFieldNode(
    val name: String,
    var type: Type,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf()

    override fun toString(): String = "DataFieldNode(name='$name', type=$type)"
}

class TraitNode(
    val name: String,
    val functions: List<TraitFunctionNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = functions

    override fun toString(): String = "TraitNode(name='$name', functions=$functions)"
}

class TraitFunctionNode(
    val name: String,
    val returnType: Type,
    val parameters: List<ParameterNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters

    override fun toString(): String = "TraitFunctionNode(name='$name', returnType=$returnType, parameters=$parameters)"
}

class TraitImplNode(
    var type: Type,
    val trait: String,
    val functions: List<FunctionNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = functions

    override fun toString(): String = "TraitImplNode(type=$type, trait='$trait', functions=$functions)"
}

class InstantiationNode(
    val name: String,
    val arguments: List<SyntaxTreeNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = arguments

    override fun toString(): String = "InstantiationNode(name='$name', arguments=$arguments)"
}

class StructAccessNode(
    val struct: SyntaxTreeNode,
    val field: String,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf()

    override fun toString(): String = "StructAccessNode(struct='$struct', field='$field')"
}

class MutationNode(
    val struct: String,
    val field: String,
    val expression: SyntaxTreeNode,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(expression)

    override fun toString(): String = "MutationNode(struct='$struct', field='$field', expression=$expression)"
}

class TraitFunctionCallNode(
    val trait: SyntaxTreeNode,
    val function: String,
    val arguments: List<SyntaxTreeNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = arguments

    override fun toString(): String = "TraitFunctionCallNode(trait='$trait', function='$function', arguments=$arguments)"
}

class ForNode(
    val variable: String,
    val iterable: RangeNode, // todo: change to SyntaxTreeNode
    val body: BlockNode,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(iterable, body)

    override fun toString(): String = "ForNode(variable='$variable', iterable=$iterable, body=$body)"
}

class RangeNode(
    val from: SyntaxTreeNode,
    val to: SyntaxTreeNode,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(from, to)

    override fun toString(): String = "RangeNode(from=$from, to=$to)"
}
