package com.example.axiom

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.axiom.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDb: UserRoomDatabase

    private lateinit var account: Auth0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = UserRoomDatabase.getDatabase((this))

        binding.btnRegReg.setOnClickListener {
            nativeValidateForm()
            writeData()
        }

        binding.imgBtnReadData.setOnClickListener {
            readData()
        }

        // Test
        binding.tvGoogle.setOnClickListener {
            loginWithBrowser()
        }
//        binding.btnLogout.setOnClickListener { logout() }


        // Colourful Google TV
        val googleText = "Sign in with Google"
        val spannableString = SpannableString(googleText)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#006DFF")), 13, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.RED), 14, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#F2DC23")), 15, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#006DFF")), 16, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.GREEN), 17, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.RED), 18, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val textView = findViewById<TextView>(R.id.tvGoogle)
        textView.text = spannableString

    }

    private fun nativeValidateForm(): Boolean {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val email = binding.etEmail.text.toString()
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        val cnfPassword = binding.etCnfPassword.text.toString()

        val icon = ContextCompat.getDrawable(this, R.drawable.ic_warning)

        icon?.setBounds(0, 0, 50, 50)

        var isValid = true

        when {
            (TextUtils.isEmpty(firstName.trim())) -> {
                binding.etFirstName.error = "Please Enter First Name"
                binding.etFirstName.setCompoundDrawables(null, null, icon, null)
                isValid = false
            }
            TextUtils.isEmpty(lastName.trim()) -> {
                binding.etLastName.error = "Please Enter Last Name"
                binding.etLastName.setCompoundDrawables(null, null, icon, null)
                isValid = false

            }
            (TextUtils.isEmpty(email.trim())) || !email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) -> {
                binding.etEmail.error = "Please Enter Email In Correct Format"
                binding.etEmail.setCompoundDrawables(null, null, icon, null)
                isValid = false
            }
            TextUtils.isEmpty(username.trim()) -> {
                binding.etUsername.error = "Please Enter Username"
                binding.etUsername.setCompoundDrawables(null, null, icon, null)
                isValid = false

            }
            (TextUtils.isEmpty(password.trim())) || password.length < 5 -> {
                binding.etPassword.error = "Please Enter Password Of At Least 5 Characters"
                binding.etPassword.setCompoundDrawables(null, null, icon, null)
                isValid = false

            }
            TextUtils.isEmpty(cnfPassword.trim()) || password != cnfPassword -> {
                binding.etCnfPassword.error = "Please Ensure Passwords Match"
                binding.etCnfPassword.setCompoundDrawables(null, null, icon, null)
                isValid = false
            }
        }
        return isValid
    }

    private fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")

            // Launch the authentication passing the callback where the results will be received
            .start(this, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
                    showSnackBar("Failure: ${exception.getCode()}")
                }

                override fun onSuccess(credentials: Credentials) {
                    val accessToken = credentials.accessToken
                    showSnackBar("Success: ${credentials.accessToken}")
                }
            })
    }
    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun writeData() {
        nativeValidateForm()
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val email = binding.etEmail.text.toString()
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        val cnfPassword = binding.etCnfPassword.text.toString()

        if (nativeValidateForm()) {
            val user = User(
                null, firstName, lastName, email, username, password, cnfPassword
            )
            // Calling INSERT method from dao
            GlobalScope.launch(Dispatchers.IO) {
                appDb.userDao().insert(user)
            }

            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etEmail.text.clear()
            binding.etUsername.text.clear()
            binding.etPassword.text.clear()
            binding.etCnfPassword.text.clear()
            clearFormIcons()

            Toast.makeText(this@MainActivity, "You are now an official Axiom affiliate 🤗", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "Please fill in all inputs 😶", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFormIcons() {
        binding.etFirstName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.etLastName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.etCnfPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
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