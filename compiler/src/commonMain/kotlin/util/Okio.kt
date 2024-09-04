package me.gabriel.gwydion.compiler.util

import okio.Path

val Path.fileExtensionOrNull: String?
    get() = segments
        .lastOrNull()
        ?.substringAfterLast('.', "")
        ?.takeIf { it.isNotEmpty() }