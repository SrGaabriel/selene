package me.gabriel.gwydion;

import com.github.ajalt.mordant.rendering.TextColors
import me.gabriel.gwydion.analyzer.CumulativeSemanticAnalyzer
import me.gabriel.gwydion.analyzer.SymbolTable
import me.gabriel.gwydion.executor.KotlinCodeExecutor
import me.gabriel.gwydion.lexing.lexers.StringLexer
import me.gabriel.gwydion.log.GwydionLogger
import me.gabriel.gwydion.log.LogLevel
import me.gabriel.gwydion.log.MordantLogger
import me.gabriel.gwydion.parsing.Parser
import me.gabriel.gwydion.parsing.SyntaxTree
import me.gabriel.gwydion.reader.AmbiguousSourceReader
import me.gabriel.gwydion.util.findRowOfIndex
import me.gabriel.gwydion.util.replaceAtIndex
import me.gabriel.gwydion.util.trimIndentReturningWidth
import java.io.File
import java.time.Instant
import kotlin.system.measureNanoTime

fun main() {
    val logger = MordantLogger()
    logger.log(LogLevel.INFO) { +"Starting the Gwydion compiler..." }

    val reader = AmbiguousSourceReader(logger)
    val stdlib = reader.read(findStdlib())
    val (stdlibCompiled, stdlibSymbols) = compile(stdlib, logger) ?: return

    val example = reader.read(readText())
    val (compiled, _) = compile(example, logger, stdlibSymbols) ?: return

    stdlibCompiled.join(compiled)
    val executor = KotlinCodeExecutor(stdlibCompiled)
    logger.log(LogLevel.INFO) { +"Now executing the code... Output:" }
    executor.execute()
    logger.log(LogLevel.INFO) { +"The code was executed with exit code 0" }
}

fun compile(text: String, logger: GwydionLogger, symbols: SymbolTable = SymbolTable()): Pair<SyntaxTree, SymbolTable>? {
    val start = Instant.now()
    val lexer = StringLexer(text);
    val result = lexer.tokenize();
    if (result.isLeft()) {
        val error = result.getLeft()
        val rowInfo =  findRowOfIndex(text.split("\n"), error.position) ?: error("Error while finding the line of the error")
        val (contentTrim, trimWidth) = rowInfo.content.trimIndentReturningWidth()
        val newRelativeIndex = rowInfo.relativeIndex - trimWidth

        logger.log(LogLevel.ERROR) {
            + "${bold("[lexing]")} ${error.message}"
            + "|"
            + "| row: ${replaceAtIndex(contentTrim, newRelativeIndex, 1, color(contentTrim[newRelativeIndex].toString(), TextColors.red))}"
            + ("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^")
        }
        return null
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
            + "${bold("[parsing]")} ${error.message}"
            + "|"
            + "| row: ${contentTrim.replace(error.token.value, color(error.token.value, TextColors.red))}"
            + ("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^".repeat(error.token.value.length))
        }
        return null
    } else {
        logger.log(LogLevel.DEBUG) { +"The parsing was successful!" }
    }

    val analyzer = CumulativeSemanticAnalyzer(parsingResult.getRight(), symbols)
    val analysis = analyzer.analyzeTree()
    if (analysis.errors.isNotEmpty()) {
        logger.log(LogLevel.ERROR) {
            +"There were ${analysis.errors.size} error(s) during the semantic analysis:"
        }
        analysis.errors.forEachIndexed { index, error ->
            logger.log(LogLevel.ERROR) {
                + "${bold("[semantic]")} ${error.message}"
            }
        }
        return null
    }
    val end = Instant.now()
    logger.log(LogLevel.INFO) { +"Compilation finished in ${end.toEpochMilli() - start.toEpochMilli()}ms" }
    return Pair(parsingResult.getRight(), analysis.table)
}

fun readText(): File {
    return File("src/main/resources")
}

fun findStdlib(): File {
    return File("stdlib/src/")
}