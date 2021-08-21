package com.application.bmiobesity.view.loginActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LoginActivityV2V2Binding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityV2V2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityV2V2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}