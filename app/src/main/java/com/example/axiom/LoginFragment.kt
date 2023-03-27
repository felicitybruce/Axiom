package com.example.axiom

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment


class LoginFragment : Fragment() {

    private lateinit var logUserOrEmail: EditText
    private lateinit var logPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)

        // Find views and assign them to the properties
        logUserOrEmail = view.findViewById<EditText>(R.id.etLoginUsernameOrEmail)
        logPassword = view.findViewById<EditText>(R.id.etLoginPassword)

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

        view.findViewById<Button>(R.id.btnLogLog).setOnClickListener{
            checkEditTextsAreEmpty(logUserOrEmail, logPassword)

            // Hide keyboard on register button click
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        view.findViewById<Button>(R.id.btnLogReg).setOnClickListener{
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }
        return view
    }

    private fun checkEditTextsAreEmpty(editText1: EditText?, editText2: EditText?) {
        if (editText1?.text?.isEmpty() == true || editText2?.text?.isEmpty() == true) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            // Do something else if both fields are not empty
            Toast.makeText(requireContext(), "happy happy happy", Toast.LENGTH_SHORT).show()

        }
    }
}