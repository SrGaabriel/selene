package me.gabriel.gwydion.analysis.analyzers

import me.gabriel.gwydion.frontend.parsing.SyntaxTreeNode
import kotlin.reflect.KClass

abstract class SingleNodeAnalyzer<T : SyntaxTreeNode>(
    private val klass: KClass<T>
): ISemanticAnalyzer<T> {
    override fun handles(node: SyntaxTreeNode): Boolean {
        return klass.isInstance(node)
    }
}