package me.gabriel.gwydion.signature

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.gabriel.gwydion.analyzer.SymbolTable
import me.gabriel.gwydion.log.LogLevel
import me.gabriel.gwydion.log.MordantLogger
import me.gabriel.gwydion.parsing.Type
import java.io.File

class SignatureHandler(
    val logger: MordantLogger,
    val json: Json
) {
    fun parseSignature(file: File): Signatures {
        if (!file.exists()) {
            logger.log(LogLevel.ERROR) { +"Signature file does not exist" }
            return Signatures(mutableListOf())
        }

        logger.log(LogLevel.INFO) { +"Parsing signature file" }
        val signatures = json.decodeFromString<Signatures>(file.readText())
        logger.log(LogLevel.INFO) { +"Parsed ${signatures.traitImpls.size} trait implsl" }
        return signatures
    }

//    fun generateSignature(table: SymbolTable): Signatures {
//        logger.log(LogLevel.INFO) { +"Generating signature file" }
//        val signatures = Signatures(
//            traits = table.filterIsInstance<Type.Trait>().map {
//                SignatureTrait(
//                    name = it.identifier,
//                    functions = it.functions.map { function ->
//                        SignatureFunction(
//                            name = function.name,
//                            returnType = SignatureType(function.returnType.signature),
//                            parameters = function.parameters.map { parameter ->
//                                SignatureType(parameter.type.signature)
//                            }
//                        )
//                    }
//                )
//            }
//        )
//        logger.log(LogLevel.INFO) { +"Generated ${signatures.traits.size} traits" }
//        return signatures
//    }

    // This won't overwrite the current file, but it will sum the signatures.
    fun appendSignatureToFile(file: File, signatures: Signatures) {
        logger.log(LogLevel.INFO) { +"Appending signature to file" }
        val currentContent = if (file.exists()) {
            json.decodeFromString<Signatures>(file.readText())
        } else {
            Signatures(mutableListOf())
        }
        val newContent = currentContent + signatures
        file.writeText(json.encodeToString(newContent))
    }
}