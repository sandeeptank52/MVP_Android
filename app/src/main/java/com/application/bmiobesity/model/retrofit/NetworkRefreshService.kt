package com.application.bmiobesity.model.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRefreshService private constructor(){
    private val BASE_URL = "https://intime.digital"
    private val BASE_URL_TEST = "https://test.intime.digital"
    private val mRetrofit: Retrofit
    private val interceptor = HttpLoggingInterceptor()

    init {
        interceptor.level = HttpLoggingInterceptor.Level.NONE

        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    fun getApi(): InTimeRefreshApi{
        return mRetrofit.create(InTimeRefreshApi::class.java)
    }

    companion object{
        @Volatile
        private var INSTANCE: NetworkRefreshService? = null

        fun getNetworkService(): NetworkRefreshService{
            return INSTANCE ?: synchronized(this){
                val instance = NetworkRefreshService()
                INSTANCE = instance
                instance
            }
        }
    }
}