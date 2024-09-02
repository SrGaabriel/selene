package me.gabriel.gwydion.compiler

import com.github.ajalt.mordant.rendering.TextColors
import kotlinx.serialization.json.Json
import me.gabriel.gwydion.analysis.SemanticAnalysisManager
import me.gabriel.gwydion.analysis.SymbolRepository
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.compiler.cli.CommandHandler
import me.gabriel.gwydion.compiler.log.MordantLogger
import me.gabriel.gwydion.compiler.log.bold
import me.gabriel.gwydion.compiler.log.color
import me.gabriel.gwydion.frontend.lexing.lexers.StringLexer
import me.gabriel.gwydion.frontend.parsing.Parser
import me.gabriel.gwydion.frontend.parsing.SyntaxTree
import me.gabriel.gwydion.ir.LLVMCodeAdapter
import me.gabriel.gwydion.ir.intrinsics.INTRINSICS
import me.gabriel.gwydion.tools.*

class GwydionCompiler(
    private val platform: GwydionCompilerPlatform,
) {
    fun start(args: Array<String>) {
        println("Gwydion Compiler") // Let's signal that the compiler has been reached, just in case we face an issue with build tools or whatever!
        val logger = platform.logger
        val cli = CommandHandler(args)

        val isStdlib = cli.option("internal-stdlib")
        if (args.isEmpty()) {
            logger.log(LogLevel.ERROR) { +"The argument should be the path to the file to compile" }
            platform.exitProcess(1)
        }
        val name = args.getOrNull(1) ?: "program"
        val signatures = platform.parseSignatures()

        logger.log(LogLevel.INFO) { +"Starting the Gwydion compiler..." }
        val symbols = SymbolRepository(name)

        val llvmCodeAdapter = LLVMCodeAdapter()
        llvmCodeAdapter.registerIntrinsicFunction(*INTRINSICS)

        val sources = platform.readSources()
        val tree = parse(logger, sources, symbols, signatures)

        logger.log(LogLevel.INFO) { +"Memory and symbol analysis was successful" }

        val generated = llvmCodeAdapter.generate(name, tree, symbols, signatures, isStdlib)
        logger.log(LogLevel.INFO) { +"Code generated successfully" }
        platform.createIrFile(
            llvmIr = generated,
            outputFileName = name
        )
        logger.log(LogLevel.INFO) { +"Generated files successfully" }
        platform.saveSignatures(
            signatures
        )
        platform.exitProcess(0)
    }

    fun parse(logger: GwydionLogger, text: String, symbols: SymbolRepository, signatures: Signatures): SyntaxTree {
        val lexer = StringLexer(text)
        val result = lexer.tokenize()
        if (result.isLeft()) {
            val error = result.getLeft()
            val rowInfo =
                findRowOfIndex(text.split("\n"), error.position) ?: error("Error while finding the line of the error")
            val (contentTrim, trimWidth) = rowInfo.content.trimIndentReturningWidth()
            val newRelativeIndex = rowInfo.relativeIndex - trimWidth

            logger.log(LogLevel.ERROR) {
                +"${bold("[lexing]")} ${error.message}"
                +"|"
                +"| row: ${
                    replaceAtIndex(
                        contentTrim,
                        newRelativeIndex,
                        1,
                        color(contentTrim[newRelativeIndex].toString(), TextColors.red)
                    )
                }"
                +("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^")
            }
            platform.exitProcess(1)
        }
        val tokenStream = result.getRight()
        logger.log(LogLevel.DEBUG) { +"The lexing was successful with ${tokenStream.count()} tokens!" }
        val parser = Parser(tokenStream)
        val parsingResult = parser.parse()
        if (parsingResult.isLeft()) {
            val error = parsingResult.getLeft()
            val position = error.token.position
            val rowInfo =
                findRowOfIndex(text.split("\n"), position) ?: error("Error while finding the line of the error")
            val (contentTrim, trimWidth) = rowInfo.content.trimIndentReturningWidth()

            logger.log(LogLevel.ERROR) {
                +"${bold("[parsing]")} ${error.message}"
                +"|"
                +"| row: ${contentTrim.replace(error.token.value, color(error.token.value, TextColors.red))}"
                +("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^".repeat(error.token.value.length))
            }
            platform.exitProcess(1)
        } else {
            logger.log(LogLevel.DEBUG) { +"The parsing was successful!" }
        }
        val tree = parsingResult.getRight()

        val analyzer = SemanticAnalysisManager(logger, symbols, signatures)
        analyzer.registerInternal()
        analyzer.registerTreeSymbols(tree)
        analyzer.issueWarnings()

        val analysis = analyzer.analyzeTree(tree)
        if (analysis.errors.isNotEmpty()) {
            logger.log(LogLevel.ERROR) {
                +"There were ${analysis.errors.size} error(s) during the semantic analysis:"
            }
            analysis.errors.forEachIndexed { _, error ->
                val position = error.node.mark.position
                val rowInfo =
                    findRowOfIndex(text.split("\n"), position) ?: error("Error while finding the line of the error")
                val (contentTrim, trimWidth) = rowInfo.content.trimIndentReturningWidth()
                logger.log(LogLevel.ERROR) {
                    +"${bold("[semantic]")} ${error.message}"
                    +"|"
                    +"| row: ${
                        contentTrim.replace(
                            error.node.mark.value,
                            color(error.node.mark.value, TextColors.red)
                        )
                    }"
                    +("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^".repeat(error.node.mark.value.length))
                }
            }
            platform.exitProcess(1)
        }
        logger.log(LogLevel.INFO) { +"Compilation finished" }
        return parsingResult.getRight()
    }
}