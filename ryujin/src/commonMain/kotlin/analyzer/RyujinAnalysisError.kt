package me.gabriel.ryujin.analyzer

import me.gabriel.ryujin.statement.DragonStatement

data class RyujinAnalysisError(
    val statement: DragonStatement,
    val message: String
)