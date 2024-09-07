package me.gabriel.ryujin.analyzer

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.statement.DragonStatement

/**
 * This analyzer is responsible for catching obvious flaws in LLVM IR.
 * It should be used to catch errors that are either not caught by the parser, or way too obvious to go unnoticed.
 */
interface RyujinAnalyzer {
    fun analyze(module: DragonModule): List<RyujinAnalysisError >
}

