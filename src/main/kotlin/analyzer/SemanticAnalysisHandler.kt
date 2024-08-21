package me.gabriel.gwydion.analyzer

import me.gabriel.gwydion.exception.AnalysisResult

interface SemanticAnalysisHandler {
    fun analyzeTree(): AnalysisResult
}