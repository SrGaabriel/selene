package me.gabriel.selene.compiler

import me.gabriel.selene.compiler.io.IoPlatform
import me.gabriel.selene.tools.SeleneLogger

interface SeleneCompilerPlatform {
    val io: IoPlatform
    val logger: SeleneLogger

    fun exitProcess(status: Int): Nothing

    companion object {
        val FILE_EXTENSIONS = arrayOf("sn")
    }
}