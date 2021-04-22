package com.application.bmiobesity.model.retrofit

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface InTimeDigitalApi {
    companion object{
        private const val WORK_API = "/api/v2"
        private const val TEST_API = "/api/v2"
    }

    @POST("$WORK_API/countries/")
    fun getCountriesAsync(@Body locale: CurrentLocale): Deferred<Response<ResultListCountries>>
    @POST("$WORK_API/genders/")
    fun getGendersAsync(@Body locale: CurrentLocale): Deferred<Response<ResultListGenders>>
    @GET("$WORK_API/policy-intime/")
    fun getPolicyAsync(@Header(value = "Accept-Language") locale: String): Deferred<Response<ResultPolicy>>

    @POST("$WORK_API/password/reset/")
    fun passwordResetAsync(@Body mail: SendEmail): Deferred<Response<ResultExist>>
    @POST("$WORK_API/password/reset/confirm/")
    fun passwordResetConfirmAsync(@Body confirm: SendConfirmResetPass): Deferred<Response<ResultExist>>

    @POST("$WORK_API/isemailexist/")
    fun isEmailExistAsync(@Body mail: SendEmail): Deferred<Response<ResultExist>>

    @POST("$WORK_API/token/")
    fun getTokenAsync(@Body login: SendLogin): Deferred<Response<ResultToken>>
    @POST("$WORK_API/token/refresh/")
    fun refreshTokenAsync(@Body refresh: SendRefreshToken): Deferred<Response<ResultToken>>

    @POST("$WORK_API/login/social/jwt-pair/")
    fun getGoogleAuthAsync(@Body googleLogin: SendGoogleTokenId): Deferred<Response<ResultTokenFromGoogle>>

    @POST("$WORK_API/signup/")
    fun signUpAsync(@Body login: SendLogin): Deferred<Response<ResultToken>>

    @GET("$WORK_API/profile/")
    fun getProfileAsync(@Header("Authorization") access: String): Deferred<Response<ResultProfile>>
    @PATCH("$WORK_API/profile/")
    fun patchProfileAsync(@Header("Authorization") access: String, @Body profile: SendProfile): Deferred<Response<ResultProfile>>

    @GET("$WORK_API/user_profile/")
    fun getUserProfileAsync(@Header("Authorization") access: String): Deferred<Response<ResultUserProfile>>

    @GET("$WORK_API/med_card/")
    fun getMedCardAsync(@Header("Authorization") access: String): Deferred<Response<ResultMedCard>>

    @GET("$WORK_API/resultdata/")
    fun getFavoritesAsync(@Header("Authorization") access: String, @Header(value = "Accept-Language") locale: String): Deferred<Response<ResultFavorites>>

    @GET("$WORK_API/result/")
    fun getResultAnalyzeAsync(@Header("Authorization") access: String, @Header(value = "Accept-Language") locale: String): Deferred<Response<ResultAnalyze>>

    @GET("$WORK_API/recomendations/")
    fun getRecommendationsAsync(@Header("Authorization") access: String, @Header(value = "Accept-Language") locale: String): Deferred<Response<List<ResultRecommendation>>>
}