package com.application.bmiantiobesity.ui.settings


import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.ui.googlefitapi.GoogleFitApiModel
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.ui.main.MainViewModel
import kotlinx.android.synthetic.main.connect_to_fragment.view.*


/**
 * A simple [Fragment] subclass.
 */
class ConnectToFragment : Fragment() {

    companion object {
        const val REQUIRE_PERMISSIONS_REQUEST_CODE = 1
        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 2
    }

    //Подключение viewModel
    //private lateinit var fitnessViewModel: GoogleFitApiModel
    private val fitnessViewModel by viewModels<GoogleFitApiModel>()

    //private lateinit var googleFitApiButton: Button

    private var isAccessToGoogleFitApi = MutableLiveData<Boolean>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Подключение модели для отправки туда информации их Google Fit
        //fitnessViewModel = ViewModelProvider(this).get(GoogleFitApiModel::class.java)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.connect_to_fragment, container, false)

        val sharedPreferences = context?.getSharedPreferences(GoogleFitApiModel.GOOGLE_API_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences?.let {
            isAccessToGoogleFitApi.value = it.getBoolean(GoogleFitApiModel.ACCESS_TO_API, false)
        }

        //googleFitApiButton = view.findViewById(R.id.connect_to_google_fit_imageView)

        // Получение результата подключения
        MainViewModel.accessToGoogleFitApi.observe(this.viewLifecycleOwner, Observer { isAccessToGoogleFitApi.value = it })

        // Визуализация кнопки
        isAccessToGoogleFitApi.observe(this.viewLifecycleOwner, Observer  {
            if (it){
                //googleFitApiButton.background.alpha = 80
                //view.textView_status.text = getString(R.string.already_connect_to_fit)
                view.text_google_fit.setTextColor(Color.BLACK)
                view.image_cloud.setImageResource(R.drawable.ic_sky_blue)
                view.image_connect.setImageResource(R.drawable.ic_lock_blue)
            }
            else{
                //googleFitApiButton.background.alpha = 255
                //view.textView_status.text = getString(R.string.click_to_icon)
                view.text_google_fit.setTextColor(Color.GRAY)
                view.image_cloud.setImageResource(R.drawable.ic_sky_gray)
                view.image_connect.setImageResource(R.drawable.ic_add_device)
            }
        })


        val onGoogleFitListener = View.OnClickListener {
            if (isAccessToGoogleFitApi.value == true){
                // Отключение от Google Fit
                safePreferences(false)
                isAccessToGoogleFitApi.value = false
                MainViewModel.accessToGoogleFitApi.value = false

            }else {
                // Подключение к Google Fit
                if (ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
                    // Сохранение access to api
                    safePreferences(true)
                    isAccessToGoogleFitApi.value = true
                    // Сообщение что больше ничего не требуется
                    //Toast.makeText(this.requireContext(), getString(R.string.already_connect_to_fit), Toast.LENGTH_SHORT).show()
                    //fitnessViewModel.startGoogleFitnessApi(this.requireActivity() as AppCompatActivity)

                } else {
                    // Разрешения не предоставлены и соответственно их нужно запросить
                    requestPermissions( //this.requireContext() as AppCompatActivity,
                        listOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BODY_SENSORS).toTypedArray(),
                        REQUIRE_PERMISSIONS_REQUEST_CODE)
                }
            }
        }

        //googleFitApiButton.setOnClickListener(onGoogleFitListener)
        view.card_fit.setOnClickListener (onGoogleFitListener)


        /*MainViewModel.accessToGoogleFitApi.observe(this.requireActivity(), Observer {
            if (it) fitnessViewModel.startGoogleFitnessApi(this.requireActivity() as AppCompatActivity)
        })*/

        val btNext = view.findViewById<Button>(R.id.connect_next_button)
        if (SettingsActivity.firstLoad) {
            btNext.setOnClickListener {

                // Сохраняем Значения что уже не первый раз
                context?.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)?.edit { putBoolean(LoginViewModel.USER_FIRST_LOGIN, false) }

                // Возвращение к главной активити
                activity?.finish()
            }
        } else {
            btNext.setText(R.string.button_back)
            btNext.setOnClickListener {
                // Возвращение к главной активити
                activity?.finish()
            }
        }

        return view
    }

    // Результат предоставления разрешений
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

            if (requestCode == REQUIRE_PERMISSIONS_REQUEST_CODE) {
                fitnessViewModel.startGoogleFitnessApi(this.requireActivity() as AppCompatActivity)
                // Сохранение access to api
                safePreferences(true)
            }
            // Сюда не попадет, этот обработчик находится в SettingsActivity
            else if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                fitnessViewModel.accessGoogleFitHistory(this.requireActivity() as AppCompatActivity)
                fitnessViewModel.accessGoogleFitSensors(this.requireActivity() as AppCompatActivity)
                // Сохранение access to api
                //safePreferences(true)

            }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun safePreferences(value:Boolean) {
        context?.let { itContext ->
            val sharedPreferences = itContext.getSharedPreferences(GoogleFitApiModel.GOOGLE_API_SETTINGS, Context.MODE_PRIVATE)
            sharedPreferences?.edit { putBoolean(GoogleFitApiModel.ACCESS_TO_API, value) }
        }
    }

}
