package com.application.bmiobesity.viewModels.eventManager

import androidx.lifecycle.LiveData
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.model.retrofit.SendUser
import com.application.bmiobesity.utils.Event

interface SignInFragmentEvent {
    fun getSignInSuccessEvent(): LiveData<Event<Boolean>>
    fun getSignInRestorePassEvent(): LiveData<Event<SendUser>>
    fun getSignInShowErrorMessageEvent(): LiveData<Event<RetrofitError>>
}