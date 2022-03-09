package com.application.bmiantiobesity.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.ui.login.LoginViewModel.Companion.ACCEPTED_TERMS_AND_CONDITIONS
import com.application.bmiantiobesity.ui.login.LoginViewModel.Companion.USER_LOGIN_SETTINGS
import kotlinx.android.synthetic.main.fragment_welcome.view.*


/**
 * A simple [Fragment] subclass.
 */
class WelcomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val action = WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        // Добавление ссылочного текста
        Linkify.addLinks(view.text_welcome, Linkify.WEB_URLS + Linkify.EMAIL_ADDRESSES)

        val sharedPreferences = this.requireContext().getSharedPreferences(USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
        if(sharedPreferences.getBoolean(ACCEPTED_TERMS_AND_CONDITIONS, false)) {
            findNavController().navigate(action)
        }
        view.accept_text.setOnClickListener {
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply {
                putBoolean(ACCEPTED_TERMS_AND_CONDITIONS, true)
            }.apply()

            findNavController().navigate(action)
        }

        return view
    }
}
