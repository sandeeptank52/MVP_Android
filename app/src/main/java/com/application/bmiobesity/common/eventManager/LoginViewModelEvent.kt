package com.application.bmiobesity.common.eventManager

import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.model.retrofit.SendUser

interface LoginViewModelEvent {
    // Sign In
    fun signInSuccessEvent(e: Boolean)
    fun signInRestorePassEvent(e: SendUser)
    fun signInShowErrorMessageEvent(e: RetrofitError)

    // Sign Up
    fun signUpCheckMailExistEvent(e: Boolean)
    fun signUpCheckMailErrorEvent(e: Boolean)
    fun signUpSuccessEvent(e: Boolean)
    fun signUpErrorEvent(e: RetrofitError)

    // Forgot Pass
    fun forgotPassSuccessEvent(e: Boolean)
    fun forgotPassErrorEvent(e: RetrofitError)

    // Reset Pass
    fun resetPassSuccessEvent(e: Boolean)
    fun resetPassErrorEvent(e: RetrofitError)
}