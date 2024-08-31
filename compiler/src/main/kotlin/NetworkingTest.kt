package me.gabriel.gwydion.compiler

import java.net.Socket

import java.io.*

/**
 * This is a test for the `networking_prototype.ll` code.
 * It creates a socket and sends a message to the server.
 *
 * I won't be removing it just to further test the compiler and equivalence to that code.
 */
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