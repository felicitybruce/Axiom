package com.example.axiom

import com.sun.net.httpserver.HttpServer
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import java.net.InetSocketAddress

// Retrofit endpoint
interface RestApi {
    @GET("/hello")
    fun sayHello(): Call<String>

    @GET("/test")
    fun test(): Call<String>
}

fun main() {
    val server = HttpServer.create(InetSocketAddress(8080), 0)
    server.createContext("/") { exchange ->
        exchange.sendResponseHeaders(200, 0) // set response length to 0
        exchange.responseBody.close() // close response body without writing any bytes
    }

    server.createContext("/hello") { exchange ->
        if (exchange.requestMethod == "GET") {
            val response = "Hello, Bunnies!".toByteArray()
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.write(response)
            exchange.responseBody.close()
        } else {
            val message = "Soz, not allowed"
            exchange.sendResponseHeaders(405, message.toByteArray().size.toLong())
            exchange.responseHeaders.set("Content-Type", "text/plain")
            exchange.responseBody.write(message.toByteArray())
            exchange.responseBody.close()
        }
    }

    server.createContext("/test") { exchange ->
        if (exchange.requestMethod == "GET") {
            val response = "This is a test endpoint!".toByteArray()
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.write(response)
            exchange.responseBody.close()
        } else {
            val message = "Soz, not allowed"
            exchange.sendResponseHeaders(405, message.toByteArray().size.toLong())
            exchange.responseHeaders.set("Content-Type", "text/plain")
            exchange.responseBody.write(message.toByteArray())
            exchange.responseBody.close()
        }
    }
    server.start()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    println("Server running on port ${retrofit.baseUrl().port} 💥")
}