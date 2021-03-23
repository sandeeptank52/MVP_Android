package com.application.bmiobesity.view.mainActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.bmiobesity.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = MainActivityBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
    }
}