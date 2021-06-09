package com.application.bmiobesity.view.labelActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.bmiobesity.databinding.LabelActivityBinding

class LabelActivity : AppCompatActivity() {

    private lateinit var labelBinding: LabelActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        labelBinding = LabelActivityBinding.inflate(layoutInflater)
        setContentView(labelBinding.root)
    }
}