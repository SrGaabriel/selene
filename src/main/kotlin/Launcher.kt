package me.gabriel.gwydion;

import com.github.ajalt.mordant.rendering.TextColors
import me.gabriel.gwydion.executor.KotlinCodeExecutor
import me.gabriel.gwydion.lexing.lexers.StringLexer
import me.gabriel.gwydion.log.LogLevel
import me.gabriel.gwydion.log.MordantLogger
import me.gabriel.gwydion.parsing.Parser
import me.gabriel.gwydion.util.findRowOfIndex
import me.gabriel.gwydion.util.replaceAtIndex
import me.gabriel.gwydion.util.trimIndentReturningWidth
import java.io.File

fun main() {
    val logger = MordantLogger()
    logger.log(LogLevel.INFO) { +"Starting the Gwydion compiler..." }

    val text = readText();
    val lexer = StringLexer(text);
    val result = lexer.tokenize();
    if (result.isLeft()) {
        val error = result.getLeft()
        val rowInfo =  findRowOfIndex(text.split("\n"), error.position) ?: error("Error while finding the line of the error")
        val (contentTrim, trimWidth) = rowInfo.content.trimIndentReturningWidth()
        val newRelativeIndex = rowInfo.relativeIndex - trimWidth

        logger.log(LogLevel.ERROR) {
            + "lexing: ${error.message}"
            + "|"
            + "| row: ${replaceAtIndex(contentTrim, newRelativeIndex, 1, colorful(contentTrim[newRelativeIndex].toString(), TextColors.red))}"
            + ("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^")
        }
        return
    }
    val tokenStream = result.getRight();
    logger.log(LogLevel.DEBUG) { +"The lexing was successful with ${tokenStream.count()} tokens!" }
    val parser = Parser(tokenStream)
    val parsingResult = parser.parse()
    if (parsingResult.isLeft()) {
        val error = parsingResult.getLeft()
        val position = error.token.position
        val rowInfo =  findRowOfIndex(text.split("\n"), position) ?: error("Error while finding the line of the error")
        val (contentTrim, trimWidth) = rowInfo.content.trimIndentReturningWidth()

        logger.log(LogLevel.ERROR) {
            + "parsing: ${error.message}"
            + "|"
            + "| row: ${contentTrim.replace(error.token.value, colorful(error.token.value, TextColors.red))}"
            + ("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^")
        }
        return
    } else {
        logger.log(LogLevel.DEBUG) { +"The parsing was successful!" }
    }

    val syntaxTree = parsingResult.getRight()

    val executor = KotlinCodeExecutor(syntaxTree)
    logger.log(LogLevel.INFO) { +"Now executing the code... Output:" }
    executor.execute()
}

fun readText(): String {
    return File("src/main/resources/example.wy").readText()
}