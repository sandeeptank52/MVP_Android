package com.application.bmiobesity.common.eventManager

import androidx.lifecycle.LiveData
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.common.Event

interface ResetPassFragmentEvent {
    fun getResetPassSuccessEvent(): LiveData<Event<Boolean>>
    fun getResetPassErrorEvent(): LiveData<Event<RetrofitError>>
}