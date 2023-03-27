package com.example.axiom

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_login, container, false)

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

        view.findViewById<Button>(R.id.btnLogReg).setOnClickListener{
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }
        return view
    }
}