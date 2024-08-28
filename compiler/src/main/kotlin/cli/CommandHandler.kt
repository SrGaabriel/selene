package me.gabriel.gwydion.compiler.cli

// Temporary
class CommandHandler(private val args: Array<String>) {
    fun option(option: String): Boolean =
        args.any { it.lowercase().drop(2) == option }

    fun argumentAt(index: Int) =
        args.filter { !it.startsWith("--") }[index]
}