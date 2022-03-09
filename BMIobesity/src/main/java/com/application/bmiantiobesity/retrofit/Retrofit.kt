package com.application.bmiantiobesity.retrofit

import android.content.Context
import com.application.bmiantiobesity.BuildConfig
import com.application.bmiantiobesity.interceptor.DebugInterceptorDB
import com.application.bmiantiobesity.models.DataTypeInTime
import com.application.bmiantiobesity.models.SetNewDataValue
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import kotlin.math.roundToInt

//Json Data Classes
//Login
data class User(var email:String, var password:String)
data class Device(var device_uuid:String, var os_name:String, var os_version:String, var device_model:String, var app_version:String)
data class Login(var user: User, var device: Device)
//Token
data class ResultToken(val access:String, val refresh:String)
data class ResultExist(val exist: Boolean)
//Reset
data class ConfirmReset(val new_password1:String, val new_password2: String, val uid:String, val token:String)
//Locale
data class Locale(val locale:String)
data class BodyCountries(val locale: String, var short_code:Boolean = false)
//Policy
data class Policy(val policy:String)
//Reset Password
data class SendEmail(val email: String)
data class SendRefresh(val token: Refresh, val device: Device)
data class Refresh(val refresh: String)
//Profile
data class Profile(var first_name:String, var last_name: String, val email: String, var image: String?)
data class UserProfile(var birth_date:String?, var country:Int?, var gender:Int?, var height:Float?, var smoker:Boolean?, var measuring_system: Int?) //, var first_name:String, var last_name: String, val email: String, var image: String?)
data class DataValues(val id:Int, val value:String)
data class Genders(val genders: List<DataValues>)
data class Countries(val countries: List<DataValues>)

//Dashboard
data class DashBoard(var gender:Int?, var birth_date:String?, var country:Int?, var height:Float?, var weight:Float?, var hip:Float?, var waist:Float?, var wrist:Float?, var neck: Float?, var heart_rate_alone:Int?,
                     var heart_rate_variability:Int?, var blood_pressure_sys:Int?, var blood_pressure_dia:Int?, var cholesterol:Float?, var glucose:Float?, var smoker:Boolean?, var locale: String?,
                     var measuring_system: Int?, var daily_activity_level: Float?){
    fun updateField(newValue: SetNewDataValue){
        when (newValue.type){
            DataTypeInTime.HEIGHT -> height = newValue.value.toFloatOrNull()
            DataTypeInTime.WEIGHT -> weight = newValue.value.toFloatOrNull()
            DataTypeInTime.HIP -> hip = newValue.value.toFloatOrNull()
            DataTypeInTime.WAIST -> waist = newValue.value.toFloatOrNull()
            DataTypeInTime.WRIST -> wrist = newValue.value.toFloatOrNull()
            DataTypeInTime.NECK -> neck = newValue.value.toFloatOrNull()
            DataTypeInTime.BLOOD_PRESSURE_SYS -> blood_pressure_sys = newValue.value.toFloat().roundToInt()
            DataTypeInTime.BLOOD_PRESSURE_DIA -> blood_pressure_dia = newValue.value.toFloat().roundToInt()
            DataTypeInTime.HEARTS_RATE_ALONE -> heart_rate_alone = newValue.value.toFloat().roundToInt() // ToDo добавить обработку отрицательных значений
            DataTypeInTime.HEARTS_RATE_VARIABILITY -> heart_rate_variability = newValue.value.toFloat().roundToInt()
            DataTypeInTime.GLUCOSE -> glucose = newValue.value.toFloatOrNull()
            DataTypeInTime.CHOLESTEROL -> cholesterol = newValue.value.toFloatOrNull()

            DataTypeInTime.SMOKER -> smoker = newValue.value.toBoolean()
            DataTypeInTime.COUNTRY -> country = newValue.value.toIntOrNull()
            DataTypeInTime.MEASURING_SYSTEM -> measuring_system = newValue.value.toIntOrNull()
            DataTypeInTime.DAILY_ACTIVITY_LEVEL -> daily_activity_level = newValue.value.toFloatOrNull()
            else -> return
        }
    }
}

//MedCard
data class MedCard( var weight:Float?, var hip:Float?, var waist:Float?, var wrist:Float?, var neck: Float?, var heart_rate_alone:Int?, var daily_activity_level: Float?,
                    var blood_pressure_sys:Int?, var blood_pressure_dia:Int?, var cholesterol:Float?, var glucose:Float?){
    fun updateField(newValue: SetNewDataValue){
        when (newValue.type){
            //DataTypeInTime.HEIGHT -> height = newValue.value.toFloatOrNull()
            DataTypeInTime.WEIGHT -> weight = newValue.value.toFloatOrNull()
            DataTypeInTime.HIP -> hip = newValue.value.toFloatOrNull()
            DataTypeInTime.WAIST -> waist = newValue.value.toFloatOrNull()
            DataTypeInTime.WRIST -> wrist = newValue.value.toFloatOrNull()
            DataTypeInTime.NECK -> neck = newValue.value.toFloatOrNull()
            DataTypeInTime.BLOOD_PRESSURE_SYS -> blood_pressure_sys = newValue.value.toFloat().roundToInt()
            DataTypeInTime.BLOOD_PRESSURE_DIA -> blood_pressure_dia = newValue.value.toFloat().roundToInt()
            DataTypeInTime.HEARTS_RATE_ALONE -> heart_rate_alone = newValue.value.toFloat().roundToInt() // ToDo добавить обработку отрицательных значений
            //DataTypeInTime.HEARTS_RATE_VARIABILITY -> heart_rate_variability = newValue.value.toFloat().roundToInt()
            DataTypeInTime.GLUCOSE -> glucose = newValue.value.toFloatOrNull()
            DataTypeInTime.CHOLESTEROL -> cholesterol = newValue.value.toFloatOrNull()

            //DataTypeInTime.SMOKER -> smoker = newValue.value.toBoolean()
            //DataTypeInTime.COUNTRY -> country = newValue.value.toIntOrNull()
            //DataTypeInTime.MEASURING_SYSTEM -> measuring_system = newValue.value.toIntOrNull()
            DataTypeInTime.DAILY_ACTIVITY_LEVEL -> daily_activity_level = newValue.value.toFloatOrNull()
            else -> return
        }
    }
}

