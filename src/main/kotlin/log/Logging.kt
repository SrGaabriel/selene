package me.gabriel.gwydion.log

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles

interface GwydionLogger {
    fun log(level: LogLevel, message: LogBuilder.() -> Unit)
}

enum class LogLevel {
    INFO, WARN, ERROR, DEBUG
}

class LogBuilder {
    private var builder = StringBuilder()

    operator fun String.unaryPlus() {
        builder.appendLine(this)
    }

    fun color(text: String, color: TextColors): String {
        return color(text)
    }

    fun bold(text: String): String {
        return TextStyles.bold(text)
    }

    fun build(): String {
        return builder.toString()
    }
}