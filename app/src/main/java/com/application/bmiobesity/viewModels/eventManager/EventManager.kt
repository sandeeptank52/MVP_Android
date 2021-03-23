package com.application.bmiobesity.viewModels.eventManager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.model.retrofit.SendUser
import com.application.bmiobesity.utils.Event

class EventManager private constructor(): LoginViewModelEvent,
                                            SignInFragmentEvent,
                                            SignUpFragmentEvent,
                                            ForgotPassFragmentEvent,
                                            ResetPassFragmentEvent {
    // Sign In
    // View Model side
    private val mSignInSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mSignInRestorePass: MutableLiveData<Event<SendUser>> = MutableLiveData<Event<SendUser>>()
    private val mSignInShowErrorMessage: MutableLiveData<Event<RetrofitError>> = MutableLiveData<Event<RetrofitError>>()

    override fun signInSuccessEvent(e: Boolean) = mSignInSuccess.postValue(Event(e))
    override fun signInRestorePassEvent(e: SendUser) = mSignInRestorePass.postValue(Event(e))
    override fun signInShowErrorMessageEvent(e: RetrofitError) = mSignInShowErrorMessage.postValue(Event(e))

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

    override fun signUpCheckMailExistEvent(e: Boolean) = mSignUpCheckMailExist.postValue(Event(e))
    override fun signUpCheckMailErrorEvent(e: Boolean) = mSignUpCheckMailError.postValue(Event(e))
    override fun signUpSuccessEvent(e: Boolean) = mSignUpSuccess.postValue(Event(e))
    override fun signUpErrorEvent(e: RetrofitError) = mSignUpError.postValue(Event(e))

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

    override fun forgotPassSuccessEvent(e: Boolean) = mForgotPassSuccess.postValue(Event(e))
    override fun forgotPassErrorEvent(e: RetrofitError) = mForgotPassError.postValue(Event(e))

    // Fragment side
    private val forgotPassSuccess: LiveData<Event<Boolean>> = mForgotPassSuccess
    private val forgotPassError: LiveData<Event<RetrofitError>> = mForgotPassError

    override fun getForgotPassSuccessEvent(): LiveData<Event<Boolean>> = forgotPassSuccess
    override fun getForgotPassErrorEvent(): LiveData<Event<RetrofitError>> = forgotPassError

    // Reset password
    // View Model side
    private val mResetPassSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mResetPassError: MutableLiveData<Event<RetrofitError>> = MutableLiveData<Event<RetrofitError>>()

    override fun resetPassSuccessEvent(e: Boolean) = mResetPassSuccess.postValue(Event(e))
    override fun resetPassErrorEvent(e: RetrofitError) = mResetPassError.postValue(Event(e))

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