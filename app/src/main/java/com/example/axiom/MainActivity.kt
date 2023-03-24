package com.example.axiom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.axiom.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDb: UserRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = UserRoomDatabase.getDatabase((this))

        binding.btnRegReg.setOnClickListener {
            writeData()
        }

        binding.imgBtnReadData.setOnClickListener {
            readData()
        }
    }

    private fun writeData() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val email = binding.etEmail.text.toString()
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        val cnfPassword = binding.etCnfPassword.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            val user = User(
                null, firstName, lastName, email, username, password, cnfPassword
            )
            // Calling INSERT method from dao
            GlobalScope.launch(Dispatchers.IO) {
                appDb.userDao().insert(user)
            }

            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()

            Toast.makeText(this@MainActivity, "You are now an official Axiom affiliate 🤗", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this@MainActivity, "Please fill in all inputs 😶", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun displayData(user: User) {
        withContext(Dispatchers.Main) {
            binding.tvFirstName.text = user.firstName
            binding.tvLastName.text = user.lastName
            binding.tvEmail.text = user.email
            binding.tvUsername.text = user.username
            binding.tvPassword.text = user.password
            binding.tvCnfPassword.text = user.cnfPassword

        }
    }

    private fun readData() {

        val email = binding.etEmail.text.toString()

        if (email.isNotEmpty()) {
            GlobalScope.launch(Dispatchers.IO) {
                val user = appDb.userDao().findByEmail(email)
                withContext(Dispatchers.Main) {
                    displayData(user)
                }
            }
        } else {
            Toast.makeText(this@MainActivity, "Hi Developer, please fill in email field correctly 👍", Toast.LENGTH_SHORT).show()
        }
    }
}