import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.compiler.GwydionCompilerPlatform
import me.gabriel.gwydion.compiler.cli.CommandLine
import me.gabriel.gwydion.tools.GwydionLogger
import me.gabriel.gwydion.tools.LogLevel
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.system.getTimeMillis

class NativeCompilerPlatform(
    override val logger: GwydionLogger,
    override val cli: CommandLine,
    private val json: Json
): GwydionCompilerPlatform {
    override fun getCurrentTimeMillis(): Long {
        return getTimeMillis()
    }

    override fun readSources(): String {
        val directory = getSourceDirectory()
        logger.log(LogLevel.DEBUG) { +"Reading the directory: ${directory}" }
        val builder = StringBuilder()
        FileSystem.SYSTEM.list(directory).forEach {
            println("Reading $it")
//            if (it.extension != "wy") return@forEach
            logger.log(LogLevel.DEBUG) { +"Reading the file: ${it.name}" }
            builder.appendLine(FileSystem.SYSTEM.read(it) { readUtf8() })
        }
        return builder.toString()
    }

    override fun parseSignatures(): Signatures {
        val file = getSignatureFile()
        if (!FileSystem.SYSTEM.exists(file)) {
            logger.log(LogLevel.ERROR) { +"Signature file does not exist at ${file}" }
            return Signatures()
        }

        logger.log(LogLevel.INFO) { +"Parsing signature file" }
        val signatures = json.decodeFromString<Signatures>(FileSystem.SYSTEM.read(file) { readUtf8() })
        logger.log(LogLevel.INFO) { +"Parsed ${signatures.traits.size} traits" }
        return signatures
    }

    override fun saveSignatures(signatures: Signatures) {
        logger.log(LogLevel.INFO) { +"Appending signature to file" }
        val file = getSignatureFile()
        val currentContent = if (FileSystem.SYSTEM.exists(file)) {
            json.decodeFromString<Signatures>(FileSystem.SYSTEM.read(file) { readUtf8() })
        } else {
            Signatures()
        }
        val newContent = currentContent + signatures
        val encoded = json.encodeToString(newContent)
        FileSystem.SYSTEM.write(file) {
            writeUtf8(encoded)
        }
    }

    override fun createIrFile(llvmIr: String, outputFileName: String) {
        val outputDirectory = getCurrentDirectory()
        if (!FileSystem.SYSTEM.exists(outputDirectory)) {
            FileSystem.SYSTEM.createDirectory(outputDirectory)
        }

        val inputLlPath = "${outputDirectory}/$outputFileName.ll".toPath()
        FileSystem.SYSTEM.delete(inputLlPath)
        FileSystem.SYSTEM.write(inputLlPath) {
            writeUtf8(llvmIr)
        }
    }

    override fun exitProcess(status: Int): Nothing {
        kotlin.system.exitProcess(status)
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