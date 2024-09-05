package me.gabriel.selene.tools

data class RowInfo(val content: String, val relativeIndex: Int)

fun findRowOfIndex(rows: List<String>, index: Int): RowInfo? {
    // Combine the list of rows into a single string with new lines
    val content = rows.joinToString(separator = "\n")

    // Check if index is within the bounds of the content
    if (index < 0 || index >= content.length) {
        return null
    }

    // Find the start of the row containing the index
    val rowStartIndex = content.lastIndexOf('\n', index)
    val rowEndIndex = content.indexOf('\n', index)

    // Determine the end index of the row, or use the end of the content if it's the last row
    val actualRowEndIndex = if (rowEndIndex == -1) content.length else rowEndIndex

    // Extract the row content
    val rowContent = content.substring(rowStartIndex + 1, actualRowEndIndex)

    // Calculate the relative index in the row
    val relativeIndexInRow = index - (rowStartIndex + 1)

    // Return both the row content and the relative index
    return RowInfo(rowContent, relativeIndexInRow)
}

fun replaceAtIndex(original: String, index: Int, length: Int, replacement: String): String {
    if (index < 0 || index >= original.length) {
        throw IndexOutOfBoundsException("Index $index is out of bounds for string of length ${original.length}")
    }

    val before = original.substring(0, index)

    val after = original.substring(index + length)

    return before + replacement + after
}

fun String.trimIndentReturningWidth(): Pair<String, Int> {
    var width = 0
    while (width < this.length && this[width].isWhitespace()) {
        width++
    }
    return Pair(this.substring(width), width)
}