package com.example.server

data class User(
    val id: Int? = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String,
    val cnfPassword: String,

    )
