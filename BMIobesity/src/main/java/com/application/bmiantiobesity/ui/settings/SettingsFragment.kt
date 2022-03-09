package com.application.bmiantiobesity.ui.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.application.bmiantiobesity.models.MeasuringSystem

import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.models.DataTypeInTime
import com.application.bmiantiobesity.models.SetNewDataValue
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.ui.main.MainViewModel
import kotlinx.android.synthetic.main.settings_fragment.view.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel>()

    private var countSelection:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        //val viewModel = ViewModelProvider(this.requireActivity()).get(SettingsViewModel::class.java)
        val sharedPreferences =  this.requireContext().getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)

        // Датчик отпечатка пальца / пин код
        //val btFingerSwitcher = view.findViewById<SwitchMaterial>(R.id.settings_switch_finger)
        // Поддреживается ли аппаратно отпечаток пальца.
        //btFingerSwitcher.isEnabled = Preconditions.hasBiometricSupport(this.requireContext())

        val isFingerTouch = viewModel.cryptoApi.decryptString(sharedPreferences.getString(LoginViewModel.IS_FINGER_TOUCHE, "") ?: "")
        view.settings_switch_finger.isChecked = when (isFingerTouch){
            "true" -> true
            else -> false
        }

        view.settings_switch_finger.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val action = SettingsFragmentDirections.actionSettingsFragmentToSetPinFragment()
                findNavController().navigate(action)
            } else
                sharedPreferences.edit{ putString(LoginViewModel.IS_FINGER_TOUCHE, viewModel.cryptoApi.encryptString("false")) }
        }

        // Система исчисления
        val measures =  resources.getStringArray(R.array.measuring_system).toList()//arrayOf(getString(R.string.measure_metrical), getString(R.string.measure_imperial))

        val adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, measures)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        view.spinner_measure.adapter = adapter
        view.spinner_measure.setSelection(MainViewModel.measuringSystem.toInt() - 1)
        view.spinner_measure.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                MainViewModel.measuringSystem = MeasuringSystem.fromInt(position + 1)
                sharedPreferences.edit {
                    putInt(MainViewModel.MEASURING_SYSTEM, MainViewModel.measuringSystem.toInt())
                }

                // Update Dashboard if it need
                MainViewModel.singleDashBoard?.let {
                    val newValue = SetNewDataValue(DataTypeInTime.MEASURING_SYSTEM, MainViewModel.measuringSystem.toInt().toString(), System.currentTimeMillis())
                    if (countSelection > 0) MainViewModel.publishNewValueFromEvent.onNext(newValue)
                    Log.d("Settings Update db", MainViewModel.singleDashBoard.toString())
                }

                countSelection++ // для того чтобы не отправлять в первый раз при установки начального значения
            }

        }

        return  view
    }

}
