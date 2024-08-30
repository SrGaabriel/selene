package me.gabriel.gwydion.compiler.reader

import java.io.File

interface SourceReader {
    fun read(directory: File): String
}