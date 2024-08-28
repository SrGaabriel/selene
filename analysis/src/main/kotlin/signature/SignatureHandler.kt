package me.gabriel.gwydion.analysis.signature

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.gabriel.gwydion.tools.GwydionLogger
import me.gabriel.gwydion.tools.LogLevel
import java.io.File

class SignatureHandler(
    val module: String,
    val logger: GwydionLogger,
    val json: Json
) {
    fun parseSignature(file: File): Signatures {
        if (!file.exists()) {
            logger.log(LogLevel.ERROR) { +"Signature file does not exist" }
            return Signatures()
        }

        logger.log(LogLevel.INFO) { +"Parsing signature file" }
        val signatures = json.decodeFromString<Signatures>(file.readText())
        logger.log(LogLevel.INFO) { +"Parsed ${signatures.traits.size} traits" }
        return signatures
    }

    fun appendSignatureToFile(file: File, signatures: Signatures) {
        logger.log(LogLevel.INFO) { +"Appending signature to file" }
        val currentContent = if (file.exists()) {
            json.decodeFromString<Signatures>(file.readText())
        } else {
            Signatures()
        }
        val newContent = currentContent + signatures
        file.writeText(json.encodeToString(newContent))
    }
}