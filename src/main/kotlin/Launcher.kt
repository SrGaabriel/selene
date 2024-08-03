package me.gabriel.gwydion;

import me.gabriel.gwydion.executor.KotlinCodeExecutor
import me.gabriel.gwydion.lexing.lexers.StringLexer
import me.gabriel.gwydion.parsing.Parser
import java.io.File

fun main() {
    val text = readText();
    val lexer = StringLexer(text);
    val result = lexer.tokenize();
    if (result.isLeft()) {
        println("Lexing error: ${result.getLeft().message}")
        return
    }
    val tokenStream = result.getRight();
    val parser = Parser(tokenStream)
    val parsingResult = parser.parse()
    if (parsingResult.isLeft()) {
        println("Parsing error: ${parsingResult.getLeft().message}")
        return
    }
    val syntaxTree = parsingResult.getRight()

    val executor = KotlinCodeExecutor(syntaxTree)
    println("Executing... Result:")
    executor.execute()
}

fun readText(): String {
    return File("src/main/resources/example.draco").readText()
}