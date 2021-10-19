package com.application.bmiobesity.model.retrofit

import androidx.annotation.Keep
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

@Keep data class CurrentLocale(val locale: String )

@Keep data class SendEmail(val email: String)
@Keep data class SendUser(var email:String, var password:String)
@Keep data class SendRefresh(var refresh: String)
@Keep data class SendDevice(var device_uuid:String, var os_name:String, var os_version:String, var device_model:String, var app_version:String)
@Keep data class SendRefreshToken(var token: SendRefresh, var device: SendDevice)
@Keep data class SendLogin(var user: SendUser, var device: SendDevice)
@Keep data class SendConfirmResetPass(var new_password1: String, var new_password2: String, var uid: String, var token: String)
@Keep data class SendGoogleTokenId(var provider: String = "google-oauth2", var code: String)
@Keep data class SendProfile(var first_name: String, var last_name: String, var email: String, var birth_date: String?, var height: Float?, var smoker: Boolean,  var country: Int?, var measuring_system: Int)
@Keep data class SendUserProfile(var birth_date: String?, var country: Int?, var gender: Int?, var height: Float?, var smoker: Boolean, var measuring_system: Int)

@Keep data class ResultSimpleCountry(val id: Int?, val value: String?)
@Keep data class ResultListCountries(val countries: List<ResultSimpleCountry>?)

@Keep data class ResultSimpleGender(val id: Int?, val value: String?)
@Keep data class ResultListGenders(val genders: List<ResultSimpleGender>?)

@Keep data class ResultSimpleFavorites(val name: String?, val value: String?, val color: String?, val desc: String?)
@Keep data class ResultFavorites(val params: List<ResultSimpleFavorites>?)

@Keep data class ResultProfile(val first_name: String?, val last_name: String?, val email: String?, val image: String?)
@Keep data class ResultUserProfile(val birth_date: String?, val country: Int?, val gender: Int?, val height: Float?, val smoker: Boolean?, val measuring_system: Int?)
@Keep data class ResultPolicy( val policy: String? )
@Keep data class ResultExist( val exist: Boolean?)
@Keep data class ResultToken(val access: String?, val refresh: String?)
@Keep data class ResultTokenFromGoogle(val token: String?, val refresh: String?)
@Keep data class ResultRecommendation(val name: String?)
@Keep data class ResultFirstTimeStamp(val timestamp: String?)
@Keep data class ResultDeleteUser(val delete: String?)

@Keep data class ResultMedCard(val weight:Float?,
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

@Keep data class ResultCommonRecommendation(val message_short: String?, val message_long: String?, val importance_level: String?)
@Keep data class ResultDiseaseRisk(val icd_id: Int?, val risk_string: String?, val message: String?, val risk_percents: String?, val recomendation: String?)
@Keep data class ResultAnalyze(val bmi: List<String>?,
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

@Keep data class UpdateResultDashBoard(
                     var gender:Int?,
                     var birth_date:String?,
                     var country:Int?,
                     var height:Float?,
                     var weight:Float?,
                     var hip:Float?,
                     var waist:Float?,
                     var wrist:Float?,
                     var heart_rate_alone:Int?,
                     var blood_pressure_sys:Int?,
                     var blood_pressure_dia:Int?,
                     var cholesterol:Float?,
                     var glucose:Float?,
                     var smoker:Boolean?,
                     var locale: String?,
                     var neck: Float?,
                     var daily_activity_level: Float?,
                     var measuring_system: Int?)

@Keep data class UpdateResultAvatar(var image: String?)