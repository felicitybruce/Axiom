package com.example.axiom

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {

    private lateinit var appDb: UserRoomDatabase
    private lateinit var account: Auth0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_register, container, false)

        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        appDb = UserRoomDatabase.getDatabase(requireContext())

        view.findViewById<Button>(R.id.btnRegReg).setOnClickListener {
            nativeValidateForm()
            writeData()
        }

        view.findViewById<TextView>(R.id.tvGoogle).setOnClickListener {
            loginWithBrowser()
        }

        // Colourful Google TV
        val googleText = "Sign in with Google"
        val spannableString = SpannableString(googleText)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#006DFF")), 13, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.RED), 14, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#F2DC23")), 15, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#006DFF")), 16, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.GREEN), 17, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.RED), 18, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val textView = view.findViewById<TextView>(R.id.tvGoogle)
        textView.text = spannableString



        view.findViewById<Button>(R.id.btnLoginReg).setOnClickListener {
            var navLogin = activity as FragmentNavigation
            navLogin.navigateFrag(LoginFragment(), false)
        }
        return view
    }

    private fun nativeValidateForm(): Boolean {
        val firstName = view?.findViewById<EditText>(R.id.etFirstName)?.text.toString()
        val lastName = view?.findViewById<EditText>(R.id.etLastName)?.text.toString()
        val email = view?.findViewById<EditText>(R.id.etEmail)?.text.toString()
        val username = view?.findViewById<EditText>(R.id.etUsername)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.etPassword)?.text.toString()
        val cnfPassword = view?.findViewById<EditText>(R.id.etCnfPassword)?.text.toString()

        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)

        icon?.setBounds(0, 0, 50, 50)

        var isValid = true

        when {
            TextUtils.isEmpty(firstName.trim()) -> {
                view?.findViewById<EditText>(R.id.etFirstName)?.apply {
                    error = "Please Enter First Name"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }

            }
            TextUtils.isEmpty(lastName.trim()) -> {
                view?.findViewById<EditText>(R.id.etLastName)?.apply {
                    error = "Please Enter Last Name"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }

            }
            (TextUtils.isEmpty(email.trim())) || !email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) -> {
                view?.findViewById<EditText>(R.id.etEmail)?.apply {
                    error = "Please Enter Email In Correct Format"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }

            TextUtils.isEmpty(username.trim()) -> {
                view?.findViewById<EditText>(R.id.etUsername)?.apply {
                    error = "Please Enter Username"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
            TextUtils.isEmpty(password.trim()) || password.length < 5 -> {
                view?.findViewById<EditText>(R.id.etPassword)?.apply {
                    error = "Please Enter Password Of At Least 5 Characters"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
            TextUtils.isEmpty(cnfPassword.trim()) || password != cnfPassword -> {
                view?.findViewById<EditText>(R.id.etCnfPassword)?.apply {
                    error = "Please Ensure Passwords Match"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                    isValid = false
                }
            }
        }
        return isValid
    }

    private fun writeData() {
        nativeValidateForm()
        val firstName = view?.findViewById<EditText>(R.id.etFirstName)?.text.toString()
        val lastName = view?.findViewById<EditText>(R.id.etLastName)?.text.toString()
        val email = view?.findViewById<EditText>(R.id.etEmail)?.text.toString()
        val username = view?.findViewById<EditText>(R.id.etUsername)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.etPassword)?.text.toString()
        val cnfPassword = view?.findViewById<EditText>(R.id.etCnfPassword)?.text.toString()

        if (nativeValidateForm()) {
            val user = User(
                null, firstName, lastName, email, username, password, cnfPassword
            )
            // Calling INSERT method from dao
            GlobalScope.launch(Dispatchers.IO) {
                appDb.userDao().insert(user)
            }

            view?.findViewById<EditText>(R.id.etFirstName)?.text?.clear()
            view?.findViewById<EditText>(R.id.etLastName)?.text?.clear()
            view?.findViewById<EditText>(R.id.etEmail)?.text?.clear()
            view?.findViewById<EditText>(R.id.etUsername)?.text?.clear()
            view?.findViewById<EditText>(R.id.etPassword)?.text?.clear()
            view?.findViewById<EditText>(R.id.etCnfPassword)?.text?.clear()
            clearFormIcons()

            Toast.makeText(requireActivity(), "You are now an official Axiom affiliate 🤗", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireActivity(), "Please check all inputs 😶", Toast.LENGTH_SHORT).show()
        }
    }

        private fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")

            // Launch the authentication passing the callback where the results will be received
            .start(requireContext(), object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
                    showSnackBar("Failure: ${exception.getCode()}")
                }

                override fun onSuccess(credentials: Credentials) {
                    val accessToken = credentials.accessToken
                    showSnackBar("Success: ${credentials.accessToken}")
                }
            })

    }

    private fun clearFormIcons() {
        view?.findViewById<EditText>(R.id.etFirstName)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        view?.findViewById<EditText>(R.id.etLastName)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        view?.findViewById<EditText>(R.id.etEmail)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        view?.findViewById<EditText>(R.id.etUsername)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        view?.findViewById<EditText>(R.id.etPassword)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        view?.findViewById<EditText>(R.id.etCnfPassword)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }
        private fun showSnackBar(text: String) {
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
