package me.gabriel.gwydion.cli

class CommandHandler(private val args: Array<String>) {
    fun option(option: String): Boolean =
        args.any { it.lowercase().drop(2) == option }

    fun argumentAt(index: Int) =
        args.filter { !it.startsWith("--") }[index]
}