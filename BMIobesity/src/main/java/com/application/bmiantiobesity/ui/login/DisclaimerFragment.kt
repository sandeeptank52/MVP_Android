package com.application.bmiantiobesity.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.application.bmiantiobesity.ui.main.MainActivity

import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.retrofit.ResultToken
import com.application.bmiantiobesity.ui.login.LoginViewModel.Companion.USER_HAS_JUST_REGISTERED
import com.google.android.material.switchmaterial.SwitchMaterial


class DisclaimerFragment : Fragment() {

    private val sharedPreferences: SharedPreferences by lazy {
        this.requireContext().getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        //val viewModel = ViewModelProvider(this.requireActivity()).get(LoginViewModel::class.java)
        val viewModel by viewModels<LoginViewModel>()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.disclaimer_fragment, container, false)

        // Добавление ссылочного текста
        //Linkify.addLinks(view.text_welcome , Linkify.ALL)

        // Восстановление сохранённых значений
        var isNotDisclaimer = false
        sharedPreferences.let { isNotDisclaimer = it.getBoolean(LoginViewModel.IS_NOT_DISCLAIMER, false) }

        val disclaimerSwitcher = view.findViewById<SwitchMaterial>(R.id.disclaimer_switch)
        disclaimerSwitcher.isChecked = isNotDisclaimer
        disclaimerSwitcher.setOnCheckedChangeListener { _, isChecked -> sharedPreferences.edit { putBoolean(LoginViewModel.IS_NOT_DISCLAIMER, isChecked) } }

        var firstLogin = true
        LoginViewModel.liveUser.observe(this.requireActivity(), Observer<UserFirstLogin> {
            firstLogin = it.firstLogin
        })

        // Получение refresh token
        var resultToken: ResultToken? = null
        LoginViewModel.liveResultToken.observe(this.viewLifecycleOwner, Observer<ResultToken> {
            resultToken = it
        })

        val nextBt = view.findViewById<Button>(R.id.diclaimer_next_button)
        nextBt.setOnClickListener {
            resultToken.let { token ->
                if (token != null) {
                    if (sharedPreferences.getBoolean(USER_HAS_JUST_REGISTERED, false)) {
                        navigateFromDisclaimer()
                    } else {
                        // Start Main Activity
                        if (!LoginViewModel.isFingerTouch) {
                            startMainActivity<MainActivity>(view.context, token, firstLogin)
                        } else {
                            viewModel.checkFinger(this, { startMainActivity<MainActivity>(view.context, token, firstLogin) }, { findNavController().navigate(R.id.checkPinFragment) })
                        }    //control finger
                    }
                } else {
                    navigateFromDisclaimer()
                }
            }
        }

        return view
    }

    private fun navigateFromDisclaimer() {
        //User has accepted the Terms and conditions on WelcomeFragment so we go directly to Login
        if (sharedPreferences.getBoolean(LoginViewModel.ACCEPTED_TERMS_AND_CONDITIONS, false)) {
            findNavController().navigate(DisclaimerFragmentDirections.actionDisclaimerFragmentToLoginFragment())
        } else { //Go to welcome page to accept terms and conditions
            findNavController().navigate(DisclaimerFragmentDirections.actionDisclaimerFragmentToWelcomeFragment())
        }
    }

}
