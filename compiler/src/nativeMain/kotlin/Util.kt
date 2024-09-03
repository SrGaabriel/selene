import okio.Path

val Path.fileExtensionOrNull: String?
    get() = segments
        .lastOrNull()
        ?.substringAfterLast('.', "")
        ?.takeIf { it.isNotEmpty() }