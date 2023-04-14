package com.example.axiom

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress


fun main() {
    val server = HttpServer.create(InetSocketAddress(8080), 0)
    server.createContext("/hello") { exchange: com.sun.net.httpserver.HttpExchange ->
        val response = "Hello, World!".toByteArray()
        exchange.sendResponseHeaders(200, response.size.toLong())
        exchange.responseBody.write(response)
        exchange.responseBody.close()
    }
    server.start()
}
