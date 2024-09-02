package me.gabriel.gwydion.compiler.cli

// Temporary
class CommandLine(private val args: Array<String>) {
    fun option(option: String): Boolean =
        args.any { it.lowercase().drop(2) == option }

    fun argumentAt(index: Int) =
        args.filter { !it.startsWith("--") }[index]

    fun argumentAtOrNull(index: Int) =
        args.filter { !it.startsWith("--") }.getOrNull(index)

    fun isStdlib() = option(STDLIB_FLAG)

    fun isEmpty() = args.isEmpty()

    fun moduleName() = argumentAt(MODULE_NAME_INDEX)
    fun moduleNameOrNull() = argumentAtOrNull(MODULE_NAME_INDEX)

    companion object {
        const val STDLIB_FLAG = "--internal-stdlib"

        const val MODULE_NAME_INDEX = 1
    }
}