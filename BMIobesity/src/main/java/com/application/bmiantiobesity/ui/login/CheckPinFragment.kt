package com.application.bmiantiobesity.ui.login

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.application.bmiantiobesity.ui.main.MainActivity
import com.application.bmiantiobesity.R

class CheckPinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.set_pin_fragment, container, false)

        //val viewModel = ViewModelProvider(this.requireActivity()).get(LoginViewModel::class.java)
        val viewModel by viewModels<LoginViewModel>()

        val sharedPreferences =  this.requireContext().getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)

        var pinCode = viewModel.cryptoApi.decryptString(sharedPreferences?.getString(LoginViewModel.PIN_CODE, "0000") ?: "0000")
        if (pinCode.length < 4) pinCode = "0000"

        val btSave = view.findViewById<Button>(R.id.pin_save_button)
        btSave.isVisible = false

        val pinCodeView = view.findViewById<OtpTextView>(R.id.pin_code)
        pinCodeView.otpListener = object: OTPListener {
            override fun onInteractionListener() {
                //pinCodeView.resetState()
            }

            override fun onOTPComplete(otp: String) {
                if (pinCode == otp) {
                    pinCodeView.showSuccess()
                    startMainActivity<MainActivity>(view.context, LoginViewModel.singleResultToken,  sharedPreferences.getBoolean(LoginViewModel.USER_FIRST_LOGIN, false))
                }
                else {
                    pinCodeView.showError()
                    Toast.makeText(this@CheckPinFragment.requireContext(), getString(R.string.error_pin_code), Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

}
