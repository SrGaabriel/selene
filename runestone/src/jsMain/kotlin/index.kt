import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.*
import kotlinx.html.js.onInputFunction
import me.gabriel.gwydion.frontend.lexing.Token
import me.gabriel.gwydion.frontend.lexing.TokenKind
import me.gabriel.gwydion.frontend.lexing.lexers.StringLexer

fun main() {
    document.body!!.append.div {
        textArea {
            id = "code"
            placeholder = "Code here"
            onInputFunction = onInputFunction@{
                val current = it.target.asDynamic().value as String
                val lexer = StringLexer(current)
                val tokens = lexer.tokenize()

                // Now we'll highlight all the tokens
                if (tokens.isLeft()) return@onInputFunction

                val tokenStream = tokens.getRightOrNull()!!
                val tokenList = tokenStream.toList()

                // We will reconstruct the entire string with highlighting
                val highlighted = buildString {
                    var currentIndex = 0

                    tokenList.forEach { token ->
                        val kind = token.kind
                        val start = token.position
                        val end = token.position + token.value.length

                        // Add any text before the token
                        append(current.substring(currentIndex, start))

                        // Choose the color based on the token kind
                        val color = when (kind) {
                            TokenKind.FUNCTION -> "blue"
                            TokenKind.IDENTIFIER -> "yellow"
                            else -> "black"
                        }

                        // Wrap the token in a span with the appropriate color
                        append("<span style='color:$color;'>${current.substring(start, end)}</span>")

                        // Move the current index past this token
                        currentIndex = end
                    }

                    // Add any remaining text after the last token
                    append(current.substring(currentIndex, current.length))
                }

                // Update the output div with the highlighted text
                document.getElementById("output")!!.innerHTML = highlighted
            }
        }
        div {
            id = "output"
        }
    }
}
