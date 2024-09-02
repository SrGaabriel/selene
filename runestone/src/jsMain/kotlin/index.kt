import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.*
import kotlinx.html.js.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import me.gabriel.gwydion.frontend.lexing.Token
import me.gabriel.gwydion.frontend.lexing.TokenKind
import me.gabriel.gwydion.frontend.lexing.lexers.StringLexer

fun main() {
    document.body!!.append.div {
        div {
            id = "editor-container"
            style = "position: relative; width: 100%; height: 300px; background-color: #383a3b; overflow: hidden; color: white;"
            spellCheck = false

            textArea {
                id = "code-input"
                style = "width: 100%; height: 100%; font-family: monospace; font-size: 14px; padding: 5px; resize: none; border: 1px solid #ccc; position: absolute; background: transparent; color: transparent; caret-color: black; z-index: 2;"
            }
            pre {
                id = "highlighted-code"
                style = "width: 100%; height: 100%; font-family: monospace; font-size: 14px; padding: 5px; margin: 0; white-space: pre-wrap; word-wrap: break-word; position: absolute; top: 0; left: 0; pointer-events: none; z-index: 1; overflow: hidden;"
            }
        }
    }

    val codeInput = document.getElementById("code-input") as HTMLTextAreaElement
    val highlightedCode = document.getElementById("highlighted-code") as HTMLPreElement

    codeInput.addEventListener("input", { event: Event ->
        updateHighlighting(codeInput, highlightedCode)
    })

    codeInput.addEventListener("scroll", { event: Event ->
        highlightedCode.scrollTop = codeInput.scrollTop
        highlightedCode.scrollLeft = codeInput.scrollLeft
    })
}

interface ColorScheme {
    val keyword: String
    val identifier: String
    val number: String
}

object DefaultColorScheme : ColorScheme {
    override val keyword = "#ff47b8"
    override val identifier = "#ff9d47"
    override val number = "#ae53db"
}

fun updateHighlighting(input: HTMLTextAreaElement, output: HTMLPreElement) {
    val text = input.value
    val lexer = StringLexer(text)
    val tokens = lexer.tokenize()

    if (tokens.isLeft()) return

    val tokenStream = tokens.getRightOrNull()!!
    val tokenList = tokenStream.toList()

    output.innerHTML = ""
    var lastIndex = 0

    tokenList.forEach { token ->
        val kind = token.kind
        val start = token.position
        val end = token.position + token.value.length

        if (start > lastIndex) {
            output.appendChild(document.createTextNode(text.substring(lastIndex, start)))
        }

        val span = document.createElement("span") as HTMLSpanElement
        span.textContent = text.substring(start, end)
        span.style.color = when (kind) {
            TokenKind.FUNCTION, TokenKind.RETURN, TokenKind.INT32_TYPE, TokenKind.LAMBDA -> DefaultColorScheme.keyword
            TokenKind.IDENTIFIER -> DefaultColorScheme.identifier
            TokenKind.NUMBER -> DefaultColorScheme.number
            else -> "white"
        }
        output.appendChild(span)

        lastIndex = end
    }

    if (lastIndex < text.length) {
        output.appendChild(document.createTextNode(text.substring(lastIndex)))
    }
}