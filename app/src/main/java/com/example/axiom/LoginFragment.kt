package com.example.axiom

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt


class LoginFragment : Fragment() {
    private lateinit var appDb: UserRoomDatabase

    private lateinit var passwordLog: EditText
    private lateinit var emailLog: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        appDb = UserRoomDatabase.getDatabase(requireContext())

        // Find views and assign them to the properties
        passwordLog = view.findViewById(R.id.etLoginPassword)
        emailLog = view.findViewById(R.id.etLoginUsernameOrEmail)

        view.findViewById<Button>(R.id.btnLogLog).setOnClickListener {
            lifecycleScope.launch {
                comparePassword(emailLog.text.toString(), passwordLog.text.toString())
            }
            // Hide keyboard on register button click
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        return view
    }
    private suspend fun comparePassword(email: String, password: String) {
        // Log the values from the EditText views
        Log.d("LOGIN", "Email from form: $email, Password from form: $password")

        // Retrieve the user from the Room database
        val user = withContext(Dispatchers.IO) {
            appDb.userDao().getUserByEmail(email)
        }

        // Log the user object stored in db
        Log.d("LOGIN", "User from db: $user")

        if (user == null ) {
            Log.d("LOGIN", "User not found for email: $email")
        } else {
            // Extract the salt value from the user object
            val salt = user.salt

            // Hash the entered password using the retrieved salt value
            val hashedPassword = BCrypt.hashpw(password, salt)

            // Log the hashed password and salt
            Log.d("LOGIN", "Hashed password: $hashedPassword, Salt: $salt")

            // Check if the entered password matches the stored password
            val isMatch = BCrypt.checkpw(password, hashedPassword)

            // Log whether the entered password matched the stored password
            Log.d("LOGIN", "Entered password matches stored password: $isMatch")

            // Check if the entered email matches the stored email
            val isEmailMatch = email == user.email

            // Log whether the entered email matched the stored email
            Log.d("LOGIN", "Entered email matches stored email: $isEmailMatch")

            if (isMatch && isEmailMatch) {
                Log.d("LOGIN", "$email matches hashed password and email matches")
            } else {
                Log.d("LOGIN", "$email does not match")
            }
        }
    }


        private fun showSnackBar(text: String) {
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
