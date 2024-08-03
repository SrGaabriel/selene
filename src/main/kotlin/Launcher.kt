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
        println("Error: ${result.getLeft().message}")
        return
    }
    val tokenStream = result.getRight();
    println("Lexed! Text:")
    println(text);
    println("Tokens (${tokenStream.count()}):")
    tokenStream.forEach { println(it) }

    val parser = Parser(tokenStream)
    val parsingResult = parser.parse()
    if (parsingResult.isLeft()) {
        println("Error: ${parsingResult.getLeft().message}")
        return
    }
    val syntaxTree = parsingResult.getRight()
    println("Parsed! Syntax tree:")
    println(syntaxTree)

    val executor = KotlinCodeExecutor(syntaxTree)
    println("Executing... Result:")
    executor.execute()
}

fun readText(): String {
    return File("src/main/resources/example.draco").readText()
}