package com.application.bmiobesity.model.retrofit

import java.lang.Exception

sealed class RetrofitResult<out T: Any>{
    data class Success <out T: Any> (val value: T, val code: Int, val message: String): RetrofitResult<T>()
    data class Error(val errorMessage: String, val code: Int) : RetrofitResult<Nothing>()
}
suspend fun <T: Any> safeApiCall(call: suspend () -> RetrofitResult<T>): RetrofitResult<T> =
try {
    call.invoke()
} catch (e: Exception){
    RetrofitResult.Error(e.message ?: "exception", 0)
}
fun errorCheck(code: Int, message: String): RetrofitError{
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
data class SendDevice(var device_uuid:String, var os_name:String, var os_version:String, var device_model:String, var app_version:String)
data class SendLogin(var user: SendUser, var device: SendDevice)
data class SendConfirmResetPass(val new_password1: String, val new_password2: String, val uid: String, val token: String)
data class SendGoogleTokenId(val provider: String = "google-oauth2", val code: String)


data class ResultSimpleCountry(val id: Int?, val value: String?)
data class ResultListCountries(val countries: List<ResultSimpleCountry>?)

data class ResultSimpleGender(val id: Int?, val value: String?)
data class ResultListGenders(val genders: List<ResultSimpleGender>?)

data class ResultProfile(val first_name: String?, val last_name: String?, val email: String?, val image: String?)
data class ResultPolicy( val policy: String? )
data class ResultExist( val exist: Boolean?)
data class ResultToken(val access: String?, val refresh: String?)
data class ResultTokenFromGoogle(val token: String?, val refresh: String?)

data class ResultMedCard(var weight:Float?,
                         var hip:Float?,
                         var waist:Float?,
                         var wrist:Float?,
                         var neck: Float?,
                         var heart_rate_alone:Int?,
                         var daily_activity_level: Float?,
                         var blood_pressure_sys:Int?,
                         var blood_pressure_dia:Int?,
                         var cholesterol:Float?,
                         var glucose:Float?)