package com.example.axiom

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt



class LoginFragment : Fragment() {
    private lateinit var appDb: UserRoomDatabase
    private lateinit var account: Auth0


    private lateinit var passwordLog: EditText
    private lateinit var emailLog: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        appDb = UserRoomDatabase.getDatabase(requireContext())

        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        // Colourful Google TV
        val googleText = "Sign in with Google"
        val spannableString = SpannableString(googleText)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#006DFF")),
            13,
            14,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            14,
            15,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#F2DC23")),
            15,
            16,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#006DFF")),
            16,
            17,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.GREEN),
            17,
            18,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            18,
            19,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val textView = view.findViewById<TextView>(R.id.tvGoogle)
        textView.text = spannableString


        // Find views and assign them to the properties
        passwordLog = view.findViewById(R.id.etLoginPassword)
        emailLog = view.findViewById(R.id.etLoginUsernameOrEmail)

        //REGISTER button navigates to register page
        view.findViewById<Button>(R.id.btnLogReg).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }


        view.findViewById<Button>(R.id.btnLogLog).setOnClickListener {
            // Hide keyboard on register button click
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            lifecycleScope.launch {
                comparePassword(emailLog.text.toString(), passwordLog.text.toString())
            }
        }

        return view
    }

    // MAIN CODE
    private fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")

            // Launch the authentication passing the callback where the results will be received
            .start(requireContext(), object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    showSnackBar("Failure: ${error.getCode()}")
                }

                override fun onSuccess(result: Credentials) {
                    val accessToken = result.accessToken
                    showSnackBar("Success: ${result.accessToken}")
                }
            })
    }

    private fun clearFormIcons() {
        view?.findViewById<EditText>(R.id.etLoginUsernameOrEmail)?.text?.clear()
        view?.findViewById<EditText>(R.id.etLoginPassword)?.text?.clear()

        //view?.findViewById<EditText>(R.id.etLoginUsernameOrEmail)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        //view?.findViewById<EditText>(R.id.etLoginPassword)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
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
                clearFormIcons()
                Log.d("LOGIN", "$email matches hashed password and email matches")
            } else {
                Log.d("LOGIN", "$email does not match")
            }

        }
        showSnackBar("Please fill in all fields correctly.")

    }


        private fun showSnackBar(text: String) {
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
// TODO: 1)intrinsic bound error icons for login