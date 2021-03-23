package com.application.bmiobesity.viewModels.eventManager

import androidx.lifecycle.LiveData
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.utils.Event

interface SignUpFragmentEvent {
    fun getSignUpCheckMailExistEvent(): LiveData<Event<Boolean>>
    fun getSignUpCheckMailErrorEvent(): LiveData<Event<Boolean>>
    fun getSignUpSuccessEvent(): LiveData<Event<Boolean>>
    fun getSignUpErrorEvent(): LiveData<Event<RetrofitError>>
}