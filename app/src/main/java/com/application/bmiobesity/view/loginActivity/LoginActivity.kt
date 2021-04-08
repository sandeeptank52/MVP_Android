package com.application.bmiobesity.view.loginActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LoginActivityBinding
import com.application.bmiobesity.databinding.LoginActivityV2Binding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}