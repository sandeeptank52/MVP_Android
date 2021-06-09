package com.application.bmiobesity.model.retrofit

import android.os.Build
import android.util.Log
import com.application.bmiobesity.BuildConfig
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import okhttp3.*
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext

class RefreshTokenAuthenticator : Authenticator, Interceptor {

    @Inject
    lateinit var appSetting: AppSettingDataStore
    private val refreshService = NetworkRefreshService.getNetworkService()
    private lateinit var sendDevice: SendDevice

    private var REFRESH_TOKEN = ""
    private var ACCESS_TOKEN = ""

    init {
        InTimeApp.appComponent.inject(this)

        val scope = CoroutineScope(Job() + Dispatchers.IO)

        scope.launch {

            sendDevice = SendDevice(appSetting.getStringParam(AppSettingDataStore.PrefKeys.DEVICE_UUID).first(),
                    AppSettingDataStore.Constants.OS_NAME,
                    Build.VERSION.RELEASE,
                    "${Build.BRAND} - ${Build.MODEL}",
                    BuildConfig.VERSION_NAME)

            val accessFlow = appSetting.getStringParam(AppSettingDataStore.PrefKeys.ACCESS_TOKEN)
            val refreshFlow = appSetting.getStringParam(AppSettingDataStore.PrefKeys.REFRESH_TOKEN)

            accessFlow.combine(refreshFlow){access, refresh ->
                ACCESS_TOKEN = access
                REFRESH_TOKEN = refresh
            }.collect()
        }
    }


    override fun authenticate(route: Route?, response: Response): Request? {
        return null
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        originalRequest.header("Authorization") ?: return chain.proceed(originalRequest)

        synchronized(this){
            val newRequest = originalRequest.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
                    .method(originalRequest.method, originalRequest.body)
                    .build()

            val response = chain.proceed(newRequest)
            val code = response.code

            if (code == 403){
                updateToken()

                val validRequest = newRequest.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
                        .method(newRequest.method, newRequest.body)
                        .build()

                return chain.proceed(validRequest)
            }

            return response
        }
    }

    private fun updateToken(){
        val sendRefresh = SendRefresh(REFRESH_TOKEN)
        val sendRefreshToken = SendRefreshToken(sendRefresh, sendDevice)
        val response = refreshService.getApi().refreshToken(sendRefreshToken).execute()
        val access = response.body()?.access
        val refresh = response.body()?.refresh
        if (!access.isNullOrEmpty() && !refresh.isNullOrEmpty()){
            this.ACCESS_TOKEN = access
            this.REFRESH_TOKEN = refresh
            runBlocking {
                appSetting.setStringParam(AppSettingDataStore.PrefKeys.REFRESH_TOKEN, refresh)
                appSetting.setStringParam(AppSettingDataStore.PrefKeys.ACCESS_TOKEN, access)
            }
        }
    }
}