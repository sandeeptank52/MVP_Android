package com.application.bmiantiobesity.ui.main


import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.view.isGone
import com.application.bmiantiobesity.*

import com.application.bmiantiobesity.utilits.getCurrentLocale
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.utilits.getAndroidID
import com.application.bmiantiobesity.utilits.getUUID

import kotlinx.android.synthetic.main.doctor_fragment.view.*

//import com.application.intime.setToolbarTitle

/**
 * A simple [Fragment] subclass.
 */
class DoctorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.doctor_fragment, container, false)

        // Настройка Toolbar
        //setToolbarTitle<MainActivity>(R.id.main_toolbar, getString(R.string.doctor))

        val btLogOff = view.findViewById<Button>(R.id.button_log_off)
        if (!BuildConfig.DEBUG) btLogOff.isGone = true
        btLogOff.setOnClickListener{
            // Удаление refresh token и выход
            context?.let {itContext ->
                val sharedPreferences = itContext.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
                sharedPreferences?.edit (true) { putString(LoginViewModel.REFRESH_TOKEN, "-0-")}
            }

            //Ожидание записи token чтобы наверняка успелось записать
            //Thread.sleep(200)

            activity?.finishAffinity()
        }

        view.button_crash.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        /*val btGetGoogleApi = view.findViewById<Button>(R.id.button_get_google_api)
        if (!BuildConfig.DEBUG) btGetGoogleApi.isGone = true
        btGetGoogleApi.setOnClickListener{
            val intent = Intent( context?.applicationContext , FitActivity::class.java)
            startActivity(intent)
        }*/


        //val temp = Build.BRAND

        val doctorText = view.findViewById<TextView>(R.id.doctor_textview)
        if (!BuildConfig.DEBUG) doctorText.text = getText(R.string.coming_soon)
        else doctorText.setText("Locale - ${getCurrentLocale(context!!).toString().toLowerCase()} \n" +
                           "Device - ${Build.DEVICE} \n" +
                           "Product - ${Build.PRODUCT} \n" +
                           "Model - ${Build.BRAND} ${Build.MODEL} \n" +
                           "Android_ID - ${getAndroidID(context!!)} \n" +
                           "OS Version - ${Build.VERSION.RELEASE} \n" +
                           "Version Name - ${BuildConfig.VERSION_NAME} \n" +
                           "UUID -\n${getUUID(context!!)}")

        return view
    }


}
