package com.application.bmiobesity.viewModels.eventManager

import androidx.lifecycle.LiveData
import com.application.bmiobesity.model.retrofit.RetrofitError
import com.application.bmiobesity.utils.Event

interface ForgotPassFragmentEvent {
    fun getForgotPassSuccessEvent(): LiveData<Event<Boolean>>
    fun getForgotPassErrorEvent(): LiveData<Event<RetrofitError>>
}