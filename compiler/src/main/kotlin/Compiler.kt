package me.gabriel.gwydion.compiler

import com.github.ajalt.mordant.rendering.TextColors
import kotlinx.serialization.json.Json
import me.gabriel.gwydion.analysis.SemanticAnalysisManager
import me.gabriel.gwydion.analysis.SymbolRepository
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.compiler.cli.CommandHandler
import me.gabriel.gwydion.compiler.io.CompilerIOService
import me.gabriel.gwydion.compiler.io.SignatureIOService
import me.gabriel.gwydion.compiler.log.MordantLogger
import me.gabriel.gwydion.compiler.log.bold
import me.gabriel.gwydion.compiler.log.color
import me.gabriel.gwydion.compiler.reader.AmbiguousSourceReader
import me.gabriel.gwydion.frontend.lexing.lexers.StringLexer
import me.gabriel.gwydion.frontend.parsing.Parser
import me.gabriel.gwydion.frontend.parsing.SyntaxTree
import me.gabriel.gwydion.ir.LLVMCodeAdapter
import me.gabriel.gwydion.ir.intrinsics.INTRINSICS
import me.gabriel.gwydion.tools.*
import java.io.File
import java.time.Instant
import kotlin.system.exitProcess

private val json = Json {
    encodeDefaults = false
}

fun main(args: Array<String>) {
    println("Gwydion Compiler") // Let's signal that the compiler has been reached, just in case we face an issue with build tools or whatever!
    val logger = MordantLogger()
    val cli = CommandHandler(args)

    val isStdlib = cli.option("internal-stdlib")
    if (args.isEmpty()) {
        logger.log(LogLevel.ERROR) { +"The argument should be the path to the file to compile" }
        exitProcess(1)
    }
    val sourcePath = args[0]
    val name = args.getOrNull(1) ?: "program"
    val signatureHandler = SignatureIOService(name, logger, json)
    val signaturesFile = args.getOrNull(2)?.let { File(it) } ?: File("signatures.json")
    signaturesFile.createNewFile()
    signaturesFile.writeText(signaturesFile.readText().ifEmpty { "{}" })
    val signatures = signaturesFile.let {
        signatureHandler.parseSignature(it)
    }

    val folder = File(sourcePath)
    if (!folder.exists() || folder.isFile) {
        logger.log(LogLevel.ERROR) { +"The folder does not exist" }
        exitProcess(1)
    }

    val currentFolder = File("").absolutePath
    logger.log(LogLevel.INFO) { +"Starting the Gwydion compiler..." }
    val symbols = SymbolRepository(name)

    val sourceReader = AmbiguousSourceReader(logger)
    val memoryStart = Instant.now()
    val llvmCodeAdapter = LLVMCodeAdapter()
    llvmCodeAdapter.registerIntrinsicFunction(*INTRINSICS)

    val sources = File(folder, "src")
    val tree = parse(logger, sourceReader.read(sources), symbols, signatures)

    val memoryEnd = Instant.now()
    val memoryDelay = memoryEnd.toEpochMilli() - memoryStart.toEpochMilli()
    logger.log(LogLevel.INFO) { +"Memory analysis took ${memoryDelay}ms" }

    val generationStart = Instant.now()
    val generated = llvmCodeAdapter.generate(name, tree, symbols, signatures, isStdlib)
    val generationEnd = Instant.now()
    val generationDelay = generationEnd.toEpochMilli() - generationStart.toEpochMilli()
    logger.log(LogLevel.INFO) { +"Code generation took ${generationDelay}ms" }
    val compilingStart = Instant.now()
    CompilerIOService.generateExecutable(
        llvmIr = generated,
        outputDir = currentFolder,
        outputFileName = name
    )
    val executionEnd = Instant.now()
    val executionDelay = executionEnd.toEpochMilli() - compilingStart.toEpochMilli()
    logger.log(LogLevel.INFO) { +"Compiling took ${executionDelay}ms" }
    signatureHandler.appendSignatureToFile(
        signaturesFile,
        signatures
    )

    logger.log(LogLevel.INFO) { +"The total time to generate and compile the code was ${executionDelay + generationDelay + memoryDelay}ms" }
    exitProcess(0)
}

fun parse(logger: GwydionLogger, text: String, symbols: SymbolRepository, signatures: Signatures): SyntaxTree {
    val start = Instant.now()
    val lexer = StringLexer(text)
    val result = lexer.tokenize()
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
        exitProcess(1)
    }
    val tokenStream = result.getRight()
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
        exitProcess(1)
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
            val rowInfo =  findRowOfIndex(text.split("\n"), position) ?: error("Error while finding the line of the error")
            val (contentTrim, trimWidth) = rowInfo.content.trimIndentReturningWidth()
            logger.log(LogLevel.ERROR) {
                + "${bold("[semantic]")} ${error.message}"
                + "|"
                + "| row: ${contentTrim.replace(error.node.mark.value, color(error.node.mark.value, TextColors.red))}"
                + ("| pos: " + " ".repeat(rowInfo.relativeIndex - trimWidth) + "^".repeat(error.node.mark.value.length))
            }
        }
        exitProcess(1)
    }
    val end = Instant.now()
    logger.log(LogLevel.INFO) { +"Compilation finished in ${end.toEpochMilli() - start.toEpochMilli()}ms" }
    return parsingResult.getRight()
}