package me.gabriel.selene.frontend.parsing

import me.gabriel.selene.frontend.SeleneType
import me.gabriel.selene.frontend.lexing.Token
import me.gabriel.selene.frontend.lexing.TokenKind

data class SyntaxTree(val root: RootNode = RootNode(mutableListOf())) {
    fun addAllNodes(nodes: List<SyntaxTreeNode>) {
        root.addNodes(nodes)
    }
}

sealed class SyntaxTreeNode(val mark: Token) {
    abstract fun getChildren(): List<SyntaxTreeNode>
}

sealed class TypedSyntaxTreeNode(open var type: SeleneType, mark: Token) : SyntaxTreeNode(mark)

class RootNode(private val children: MutableList<SyntaxTreeNode>) : SyntaxTreeNode(Token(TokenKind.BOF, "", 0)) {
    fun addNode(node: SyntaxTreeNode) {
        children.add(node)
    }

    fun addNodes(nodes: List<SyntaxTreeNode>) {
        children.addAll(nodes)
    }

    override fun getChildren(): List<SyntaxTreeNode> = children

    override fun toString(): String = "RootNode(children=$children)"
}

class FunctionNode(
    val name: String,
    var returnType: SeleneType,
    val parameters: List<ParameterNode>,
    val body: BlockNode,
    val modifiers: MutableList<Modifiers>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters + body

    override fun toString(): String = "FunctionNode(name='$name', returnType=$returnType, parameters=$parameters, body=$body, modifiers=$modifiers)"
}

class BlockNode(
    private val children: List<SyntaxTreeNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = children
}

class ParameterNode(
    val name: String,
    type: SeleneType,
    mark: Token
) : TypedSyntaxTreeNode(type, mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    override fun toString(): String = "ParameterNode(name='$name', type=$type)"
}

class AssignmentNode(
    val name: String,
    val expression: SyntaxTreeNode,
    val mutable: Boolean,
    type: SeleneType,
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
) : TypedSyntaxTreeNode(SeleneType.Boolean, mark) {
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

class DataStructureReferenceNode(
    val name: String,
    mark: Token
): SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    override fun toString(): String = "DataStructureReferenceNode(name='$name')"
}

class NumberNode(
    var value: String,
    val explicit: Boolean,
    override var type: SeleneType,
    mark: Token
) : TypedSyntaxTreeNode(type, mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    override fun toString(): String = "NumberNode(value='$value', explicit=$explicit)"
}

class StringNode(
    val value: String,
    val segments: List<Segment>,
    mark: Token
) : TypedSyntaxTreeNode(SeleneType.String, mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    sealed class Segment {
        data class Text(val text: String) : Segment()
        data class Reference(val node: VariableReferenceNode) : Segment()
        data class Expression(val node: SyntaxTreeNode) : Segment()
    }

    override fun toString(): String = "StringNode(value='$value', segments=$segments)"
}

class BooleanNode(
    val value: Boolean,
    mark: Token
) : TypedSyntaxTreeNode(SeleneType.Boolean, mark) {
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
    override fun getChildren(): List<SyntaxTreeNode> = listOf(array, index)

    override fun toString(): String = "ArrayAccessNode(array='$array', index=$index)"
}

class ArrayAssignmentNode(
    val array: SyntaxTreeNode,
    val index: SyntaxTreeNode,
    val expression: SyntaxTreeNode,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(index, array, expression)

    override fun toString(): String = "ArrayAssignmentNode(array='$array', index=$index, expression=$expression)"
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
    var type: SeleneType,
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
    var returnType: SeleneType,
    val parameters: List<ParameterNode>,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters

    override fun toString(): String = "TraitFunctionNode(name='$name', returnType=$returnType, parameters=$parameters)"
}

class TraitImplNode(
    var type: SeleneType,
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
    override fun getChildren(): List<SyntaxTreeNode> = listOf(struct)

    override fun toString(): String = "StructAccessNode(struct='$struct', field='$field')"
}

class MutationNode(
    val struct: SyntaxTreeNode,
    val field: String,
    val expression: SyntaxTreeNode,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = listOf(struct, expression)

    override fun toString(): String = "MutationNode(struct='$struct', field='$field', expression=$expression)"
}

class TraitFunctionCallNode(
    val trait: SyntaxTreeNode,
    val function: String,
    val arguments: List<SyntaxTreeNode>,
    val static: Boolean,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = arguments + trait

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

class LambdaNode(
    val parameters: List<LambdaParameterNode>,
    val body: SyntaxTreeNode,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = parameters + body

    override fun toString(): String = "LambdaNode(parameters=$parameters, body=$body)"
}

class LambdaParameterNode(
    val name: String,
    val type: SeleneType,
    mark: Token
) : SyntaxTreeNode(mark) {
    override fun getChildren(): List<SyntaxTreeNode> = emptyList()

    override fun toString(): String = "LambdaParameterNode(name='$name', type=$type)"
}
