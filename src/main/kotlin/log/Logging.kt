package me.gabriel.gwydion.log

import com.github.ajalt.mordant.rendering.TextColors

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

    fun color(text: String, color: TextColors) {
        builder.append(color(text))
    }

    fun colorful(text: String, color: TextColors): String {
        return color(text)
    }

    fun build(): String {
        return builder.toString()
    }
}