package me.gabriel.gwydion.compiler.cli

// Temporary
class CommandLine(private val args: Array<String>) {
    fun option(option: String): Boolean =
        args.any { it.lowercase().drop(2) == option }

    fun argumentAt(index: Int) =
        args.filter { !it.startsWith("--") }[index]

    fun argumentAtOrNull(index: Int) =
        args.filter { !it.startsWith("--") }.getOrNull(index)

    fun isCompileIntrinsics() = option(COMPILE_INTRINSICS_FLAG)

    fun isEmpty() = args.isEmpty()

    fun moduleName() = argumentAt(MODULE_NAME_INDEX)
    fun moduleNameOrNull() = argumentAtOrNull(MODULE_NAME_INDEX)

    companion object {
        const val COMPILE_INTRINSICS_FLAG = "compile-intrinsics"

        const val MODULE_NAME_INDEX = 1
    }
}