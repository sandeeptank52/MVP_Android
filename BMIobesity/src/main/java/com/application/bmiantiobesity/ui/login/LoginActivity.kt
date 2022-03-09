package com.application.bmiantiobesity.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.models.MeasuringSystem
import com.application.bmiantiobesity.retrofit.Refresh
import com.application.bmiantiobesity.ui.main.MainViewModel
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.reactivex.plugins.RxJavaPlugins


class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var crashlytics: FirebaseCrashlytics

    companion object {
        const val REQUIRE_PERMISSIONS_WRITE_REQUEST_CODE = 1001
    }

    private lateinit var navController: NavController

    private val viewModel by viewModels<LoginViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        initGoogleServices()

        //Выберете основной экран для правильной начальной загрузки
        //val viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        //Отслеживание изменений фрагментов
        navController = findNavController(R.id.nav_login_host_fragment)

        RxJavaPlugins.setErrorHandler { }

        //Запрос прав записи на устройства
        //if (BuildConfig.DEBUG) LoginViewModel.isStoragePermissionGranted = isStoragePermissionGranted()

        // Восстановление имени пользователя и пароля
        val sharedPreferences = getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences?.let {
            val user = UserFirstLogin(
                viewModel.cryptoApi.decryptString(it.getString(LoginViewModel.USER_NAME, "") ?: ""),
                viewModel.cryptoApi.decryptString(it.getString(LoginViewModel.USER_PASSWORD, "") ?: ""),
                it.getBoolean(LoginViewModel.USER_FIRST_LOGIN, false)
            )
            LoginViewModel.liveUser.value = user
            if (user.password.isNotEmpty()) LoginViewModel.safePassword = true

            //Refresh
            val refresh = Refresh(it.getString(LoginViewModel.REFRESH_TOKEN, "") ?: "")
            //Log.d("Refersh -", saveRefresh.toString())
            LoginViewModel.refresh.value = refresh

            val isFingerTouch = viewModel.cryptoApi.decryptString(it.getString(LoginViewModel.IS_FINGER_TOUCHE, "") ?: "")
            LoginViewModel.isFingerTouch = when (isFingerTouch){
                "true" -> true
                else -> false
            }

            // Заполнение СИ
            MainViewModel.measuringSystem = MeasuringSystem.fromInt(it.getInt(MainViewModel.MEASURING_SYSTEM, 0))
        }

    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.loginFragment)
        // LoginFragment
            finishAffinity()
        else{
            super.onBackPressed() // comment
        }
    }

    private fun isStoragePermissionGranted() =
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) true
            else {
                // Разрешения не предоставлены и соответственно их нужно запросить
                requestPermissions(listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(), REQUIRE_PERMISSIONS_WRITE_REQUEST_CODE)
                false
            }
        }else{
             // Права получены при установке
             true
         }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Окончательная проверка всех разрешений при регистрации
        if (requestCode == REQUIRE_PERMISSIONS_WRITE_REQUEST_CODE) LoginViewModel.isStoragePermissionGranted =
            true

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initGoogleServices() {
        firebaseAnalytics = Firebase.analytics
        crashlytics = Firebase.crashlytics
        MobileAds.initialize(this)
    }
}