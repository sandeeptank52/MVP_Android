package com.application.bmiobesity.view.mainActivity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainActivityBinding
import com.application.bmiobesity.utils.EventObserver
import com.application.bmiobesity.viewModels.MainViewModel
import com.application.bmiobesity.viewModels.eventManagerMain.EventManagerMain
import com.application.bmiobesity.viewModels.eventManagerMain.MainActivityEvent
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: MainActivityBinding
    private val mainModel: MainViewModel by viewModels()
    private val eventManager: MainActivityEvent = EventManagerMain.getEventManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = MainActivityBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.mainBottomNavigationView).setupWithNavController(navController)

        addListeners()
        init()
    }

    private fun init(){
        mainModel.toString()
    }

    private fun addListeners(){
        eventManager.getPreloadSuccessEvent().observe(this, EventObserver{
            if (it) mainBinding.mainFrameLayoutWaiting.visibility = View.GONE
        })
    }
}