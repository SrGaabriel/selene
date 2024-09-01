package me.gabriel.gwydion.tools

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

    fun build(): String {
        return builder.toString()
    }
}