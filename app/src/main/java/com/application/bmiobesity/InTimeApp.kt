package com.application.bmiobesity

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.application.bmiobesity.di.ApplicationComponent
import com.application.bmiobesity.di.DaggerApplicationComponent

class InTimeApp : MultiDexApplication() {
    companion object{
        lateinit var APPLICATION: Application
        lateinit var appComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        APPLICATION = this
        appComponent = DaggerApplicationComponent.factory().create(applicationContext)
    }
}