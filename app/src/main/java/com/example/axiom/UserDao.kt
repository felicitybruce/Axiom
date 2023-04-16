package com.example.axiom

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun register(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * from user")
    fun getUsers(): List<User>

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User


    @Query("SELECT COUNT(*) FROM user WHERE username = :username AND password = :password")
    fun userExists(username: String, password: String): Int

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM user WHERE password = :password LIMIT 1")
    suspend fun getUserPassword(password: String): User?

}
