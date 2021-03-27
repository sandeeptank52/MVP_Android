package com.application.bmiobesity.model.retrofit

import com.application.bmiobesity.model.db.paramSettings.entities.Profile
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface InTimeDigitalApi {
    companion object{
        private const val WORK_API = "/api/v2"
        private const val TEST_API = "/api/v2/login/social/jwt-pair/"
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

    @POST("$WORK_API/signup/")
    fun signUpAsync(@Body login: SendLogin): Deferred<Response<ResultToken>>

    @GET("$WORK_API/profile/")
    fun getProfileAsync(@Header("Authorization") access: String): Deferred<Response<ResultProfile>>
    @PATCH("$WORK_API/profile/")
    fun patchProfileAsync(@Header("Authorization") access: String, @Body profile: Profile): Deferred<Response<ResultProfile>>

    @GET("$WORK_API/med_card/")
    fun getMedCardAsync(@Header("Authorization") access: String): Deferred<Response<ResultMedCard>>

    // Testing API
    /*@POST(TEST_API)
    fun getGoogleAuthAsync(@Body login: SendGoogleTokenId): Deferred<Response<ResultTokenFromGoogle>>*/
}