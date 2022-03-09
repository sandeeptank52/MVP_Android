package com.application.bmiantiobesity.ui.resetpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.ui.login.LoginViewModel

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_activity)
        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ResetPasswordFragment.newInstance())
                .commitNow()
        }*/

        // Восстановление пароля по перехвату ссылки ссылке
        val action = intent.action
        val data = intent.dataString
        if (Intent.ACTION_VIEW == action && data != null) LoginViewModel.liveResetPasswordString.value = data.toString()
    }

}
