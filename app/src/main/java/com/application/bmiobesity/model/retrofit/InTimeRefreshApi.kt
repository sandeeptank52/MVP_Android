package com.application.bmiobesity.model.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface InTimeRefreshApi {
    companion object{
        private const val WORK_API = "/api/v2"
    }

    @POST("${WORK_API}/token/refresh/")
    fun refreshToken(@Body refresh: SendRefreshToken): Call<ResultToken>
}