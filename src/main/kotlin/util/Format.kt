package me.gabriel.gwydion.util

import me.gabriel.gwydion.parsing.SyntaxTreeNode

fun formatAstTree(node: SyntaxTreeNode, depth: Int = 0): String {
    val children = node.getChildren()
    val childrenString = children.joinToString("\n") { formatAstTree(it, depth + 1) }
    return "${" ".repeat(depth * 2)}$node}\n$childrenString"
}