package com.example.axiom

import com.sun.net.httpserver.HttpServer
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST
import java.net.InetSocketAddress

// Retrofit endpoint
interface ApiService {
    @POST("/hello")
    fun sayHello(): Call<String>
}

fun main() {
    val server = HttpServer.create(InetSocketAddress(8080), 0)
    server.createContext("/hello") { exchange ->
        val response = "Hello, Bunnies!".toByteArray()
        exchange.sendResponseHeaders(200, response.size.toLong())
        exchange.responseBody.write(response)
        exchange.responseBody.close()
    }
    server.start()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val helloService = retrofit.create(ApiService::class.java)

    val call = helloService.sayHello()
    val response = call.execute()

    println(response.body())
}