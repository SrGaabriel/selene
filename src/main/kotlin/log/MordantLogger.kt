package me.gabriel.gwydion.log

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal

class MordantLogger: GwydionLogger {
    private val terminal = Terminal(tabWidth = 4, ansiLevel = AnsiLevel.TRUECOLOR)

    override fun log(level: LogLevel, message: LogBuilder.() -> Unit) {
        val (prefix, color) = when (level) {
            LogLevel.DEBUG -> "🐛 debug" to TextColors.green
            LogLevel.WARN -> "⚠️ warn" to TextColors.yellow
            LogLevel.ERROR -> "❌ error" to TextColors.red
            LogLevel.INFO -> "ℹ️ info" to TextColors.blue
        }

        val builder = LogBuilder()
        val text = buildString {
            append(color(prefix))
            append(": ")
            val string = builder.apply(message).build().let {
                if (it.endsWith("\n")) it.dropLast(1) else it
            }
            append(terminal.render(string)
                    .replace("\n", "\n${" ".repeat(prefix.length)}")
            )
        }
        terminal.println(text)
    }

}