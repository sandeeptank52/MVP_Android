package com.application.bmiobesity

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.application.bmiobesity.di.ApplicationComponent
import com.application.bmiobesity.di.DaggerApplicationComponent
import com.application.bmiobesity.services.google.billing.GoogleBillingClient
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class InTimeApp : MultiDexApplication() {

    companion object{
        lateinit var APPLICATION: Application
        lateinit var appComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        APPLICATION = this
        appComponent = DaggerApplicationComponent.factory().create(applicationContext)
        //initYandexAppMetric()
    }

    private fun initYandexAppMetric(){
        val config: YandexMetricaConfig = YandexMetricaConfig.newConfigBuilder("6acca38c-8062-42cf-8eef-d4c393b64f28").build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}