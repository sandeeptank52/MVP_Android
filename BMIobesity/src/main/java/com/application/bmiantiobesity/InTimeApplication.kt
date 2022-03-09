package com.application.bmiantiobesity

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class InTimeApplication : MultiDexApplication(){

    companion object {
        var component: AppComponent? = null
        lateinit var APPLICATION : Application
        //var viewModelSubComponent: ViewModelSubComponent? = null
        //private set
    }

    private fun buildComponent(): AppComponent {
        return DaggerAppComponent
            .builder()
            //.withApplication(this)
            .restApiModule(RestApiModule())
            .contextModule(ContextModule(this))
            .cryptoApiModule(CryptoApiModule())
            .storageModule(StorageModule())
            .build()
            //.contextModule(ContextModule(this))
    }

    //private fun buildSubComponent() = component?.viewModelSubComponentBuilder()?.build()


    override fun onCreate() {
        super.onCreate()
        //Генерация контекста инекций
        component = buildComponent()
        APPLICATION = this
        initYandexMetrics()
        //viewModelSubComponent = buildSubComponent()
    }

    private fun initYandexMetrics() {
        val yandexConfig: YandexMetricaConfig =
            YandexMetricaConfig.newConfigBuilder("6acca38c-8062-42cf-8eef-d4c393b64f28").build()
        YandexMetrica.activate(applicationContext, yandexConfig)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}