//data class NewDashBoard(var newValue: String?):

//Resultat
data class Result(var bmi:List<String>?, var obesity_level:List<String>?, var ideal_weight:Float?, var base_metabolism:Int?, var calories_to_low_weight:Int?,
                  var waist_to_hip_proportion:Float?, var bio_age:Int?, var common_risk_level:List<String>?, var prognostic_age:Int?, var fat_percent:List<String>?,
                  var body_type:String?, var disease_risk:List<DiseaseRisk>?, var common_recomendations:MutableList<CommonRecomendations>?, var unfilled:String?)
data class DiseaseRisk(var icd_id:Int?, var risk_string:String?, var risk_percents: String?, var message:String?, var recomendation:String?)
data class CommonRecomendations(var message_short: String?, var message_long: String?, var importance_level:String?)

//Recomendations
data class Recomendation(var name:String?)

data class LoadData(val type: DataTypeInTime, val firstName: String, var contentFirst:String, val secondName:String, var contentSecond:String)
//data class RiskData(val risk:String, val description:String, val percent:Int)

//TimeStamp
data class GetFirstResultTime(var timestamp: String?)

interface InTimeDigitalApi {
    companion object{
        private const val WORK_API = "api/v2"
        //private const val WORK_API_NEW = "api/v2"
    }

    @POST("$WORK_API/isemailexist/")
    fun isEmailExist(@Body email: SendEmail): Observable<ResultExist>

    @POST("$WORK_API/signup/")
    fun signUp(@Body login: Login): Observable<ResultToken>

    @POST("$WORK_API/token/")
    fun getUserToken(@Body login: Login): Observable<ResultToken>

    @POST("$WORK_API/token/refresh/")
    fun refreshUserToken(@Body refresh: SendRefresh): Observable<ResultToken>

    @POST("$WORK_API/password/reset/")
    fun passwordReset(@Body email: SendEmail): Observable<ResultExist>

    @POST("$WORK_API/password/reset/confirm/")
    fun passwordResetConfirm(@Body confirmReset: ConfirmReset): Observable<ResultExist>

    @POST("$WORK_API/policy/")
    fun getPolicy(@Body locale: Locale): Observable<Policy>

    @GET("$WORK_API/profile/")
    fun getProfile(@Header("Authorization") access: String): Observable<Profile> //

    @PATCH("$WORK_API/profile/")
    fun setProfile(@Header("Authorization") access: String, @Body profile: Profile): Observable<Profile>

    @GET("$WORK_API/user_profile/")
    fun getUserProfile(@Header("Authorization") access: String): Observable<UserProfile> //

    @PATCH("$WORK_API/user_profile/")
    fun setUserProfile(@Header("Authorization") access: String, @Body userProfile: UserProfile): Observable<UserProfile>

    @GET("$WORK_API/dashboard/")
    fun getDashBoard(@Header("Authorization") access: String): Observable<DashBoard>

    @PUT("$WORK_API/dashboard/")
    fun setDashBoard(@Header("Authorization") access: String, @Body dashBoard: DashBoard): Observable<DashBoard>

    @PATCH("$WORK_API/dashboard/")
    fun updateDashBoard(@Header("Authorization") access: String, @Body dashBoard: DashBoard): Observable<DashBoard>

    @GET("$WORK_API/med_card/")
    fun getMedCard(@Header("Authorization") access: String): Observable<MedCard>

    @PUT("$WORK_API/med_card/")
    fun setMedCard(@Header("Authorization") access: String, @Body medCard: MedCard): Observable<MedCard>

    @PATCH("$WORK_API/med_card/")
    fun updateMedCard(@Header("Authorization") access: String, @Body medCard: MedCard): Observable<MedCard>

    @GET("$WORK_API/result/")
    fun getResult(@Header("Authorization") access: String, @Header("Accept-Language") locale: String): Observable<Result>

    @GET("$WORK_API/recomendations/")
    fun getRecomedations(@Header("Authorization") access: String, @Header("Accept-Language") locale: String): Observable<List<Recomendation>>

    @GET("$WORK_API/getfirstresulttime/")
    fun getFirstResultTime(@Header("Authorization") access: String): Observable<GetFirstResultTime>

    @POST("$WORK_API/genders/")
    fun getGenders(@Body locale: Locale): Observable<Genders>

    @POST("$WORK_API/countries/")
    fun getCountries(@Body bodyCountries: BodyCountries): Observable<Countries>

}


object MyRetrofitQuery{
    private val interceptor = HttpLoggingInterceptor()

    init {
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    //Json Login
    fun getInstanceInTimeJson(baseUrl:String): InTimeDigitalApi {

        //val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        // Для тестировщиков
        val  client = if (BuildConfig.DEBUG) OkHttpClient.Builder().addInterceptor(DebugInterceptorDB()).addInterceptor(interceptor).build() //addInterceptor(DebugWriteFileInterceptor(context))
            else OkHttpClient.Builder().addInterceptor(interceptor).build()


        val myRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        return myRetrofit.create(InTimeDigitalApi::class.java)
    }

}