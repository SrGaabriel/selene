package me.gabriel.gwydion.compiler.jvm

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.compiler.GwydionCompilerPlatform
import me.gabriel.gwydion.compiler.cli.CommandLine
import me.gabriel.gwydion.tools.GwydionLogger
import me.gabriel.gwydion.tools.LogLevel
import java.io.File

class JvmCompilerPlatform(
    override val logger: GwydionLogger,
    override val cli: CommandLine,
    private val json: Json
): GwydionCompilerPlatform {
    override fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun readSources(): String {
        val directory = File(getSourceDirectory(), "src")
        logger.log(LogLevel.DEBUG) { +"Reading the directory: ${directory.absolutePath}" }
        val builder = StringBuilder()
        directory.listFiles()?.forEach {
            if (it.extension != "wy") return@forEach
            logger.log(LogLevel.DEBUG) { +"Reading the file: ${it.name}" }
            builder.appendLine(it.readText())
        }
        return builder.toString()
    }

    override fun parseSignatures(): Signatures {
        val file = File(getCurrentDirectory(), "signatures.json")
        if (!file.exists()) {
            logger.log(LogLevel.ERROR) { +"Signature file does not exist at ${file.absolutePath}" }
            return Signatures()
        }

        logger.log(LogLevel.INFO) { +"Parsing signature file" }
        val signatures = json.decodeFromString<Signatures>(file.readText())
        logger.log(LogLevel.INFO) { +"Parsed ${signatures.traits.size} traits" }
        return signatures
    }

    override fun saveSignatures(signatures: Signatures) {
        logger.log(LogLevel.INFO) { +"Appending signature to file" }
        val file = getSignatureFile()
        val currentContent = if (file.exists()) {
            json.decodeFromString<Signatures>(file.readText())
        } else {
            Signatures()
        }
        val newContent = currentContent + signatures
        file.writeText(json.encodeToString(newContent))
    }

    override fun createIrFile(llvmIr: String, outputFileName: String) {
        val outputDirectory = getCurrentDirectory()
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }

        val inputLlPath = "${outputDirectory.absolutePath}/$outputFileName.ll"

        File(inputLlPath).delete()
        File(inputLlPath).createNewFile()
        File(inputLlPath).writeText(llvmIr)
    }

    override fun exitProcess(status: Int): Nothing {
        kotlin.system.exitProcess(status)
    }

    private fun getCurrentDirectory(): File {
        return File("").absoluteFile
    }

    private fun getSourceDirectory(): File =
        File(cli.argumentAt(0))

    private fun getSignatureFile(): File {
        return File("signatures.json")
    }
}