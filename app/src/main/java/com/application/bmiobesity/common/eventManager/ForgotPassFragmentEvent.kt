package com.application.bmiobesity.common.eventManager

import androidx.lifecycle.LiveData
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.common.Event

interface ForgotPassFragmentEvent {
    fun getForgotPassSuccessEvent(): LiveData<Event<Boolean>>
    fun getForgotPassErrorEvent(): LiveData<Event<RetrofitError>>
}