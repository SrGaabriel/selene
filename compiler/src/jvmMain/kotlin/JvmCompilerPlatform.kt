package me.gabriel.selene.compiler.jvm

import kotlinx.serialization.json.Json
import me.gabriel.selene.compiler.SeleneCompilerPlatform
import me.gabriel.selene.compiler.io.IoPlatform
import me.gabriel.selene.tools.SeleneLogger
import okio.FileSystem

class JvmCompilerPlatform(
    override val logger: SeleneLogger
): SeleneCompilerPlatform {
    override val io: IoPlatform = Io

    override fun exitProcess(status: Int): Nothing {
        kotlin.system.exitProcess(status)
    }

    companion object Io : IoPlatform {
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
        }

        override fun getJson(): Json = json

        override fun getFileSystem(): FileSystem = FileSystem.SYSTEM
    }
}