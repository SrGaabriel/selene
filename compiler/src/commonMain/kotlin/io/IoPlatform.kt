package me.gabriel.gwydion.compiler.io

import kotlinx.serialization.json.Json
import me.gabriel.gwydion.compiler.cli.CommandLine
import okio.FileSystem

interface IoPlatform {
    fun getJson(): Json

    fun getFileSystem(): FileSystem
}