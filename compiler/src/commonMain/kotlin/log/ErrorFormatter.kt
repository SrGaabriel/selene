package me.gabriel.selene.compiler.log

import com.github.ajalt.mordant.rendering.TextColors
import me.gabriel.selene.frontend.parsing.SyntaxTreeNode
import me.gabriel.selene.tools.LogLevel
import me.gabriel.selene.tools.SeleneLogger
import me.gabriel.selene.tools.findRowOfIndex
import me.gabriel.selene.tools.trimIndentReturningWidth

class ErrorFormatter(
    private val logger: SeleneLogger
) {
    fun printError(
        code: String,
        prefix: String,
        start: Int,
        end: Int,
        message: String
    ) {
        val length = end - start
        val rowInfo =
            findRowOfIndex(code.split("\n"), start) ?: error("Error while finding the line of the error")
        val (contentTrim, trimWidth) = rowInfo.content.trimIndentReturningWidth()

        val relativeStart = rowInfo.relativeIndex - trimWidth
        val relativeEnd = (rowInfo.relativeIndex + length - trimWidth)
            .coerceAtMost(contentTrim.length)

        val hasContinuation = length > contentTrim.length

        logger.log(LogLevel.ERROR) {
            +"${bold("[$prefix]")} $message"
            +"|"
            +"| row: ${
                contentTrim.replaceRange(
                    relativeStart,
                    relativeEnd,
                    color(contentTrim.substring(relativeStart, relativeEnd), TextColors.red)
                )
            }"
            +("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^".repeat(relativeEnd - relativeStart))
        }
    }

    fun printError(
        code: String,
        prefix: String,
        node: SyntaxTreeNode,
        message: String
    ) {
        printError(
            code = code,
            prefix = prefix,
            start = node.mark.position,
            end = findEndOfNode(node),
            message = message
        )
    }

    private fun findEndOfNode(node: SyntaxTreeNode): Int {
        val children = node.getChildren()
        return if (children.isEmpty()) {
            node.mark.position + node.mark.value.length
        } else {
            children.maxOf { findEndOfNode(it) }
        }
    }
}