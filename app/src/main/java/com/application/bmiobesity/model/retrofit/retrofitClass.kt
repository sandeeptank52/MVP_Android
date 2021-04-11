package com.application.bmiobesity.model.retrofit

import okhttp3.ResponseBody
import java.lang.Exception

sealed class RetrofitResult<out T: Any>{
    data class Success <out T: Any> (val value: T, val code: Int, val message: String): RetrofitResult<T>()
    data class Error(val errorMessage: String, val code: Int, val responseBody: ResponseBody?) : RetrofitResult<Nothing>()
}
suspend fun <T: Any> safeApiCall(call: suspend () -> RetrofitResult<T>): RetrofitResult<T> =
try {
    call.invoke()
} catch (e: Exception){
    RetrofitResult.Error(e.message ?: "exception", 0, null)
}

fun errorCheck(code: Int, message: String, responseBody: ResponseBody?): RetrofitError{
    return if (code == 0){
        when(message){
            "Unable to resolve host \"intime.digital\": No address associated with hostname" -> RetrofitError.NO_INTERNET_CONNECTION
            else -> RetrofitError.UNKNOWN_ERROR
        }
    } else {
        when(code){
            401 -> if (message == "Unauthorized") RetrofitError.PASS_INCORRECT else RetrofitError.UNKNOWN_ERROR
            404 -> if (message == "Not Found") RetrofitError.MAIL_NOT_FOUND else RetrofitError.UNKNOWN_ERROR
            else -> RetrofitError.UNKNOWN_ERROR
        }
    }
}

data class CurrentLocale( val locale: String )

data class SendEmail( val email: String)
data class SendUser(var email:String, var password:String)
data class SendRefresh(var refresh: String)
data class SendDevice(var device_uuid:String, var os_name:String, var os_version:String, var device_model:String, var app_version:String)
data class SendRefreshToken(var token: SendRefresh, var device: SendDevice)
data class SendLogin(var user: SendUser, var device: SendDevice)
data class SendConfirmResetPass(val new_password1: String, val new_password2: String, val uid: String, val token: String)
data class SendGoogleTokenId(val provider: String = "google-oauth2", val code: String)


data class ResultSimpleCountry(val id: Int?, val value: String?)
data class ResultListCountries(val countries: List<ResultSimpleCountry>?)

data class ResultSimpleGender(val id: Int?, val value: String?)
data class ResultListGenders(val genders: List<ResultSimpleGender>?)

data class ResultSimpleFavorites(val name: String?, val value: String?, val color: String?, val desc: String?)
data class ResultFavorites(val params: List<ResultSimpleFavorites>?)

data class ResultProfile(val first_name: String?, val last_name: String?, val email: String?, val image: String?)
data class ResultUserProfile(val birth_date: String?, val country: Int?, val gender: Int?, val height: Float?, val smoker: Boolean?, val measuring_system: Int?)
data class ResultPolicy( val policy: String? )
data class ResultExist( val exist: Boolean?)
data class ResultToken(val access: String?, val refresh: String?)
data class ResultTokenFromGoogle(val token: String?, val refresh: String?)
data class ResultRecommendation(val name: String?)

data class ResultMedCard(val weight:Float?,
                         val hip:Float?,
                         val waist:Float?,
                         val wrist:Float?,
                         val neck: Float?,
                         val heart_rate_alone:Int?,
                         val daily_activity_level: Float?,
                         val blood_pressure_sys:Int?,
                         val blood_pressure_dia:Int?,
                         val cholesterol:Float?,
                         val glucose:Float?)

data class ResultCommonRecommendation(val message_short: String?, val message_long: String?, val importance_level: String?)
data class ResultDiseaseRisk(val icd_id: Int?, val risk_string: String?, val message: String?, val risk_percents: String?, val recomendation: String?)
data class ResultAnalyze(val bmi: List<String>?,
                         val obesity_level: List<String>?,
                         val ideal_weight: Float?,
                         val base_metabolism: Int?,
                         val calories_to_low_weight: Int?,
                         val waist_to_hip_proportion: Float?,
                         val passport_age: Int?,
                         val common_risk_level: List<String>?,
                         val prognostic_age: Int?,
                         val fat_percent: List<String>?,
                         val body_type: String?,
                         val unfilled: String?,
                         val disease_risk: List<ResultDiseaseRisk>?,
                         val common_recomendations: List<ResultCommonRecommendation>?)