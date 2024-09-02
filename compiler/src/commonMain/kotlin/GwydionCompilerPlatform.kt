package me.gabriel.gwydion.compiler

import me.gabriel.gwydion.analysis.signature.Signatures
import me.gabriel.gwydion.tools.GwydionLogger

interface GwydionCompilerPlatform {
    val logger: GwydionLogger

    fun getCurrentTimeMillis(): Long

    fun readSources(): String

    fun parseSignatures(): Signatures

    fun saveSignatures(signatures: Signatures)

    fun createIrFile(llvmIr: String, outputFileName: String)

    fun exitProcess(status: Int): Nothing
}