package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.exception.AnalysisError

interface SemanticAnalyzer {
    fun analyzeTree(): List<AnalysisError>
}