package com.example.axiom

import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST

interface ApiService {
    @POST("/hello")
    suspend fun sayHello(): String
}

fun main() {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    runBlocking {
        val response = apiService.sayHello()
        println(response) // Output: "Hello, World!"
    }
}
