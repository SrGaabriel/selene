package me.gabriel.gwydion.compiler.io

import kotlinx.serialization.encodeToString
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.compiler.cli.CommandLine
import me.gabriel.gwydion.compiler.util.fileExtensionOrNull
import me.gabriel.gwydion.tools.GwydionLogger
import me.gabriel.gwydion.tools.LogLevel
import okio.Path
import okio.Path.Companion.toPath

class LoggedResourceManager(
    private val logger: GwydionLogger,
    private val cli: CommandLine,
    override val ioPlatform: IoPlatform
): ResourceManager {
    private val fileSystem get() = ioPlatform.getFileSystem()

    override fun readSources(): String {
        val directory = getSourceDirectory()
        logger.log(LogLevel.DEBUG) { +"Reading the directory: ${directory}" }
        val builder = StringBuilder()
        fileSystem.list(directory).forEach {
            if (it.fileExtensionOrNull != "wy") return@forEach
            logger.log(LogLevel.DEBUG) { +"Reading the file: ${it.name}" }
            builder.appendLine(fileSystem.read(it) { readUtf8() })
        }
        return builder.toString()
    }

    override fun parseSignatures(): Signatures {
        val file = getSignatureFile()
        if (!fileSystem.exists(file)) {
            logger.log(LogLevel.ERROR) { +"Signature file does not exist at ${file}" }
            return Signatures()
        }

        logger.log(LogLevel.INFO) { +"Parsing signature file" }
        val signatures = ioPlatform.getJson().decodeFromString<Signatures>(fileSystem.read(file) { readUtf8() })
        logger.log(LogLevel.INFO) { +"Parsed ${signatures.traits.size} traits" }
        return signatures
    }

    override fun saveSignatures(signatures: Signatures) {
        logger.log(LogLevel.INFO) { +"Appending signature to file" }
        val file = getSignatureFile()
        val json = ioPlatform.getJson()
        val currentContent = if (fileSystem.exists(file)) {
            json.decodeFromString<Signatures>(fileSystem.read(file) { readUtf8() })
        } else {
            Signatures()
        }
        val newContent = currentContent + signatures
        val encoded = json.encodeToString(newContent)
        fileSystem.write(file) {
            writeUtf8(encoded)
        }
    }

    override fun createIrFile(llvmIr: String, outputFileName: String) {
        val outputDirectory = getCurrentDirectory()
        if (!fileSystem.exists(outputDirectory)) {
            fileSystem.createDirectory(outputDirectory)
        }

        val inputLlPath = "${outputDirectory}/$outputFileName.ll".toPath()
        fileSystem.delete(inputLlPath)
        fileSystem.write(inputLlPath) {
            writeUtf8(llvmIr)
        }
    }

    private fun getCurrentDirectory(): Path {
        return ".".toPath()
    }

    private fun getSourceDirectory(): Path =
        cli.argumentAt(0).toPath().resolve("src")

    private fun getSignatureFile(): Path {
        return "signatures.json".toPath()
    }
}