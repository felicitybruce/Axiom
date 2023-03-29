package com.example.axiom

data class UserWithEmailAndSalt(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String,
    val cnfPassword: String,
    val salt: String // add

     )
