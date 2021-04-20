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
        val authHeader = originalRequest.header("Authorization") ?: return chain.proceed(originalRequest)

        val tokeh = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhY2Nlc3MiOiJhY2Nlc3MiLCJleHAiOjE2MTg2MjEzNzYsImp0aSI6ImFmMDViMGY0ZDYyMzQ0MDk4ZDQ4ZDgwZTQ1YTI5NDQ0IiwidXNlcl9pZCI6IjcxNTgxOTFhLThkZDQtNGU0Mi1hNzBiLTQ1YWI3Mjk3NzcwMyJ9.RLIZ8HrZK-O7J4noQnrtDNUxFAmz9FPt-pqKDeIMZ_buwwwOl6er9pYfvdvgSheGdM6RSxfdFIey8nR3dV_OQvwQpy51efAoPlKPdR91cz0KvZOqE0RyGS-PeaG42AqH7eAwDRKdQIM3uhcmphIIaq0ErY3ji47Jo_kGhZig2XXE_lW6JGsYLTDQkORfzC8P-wByxlRJZtyjrNHX-qvZfSz_DCW1QLT4Fs0vzCXlpdnEKQu8rMhBrx0s9POwsFqDnY-Kfj1DB70GpfB4A5zokfZX3naRkrqPiKJ-zlKnFK89oDPo9Lk45OFv9AXjowh-9DUFPXkBGdd-wWga2HAlSNHzZwUvMFYS5C0IHZC8SzEXKijCYIXlCB5LvZghQ_iBzVopEU3hFOuhyqu1ATy4clFI7D_BqfaEbGSUjv4bvZDTPxlE6DTc-Kz0S58A8nppC4t4bi0110z5sYWuPw39dfrXe8cyRGo-OogUdnN3BSfDo0cCfun4SHDNbfxOAyJ-x6mp1r7y7yQDnI122WwXGyCltFgtKE2jQ7rQbZTlFkFonxgLqROAPBz3Ip_9V3XXO29uNQMsGF9JOrM-P_0sNeLTt_Sfxx6UgGqP7tBb5RHOYuAYACCnDrT3K0q1T-uR8zd27nSTRQeyhGoD1E0agSBDJFtXvGJVO3DpzkRZx9M"

        synchronized(this){
            val newRequest = originalRequest.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", tokeh)
                    .method(originalRequest.method, originalRequest.body)
                    .build()

            val response = chain.proceed(newRequest)
            val code = response.code

            if (code == 403){
                updateToken(newRequest.url.encodedPath)

                val validRequest = newRequest.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", tokeh)
                        .method(newRequest.method, newRequest.body)
                        .build()

                //return chain.proceed(validRequest)
            }

            return response
        }
    }

    private fun updateToken(rrr: String){
        Log.d("TEST", "--update--")
        runBlocking {
            appSetting.setStringParam(AppSettingDataStore.PrefKeys.REFRESH_TOKEN, rrr)
            //delay(5000)

        }

        /*
        val sendRefresh = SendRefresh(REFRESH_TOKEN)
        val sendRefreshToken = SendRefreshToken(sendRefresh, sendDevice)
        val response = refreshService.getApi().refreshToken(sendRefreshToken).execute()
        val access = response.body()?.access
        val refresh = response.body()?.refresh
        */
    }
}