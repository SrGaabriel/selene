package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.exception.AnalysisError

class AnalysisResult(
    val table: SymbolTable,
    val errors: List<AnalysisError>
)