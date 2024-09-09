package me.gabriel.selene.compiler

import me.gabriel.selene.analysis.SemanticAnalysisManager
import me.gabriel.selene.analysis.SymbolRepository
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.backend.common.SeleneCompilerBackend
import me.gabriel.selene.backend.common.SeleneCompilerModule
import me.gabriel.selene.compiler.cli.CommandLine
import me.gabriel.selene.compiler.io.LoggedResourceManager
import me.gabriel.selene.compiler.log.ErrorFormatter
import me.gabriel.selene.frontend.lexing.lexers.StringLexer
import me.gabriel.selene.frontend.parsing.Parser
import me.gabriel.selene.frontend.parsing.SyntaxTree
import me.gabriel.selene.tools.*

class SeleneCompiler(
    private val platform: SeleneCompilerPlatform,
    private val backend: SeleneCompilerBackend<*, *, *>,
    private val cli: CommandLine
) {
    private val errorFormatter = ErrorFormatter(platform.logger)

    fun start() {
        println("Selene Compiler") // Let's signal that the compiler has been reached, just in case we face an issue with build tools or whatever!
        val logger by platform::logger

        val resources = LoggedResourceManager(
            logger,
            cli,
            platform.io
        )

        val isStdlib = cli.isCompileIntrinsics()
        if (cli.isEmpty()) {
            logger.log(LogLevel.ERROR) { +"The argument should be the path to the file to compile" }
            platform.exitProcess(1)
        }
        val name = cli.moduleNameOrNull() ?: "program"
        val signatures = resources.parseSignatures()

        logger.log(LogLevel.INFO) { +"Starting the Selene compiler..." }
        val symbols = SymbolRepository(name)

        // TODO: register intrinsics
//        llvmCodeAdapter.registerIntrinsicFunction(*INTRINSICS)

        val sources = resources.readSources()
        val tree = parse(logger, sources, symbols, signatures)

        val module = SeleneCompilerModule(
            name = name,
            symbols = symbols,
            astTree = tree,
            signatures = signatures,
            stdlib = isStdlib
        )

        logger.log(LogLevel.INFO) { +"Memory and symbol analysis was successful" }

        val generated = try {
            backend.compile(module)
        } catch (e: RuntimeException) {
            logger.log(LogLevel.ERROR) { +"An error occurred during '$name' code generation: ${e.message}" }
            e.printStackTrace()
            platform.exitProcess(1)
        }
        logger.log(LogLevel.INFO) { +"Code generated successfully" }
        resources.createIrFile(
            llvmIr = generated,
            outputFileName = name
        )
        logger.log(LogLevel.INFO) { +"Generated files successfully" }
        resources.saveSignatures(
            signatures
        )
        platform.exitProcess(0)
    }

    fun parse(logger: SeleneLogger, text: String, symbols: SymbolRepository, signatures: Signatures): SyntaxTree {
        val lexer = StringLexer(text)
        val result = lexer.tokenize()
        if (result.isLeft()) {
            val error = result.getLeft()
            errorFormatter.printError(
                code = text,
                prefix = "lexing",
                start = error.position,
                end = error.position + error.length,
                message = error.message
            )
            platform.exitProcess(1)
        }
        val tokenStream = result.getRight()
        logger.log(LogLevel.DEBUG) { +"The lexing was successful with ${tokenStream.count()} tokens!" }
        val parser = Parser(tokenStream)
        val parsingResult = parser.parse()
        if (parsingResult.isLeft()) {
            val error = parsingResult.getLeft()
            errorFormatter.printError(
                code = text,
                prefix = "parsing",
                start = error.token.position,
                end = error.token.position + error.token.value.length,
                message = error.message
            )
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
                errorFormatter.printError(
                    code = text,
                    prefix = "semantic",
                    node = error.node,
                    message = error.message
                )
            }
            platform.exitProcess(1)
        }
        logger.log(LogLevel.INFO) { +"Compilation finished" }
        return parsingResult.getRight()
    }
}