package com.application.bmiobesity.model.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService private constructor(){
    private val BASE_URL = "https://intime.digital"
    private val BASE_URL_TEST = "https://test.intime.digital"
    private val mRetrofit: Retrofit
    private val interceptor = HttpLoggingInterceptor()

    init {
        interceptor.level = HttpLoggingInterceptor.Level.NONE

        val client = OkHttpClient.Builder()
                .addInterceptor(RefreshTokenAuthenticator())
                .addInterceptor(interceptor)
                .build()

        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }

    fun getApi(): InTimeDigitalApi{
        return mRetrofit.create(InTimeDigitalApi::class.java)
    }

    companion object{
        @Volatile
        private var INSTANCE: NetworkService? = null

        fun getNetworkService(): NetworkService{
            return INSTANCE ?: synchronized(this){
                val instance = NetworkService()
                INSTANCE = instance
                instance
            }
        }
    }
}