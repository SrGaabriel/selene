package me.gabriel.selene.backend.common

import me.gabriel.selene.analysis.SymbolRepository
import me.gabriel.selene.analysis.signature.Signatures
import me.gabriel.selene.frontend.parsing.SyntaxTree

data class SeleneCompilerModule(
    val name: String,
    val symbols: SymbolRepository,
    val signatures: Signatures,
    val astTree: SyntaxTree,
    val stdlib: Boolean
)