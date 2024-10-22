package me.gabriel.selene.compiler.io

import kotlinx.serialization.json.Json
import okio.FileSystem

interface IoPlatform {
    fun getJson(): Json

    fun getFileSystem(): FileSystem
}