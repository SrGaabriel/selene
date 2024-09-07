package me.gabriel.ryujin.analyzer.impl

import me.gabriel.ryujin.DragonModule
import me.gabriel.ryujin.analyzer.RyujinAnalysisError
import me.gabriel.ryujin.analyzer.RyujinAnalyzer
import me.gabriel.ryujin.statement.DragonStatement
import me.gabriel.ryujin.statement.StoreStatement
import me.gabriel.ryujin.struct.DragonType

/**
 * This is a balanced analyzer: neither too thorough nor too shallow, as to
 * provide a good balance between performance and error detection.
 */
class BalancedRyujinAnalyzer: RyujinAnalyzer {
    override fun analyze(module: DragonModule): List<RyujinAnalysisError> {
        val errors = mutableListOf<RyujinAnalysisError>()
        module.functions.asSequence().flatMap { it.statements }.forEach {
            errors.addAll(analyzeStatement(it))
        }
        return errors
    }

    private fun analyzeStatement(statement: DragonStatement): List<RyujinAnalysisError> = when (statement) {
        is StoreStatement -> analyzeStoreStatement(statement)
        else -> emptyList()
    }

    private fun analyzeStoreStatement(statement: StoreStatement): List<RyujinAnalysisError> {
        val errors = mutableListOf<RyujinAnalysisError>()
        if (statement.target.type !is DragonType.Pointer) {
            errors.add(
                RyujinAnalysisError(
                    statement = statement,
                    message = "Target is not a pointer: ${statement.target.type}"
                )
            )
        }
        return errors
    }
}