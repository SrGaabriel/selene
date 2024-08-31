package me.gabriel.gwydion.compiler

import java.net.Socket

import java.io.*

fun main() {
    val socket = Socket("localhost", 8080)
    val out = PrintWriter(socket.getOutputStream(), true)
    val input = BufferedReader(InputStreamReader(socket.getInputStream()))

    val message = "Hello, World!"
    out.println(message)
    out.flush()
    println("Sent: $message")

    val response = input.readLine()
    println("Server echoed: $response")

    out.close()
    input.close()
    socket.close()
}