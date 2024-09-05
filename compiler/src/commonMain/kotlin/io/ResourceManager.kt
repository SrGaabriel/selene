package me.gabriel.selene.compiler.io

import me.gabriel.selene.analysis.signature.Signatures

interface ResourceManager {
    val ioPlatform: IoPlatform

    fun readSources(): String

    fun parseSignatures(): Signatures

    fun saveSignatures(signatures: Signatures)

    fun createIrFile(llvmIr: String, outputFileName: String)
}