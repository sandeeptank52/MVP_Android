package com.application.bmiobesity.viewModels

import android.net.Uri
import android.os.Build
import android.util.Patterns
import androidx.lifecycle.*
import com.application.bmiobesity.BuildConfig
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.model.appSettings.AppPreference
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.model.db.commonSettings.CommonSettingRepo
import com.application.bmiobesity.model.db.commonSettings.entities.Policy
import com.application.bmiobesity.model.retrofit.*
import com.application.bmiobesity.viewModels.eventManager.EventManager
import com.application.bmiobesity.viewModels.eventManager.LoginViewModelEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

class LoginViewModel : ViewModel() {
    @Inject
    lateinit var remoteRepo: RemoteRepo
    @Inject
    lateinit var appSetting: AppSettingDataStore
    @Inject
    lateinit var commonSettingRepo: CommonSettingRepo

    private lateinit var appPreference: AppPreference
    private lateinit var policy: List<Policy>
    private val eventManager: LoginViewModelEvent

    init {
        InTimeApp.appComponent.inject(this)
        viewModelScope.launch(Dispatchers.IO) {
            policy = commonSettingRepo.getAllPolicy()
        }
        eventManager = EventManager.getEventManager()
    }

    // Sign In fragment
    fun signInAction(mail: String, pass: String, rememberPass: Boolean){

        val user = SendUser(mail, pass)
        val device = getDevice()
        val login = SendLogin(user, device)
        
        viewModelScope.launch {
            when(val resultToken = remoteRepo.getToken(login)){
                is RetrofitResult.Success -> {
                    signInSuccess(resultToken.value, user, rememberPass)
                    eventManager.signInSuccessEvent(true)
                }
                is RetrofitResult.Error -> {
                    eventManager.signInShowErrorMessageEvent(errorCheck(resultToken.code, resultToken.errorMessage, null))
                }
            }
        }
    }
    fun signInActionWithGoogle(mail: String, code: String, rememberPass: Boolean, firstName: String?, lastName: String?, photoUri: Uri?){
        viewModelScope.launch {
            when(val result = remoteRepo.getTokenFromGoogle(SendGoogleTokenId(code = code))){
                is RetrofitResult.Success -> {
                    signInSuccessFromGoogle(result.value.token, result.value.refresh, mail)
                    eventManager.signInSuccessEvent(true)
                }
                is RetrofitResult.Error -> {
                    eventManager.signInShowErrorMessageEvent(errorCheck(result.code, result.errorMessage, null))
                }
            }
        }
    }
    fun checkSavedPass(){
        viewModelScope.launch(Dispatchers.IO) {
            val res: SendUser = SendUser("", "")
            val mailDeferred = async { appSetting.getStringParam(AppSettingDataStore.PrefKeys.USER_MAIL).first() }
            val passDeferred = async { appSetting.getStringParam(AppSettingDataStore.PrefKeys.USER_PASS).first() }
            res.email = mailDeferred.await()
            res.password = passDeferred.await()
            if(res.password.isNotEmpty()) eventManager.signInRestorePassEvent(res)
        }
    }

    // Sign Up fragment
    fun signUpAction(mail: String, pass: String){
        val user = SendUser(mail, pass)
        val device = getDevice()
        val login = SendLogin(user, device)

        viewModelScope.launch {
            when (val resultToken = remoteRepo.signUp(login)){
                is RetrofitResult.Success -> {
                    signInSuccess(resultToken.value, user, false)
                    eventManager.signUpSuccessEvent(true)
                }
                is RetrofitResult.Error -> {
                    eventManager.signUpErrorEvent(errorCheck(resultToken.code, resultToken.errorMessage, null))
                }
            }
        }
    }
    fun checkExistMail(mail: String){
        viewModelScope.launch {
            when (val existEmail = remoteRepo.isMailExist(SendEmail(mail))){
                is RetrofitResult.Success -> {
                    eventManager.signUpCheckMailExistEvent(!(existEmail.value.exist ?: true))
                }
                is RetrofitResult.Error -> {
                    eventManager.signUpCheckMailErrorEvent(true)
                }
            }
        }
    }

    // Reset password
    fun forgotPassAction(mail: String){
        viewModelScope.launch {
            when (val resetConfirm = remoteRepo.passwordReset(SendEmail(mail))){
                is RetrofitResult.Success -> {
                    if (resetConfirm.value == "OK") eventManager.forgotPassSuccessEvent(true) else eventManager.forgotPassErrorEvent(RetrofitError.UNKNOWN_ERROR)
                }
                is RetrofitResult.Error -> {
                    eventManager.forgotPassErrorEvent(errorCheck(resetConfirm.code, resetConfirm.errorMessage, null))
                }
            }
        }
    }
    fun resetPassAction(sendConfirm: SendConfirmResetPass){
        viewModelScope.launch {
            when (val passResetConfirm = remoteRepo.passwordResetConfirm(sendConfirm)){
                is RetrofitResult.Success -> {
                    eventManager.resetPassSuccessEvent(true)
                }
                is RetrofitResult.Error -> {
                    eventManager.resetPassErrorEvent(errorCheck(passResetConfirm.code, passResetConfirm.errorMessage, null))
                }
            }
        }
    }

    // Common
    private suspend fun signInSuccessFromGoogle(accessToken: String?, refreshToken: String?, mail: String){
        appSetting.setStringParam(AppSettingDataStore.PrefKeys.ACCESS_TOKEN, accessToken ?: "")
        appSetting.setStringParam(AppSettingDataStore.PrefKeys.REFRESH_TOKEN, refreshToken ?: "")
        appSetting.setStringParam(AppSettingDataStore.PrefKeys.USER_MAIL, mail)
        updateAppPreference()
    }
    private suspend fun signInSuccess(resToken: ResultToken, user: SendUser, rememberPass: Boolean){
        appSetting.setStringParam(AppSettingDataStore.PrefKeys.ACCESS_TOKEN, resToken.access ?: "")
        appSetting.setStringParam(AppSettingDataStore.PrefKeys.REFRESH_TOKEN, resToken.refresh ?: "")
        appSetting.setStringParam(AppSettingDataStore.PrefKeys.USER_MAIL, user.email)
        if (rememberPass) {
            appSetting.setStringParam(AppSettingDataStore.PrefKeys.USER_PASS, user.password)
        } else {
            appSetting.setStringParam(AppSettingDataStore.PrefKeys.USER_PASS, "")
        }
        updateAppPreference()
    }
    private fun getDevice() = SendDevice(
        appPreference.deviceUUID,
        AppSettingDataStore.Constants.OS_NAME,
        Build.VERSION.RELEASE,
        "${Build.BRAND} - ${Build.MODEL}",
        BuildConfig.VERSION_NAME
    )
    fun getPolicy(): String{
        return policy[0].value
    }
    fun updateAppPreference() = viewModelScope.launch(Dispatchers.IO) { appPreference = appSetting.getAppPreference().first() }

    // Functions for check fields
    fun checkEmail(mail: CharSequence) = Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    fun checkPassLength(pass: CharSequence) = pass.length >= 8
    fun checkPassNumber(pass: CharSequence) = pass.contains("[0-9]".toRegex())
    fun checkPassSymbol(pass: CharSequence) = pass.contains("[!№;%:?()_+/*-.,|`~@#^&<>'{}$]".toRegex())
    fun checkPassConfirm(pass: CharSequence, passConfirm: CharSequence) = pass == passConfirm
    fun checkPassNotEmpty(pass: CharSequence) = pass.isNotEmpty()

    override fun onCleared() {
        super.onCleared()
    }
}