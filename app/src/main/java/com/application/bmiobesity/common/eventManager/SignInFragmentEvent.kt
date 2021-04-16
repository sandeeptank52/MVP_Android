package com.application.bmiobesity.common.eventManager

import androidx.lifecycle.LiveData
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.model.retrofit.SendUser
import com.application.bmiobesity.common.Event

interface SignInFragmentEvent {
    fun getSignInSuccessEvent(): LiveData<Event<Boolean>>
    fun getSignInRestorePassEvent(): LiveData<Event<SendUser>>
    fun getSignInShowErrorMessageEvent(): LiveData<Event<RetrofitError>>
}