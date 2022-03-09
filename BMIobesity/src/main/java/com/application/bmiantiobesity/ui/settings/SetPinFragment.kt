package com.application.bmiantiobesity.ui.settings

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.ui.login.LoginViewModel

/**
 * A simple [Fragment] subclass.
 */
class SetPinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.set_pin_fragment, container, false)

        val viewModel by viewModels<SettingsViewModel>()

        var pinCode = "0000"

        val btSave = view.findViewById<Button>(R.id.pin_save_button)
        btSave.isEnabled = false

        val pinCodeView = view.findViewById<OtpTextView>(R.id.pin_code)
        pinCodeView.otpListener = object: OTPListener{
            override fun onInteractionListener() {
                btSave.isEnabled = false
            }

            override fun onOTPComplete(otp: String) {
                pinCode = otp
                btSave.isEnabled = true
            }
        }

        btSave.setOnClickListener {
            // Сохраняем Значения
            val sharedPreferences =  this.requireContext().getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
            sharedPreferences.edit{ putString(LoginViewModel.IS_FINGER_TOUCHE, viewModel.cryptoApi.encryptString("true")) }
            sharedPreferences.edit{ putString(LoginViewModel.PIN_CODE, viewModel.cryptoApi.encryptString(pinCode)) }

            Toast.makeText(this.requireContext(), "${this.getString(R.string.pin_code)} $pinCode", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        return view
    }

}
