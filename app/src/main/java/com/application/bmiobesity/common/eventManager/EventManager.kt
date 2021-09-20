package com.application.bmiobesity.common.eventManager

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.analytics.AnalyticsEvent
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.model.retrofit.SendUser
import com.application.bmiobesity.common.Event
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import com.application.bmiobesity.analytics.EventParam
import com.application.bmiobesity.analytics.EventValue
import com.google.firebase.analytics.FirebaseAnalytics.Event as FbEvent
import com.google.firebase.analytics.FirebaseAnalytics.Param

class EventManager private constructor(): LoginViewModelEvent,
                                            SignInFragmentEvent,
                                            SignUpFragmentEvent,
                                            ForgotPassFragmentEvent,
                                            ResetPassFragmentEvent {
    @Inject
    lateinit var analytics: FirebaseAnalytics
    // Sign In
    // View Model side
    private val mSignInSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mSignInRestorePass: MutableLiveData<Event<SendUser>> = MutableLiveData<Event<SendUser>>()
    private val mSignInShowErrorMessage: MutableLiveData<Event<RetrofitError>> = MutableLiveData<Event<RetrofitError>>()

    init {
        InTimeApp.appComponent.inject(this)
    }
    override fun signInSuccessEvent(e: Boolean) = run {
        mSignInSuccess.postValue(Event(e))
        val bundle = Bundle()
        bundle.putBoolean(EventParam.LOGIN_SUCCESS, e)
        analytics.logEvent(AnalyticsEvent.LOGIN, bundle)
    }
    override fun signInRestorePassEvent(e: SendUser) =  run {
        mSignInRestorePass.postValue(Event(e))
        val bundle = Bundle()
        bundle.putString(EventParam.USER, e.email)
        analytics.logEvent(AnalyticsEvent.RESTORE, bundle)
    }
    override fun signInShowErrorMessageEvent(e: RetrofitError) =
        run {
            mSignInShowErrorMessage.postValue(Event(e))
            val bundle = Bundle()
            bundle.putBoolean(EventParam.LOGIN_SUCCESS, false)
            bundle.putString(EventParam.LOGIN_ERROR, e.name)
            analytics.logEvent(AnalyticsEvent.LOGIN, bundle)
        }


    // Fragment side
    private val signInSuccess: LiveData<Event<Boolean>> = mSignInSuccess
    private val signInRestorePass: LiveData<Event<SendUser>> = mSignInRestorePass
    private val signInShowErrorMessage: LiveData<Event<RetrofitError>> = mSignInShowErrorMessage

    override fun getSignInSuccessEvent(): LiveData<Event<Boolean>> = signInSuccess
    override fun getSignInRestorePassEvent(): LiveData<Event<SendUser>> = signInRestorePass
    override fun getSignInShowErrorMessageEvent(): LiveData<Event<RetrofitError>> = signInShowErrorMessage


    // Sign Up
    // View Model side
    private val mSignUpCheckMailExist: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mSignUpCheckMailError: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mSignUpSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mSignUpError: MutableLiveData<Event<RetrofitError>> = MutableLiveData<Event<RetrofitError>>()

    override fun signUpCheckMailExistEvent(e: Boolean) =
        run {
            mSignUpCheckMailExist.postValue(Event(e))
            val bundle = Bundle()
            bundle.putBoolean(EventParam.REGISTER_SUCCESS, false)
            bundle.putString(EventParam.REGISTER_ERROR, EventValue.REGISTER_EMAIL_ERROR)
            analytics.logEvent(AnalyticsEvent.REGISTER, bundle)
        }
    override fun signUpCheckMailErrorEvent(e: Boolean) =  run {
        mSignUpCheckMailError.postValue(Event(e))
        val bundle = Bundle()
        bundle.putBoolean(EventParam.REGISTER_SUCCESS, false)
        bundle.putString(EventParam.REGISTER_ERROR, EventValue.CHECK_EMAIL_ERROR)
        analytics.logEvent(AnalyticsEvent.REGISTER, bundle)
    }
    override fun signUpSuccessEvent(e: Boolean) = run {
        mSignUpSuccess.postValue(Event(e))
        val bundle = Bundle()
        bundle.putBoolean(EventParam.REGISTER_SUCCESS, e)
        analytics.logEvent(AnalyticsEvent.REGISTER, bundle)
    }
    override fun signUpErrorEvent(e: RetrofitError) = run {
            mSignUpError.postValue(Event(e))
            val bundle = Bundle()
            bundle.putBoolean(EventParam.REGISTER_SUCCESS, false)
            bundle.putString(EventParam.REGISTER_ERROR, e.name)
            analytics.logEvent(AnalyticsEvent.REGISTER, bundle)
        }

    // Fragment side
    private val signUpCheckMailExist: LiveData<Event<Boolean>> = mSignUpCheckMailExist
    private val signUpCheckMailError: LiveData<Event<Boolean>> = mSignUpCheckMailError
    private val signUpSuccess: LiveData<Event<Boolean>> = mSignUpSuccess
    private val signUpError: LiveData<Event<RetrofitError>> = mSignUpError

    override fun getSignUpCheckMailExistEvent(): LiveData<Event<Boolean>> = signUpCheckMailExist
    override fun getSignUpCheckMailErrorEvent(): LiveData<Event<Boolean>> = signUpCheckMailError
    override fun getSignUpSuccessEvent(): LiveData<Event<Boolean>> = signUpSuccess
    override fun getSignUpErrorEvent(): LiveData<Event<RetrofitError>> = signUpError


    // Forgot password
    // View Model side
    private val mForgotPassSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mForgotPassError: MutableLiveData<Event<RetrofitError>> = MutableLiveData<Event<RetrofitError>>()

    override fun forgotPassSuccessEvent(e: Boolean) =  run {
        mForgotPassSuccess.postValue(Event(e))
        val bundle = Bundle()
        bundle.putBoolean(EventParam.FORGOT_PASSWORD_SUCCESS, e)
        analytics.logEvent(AnalyticsEvent.FORGOT_PASSWORD, bundle)
    }
    override fun forgotPassErrorEvent(e: RetrofitError) =  run {
        mForgotPassError.postValue(Event(e))
        val bundle = Bundle()
        bundle.putBoolean(EventParam.FORGOT_PASSWORD_SUCCESS, false)
        bundle.putString(EventParam.FORGOT_PASSWORD_ERROR, e.name)
        analytics.logEvent(AnalyticsEvent.FORGOT_PASSWORD, bundle)
    }

    // Fragment side
    private val forgotPassSuccess: LiveData<Event<Boolean>> = mForgotPassSuccess
    private val forgotPassError: LiveData<Event<RetrofitError>> = mForgotPassError

    override fun getForgotPassSuccessEvent(): LiveData<Event<Boolean>> = forgotPassSuccess
    override fun getForgotPassErrorEvent(): LiveData<Event<RetrofitError>> = forgotPassError

    // Reset password
    // View Model side
    private val mResetPassSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mResetPassError: MutableLiveData<Event<RetrofitError>> = MutableLiveData<Event<RetrofitError>>()

    override fun resetPassSuccessEvent(e: Boolean) = run {
        mResetPassSuccess.postValue(Event(e))
        val bundle = Bundle()
        bundle.putBoolean(EventParam.RESET_PASSWORD_SUCCESS, e)
        analytics.logEvent(AnalyticsEvent.RESET_PASSWORD, bundle)
    }
    override fun resetPassErrorEvent(e: RetrofitError) = run {
        mResetPassError.postValue(Event(e))
        val bundle = Bundle()
        bundle.putBoolean(EventParam.RESET_PASSWORD_SUCCESS, false)
        bundle.putString(EventParam.RESET_PASSWORD_ERROR, e.name)
        analytics.logEvent(AnalyticsEvent.RESET_PASSWORD, bundle)
    }

    // Fragment side
    private val resetPassSuccess: LiveData<Event<Boolean>> = mResetPassSuccess
    private val resetPassError: LiveData<Event<RetrofitError>> = mResetPassError

    override fun getResetPassSuccessEvent(): LiveData<Event<Boolean>> = resetPassSuccess
    override fun getResetPassErrorEvent(): LiveData<Event<RetrofitError>> = resetPassError

    companion object{
        @Volatile
        private var INSTANCE: EventManager? = null

        fun getEventManager(): EventManager{
            return INSTANCE ?: synchronized(this){
                val instance = EventManager()
                INSTANCE = instance
                instance
            }
        }
    }
}

