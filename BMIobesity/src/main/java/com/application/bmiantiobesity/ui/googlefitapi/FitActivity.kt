package com.application.bmiantiobesity.ui.googlefitapi

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.application.bmiantiobesity.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class FitActivity : AppCompatActivity() {

    companion object {
        private const val REQUIRE_PERMISSIONS_REQUEST_CODE = 1
        private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 2
    }

    private var disposableGetDashBoard: Disposable? = null

    //Подключение viewModel
    //private lateinit var fitnessViewModel: GoogleFitApiModel
    private val fitnessViewModel by viewModels<GoogleFitApiModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fit_activity)

        //Подключение модели для отправки туда информации их Google Fit
        //fitnessViewModel = ViewModelProvider(this).get(GoogleFitApiModel::class.java)


        val textView = findViewById<TextView>(R.id.fit_text_view)
        // Подписка на обновление DashBoard (не инициализируется вначале) fot Test
        disposableGetDashBoard = fitnessViewModel.liveText
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {next ->
                    textView.append("\n ${next}") },
                {error ->  Log.d("Error FA-", error.message ?: "")
                    textView.append(error.message)})


        // Проверка на выдачу разрешений к правам доступа
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            fitnessViewModel.startGoogleFitnessApi(this)
        } else {
            ActivityCompat.requestPermissions(this,
                listOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.BODY_SENSORS).toTypedArray(),
                REQUIRE_PERMISSIONS_REQUEST_CODE
            )
        }

    }

    // Проверка ответов о FIT API о пердоставлении прав
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == REQUIRE_PERMISSIONS_REQUEST_CODE) {
                fitnessViewModel.startGoogleFitnessApi(this)
            } else if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                fitnessViewModel.accessGoogleFitHistory(this)
                fitnessViewModel.accessGoogleFitSensors(this)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*override fun onBackPressed() {
        // Отсоединиться
        //if (isLogin) Fitness.getConfigClient(this, GoogleSignIn.getLastSignedInAccount(this)!!).disableFit()
        super.onBackPressed()
    }*/

    override fun onDestroy() {
        disposableGetDashBoard?.let { if (!it.isDisposed) it.dispose()}
        super.onDestroy()
    }
}