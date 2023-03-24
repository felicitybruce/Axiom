package com.example.axiom

import androidx.room.*
import com.example.axiom.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

//    @Query("SELECT * from user WHERE id = :id")
//    suspend fun getUser(id: Int): Flow<User>

    @Query("SELECT * from user")
    fun getUsers(): List<User>

    @Query("SELECT * FROM user WHERE email LIKE :email LIMIT 1")
    suspend fun findByEmail(email: String): User
}
