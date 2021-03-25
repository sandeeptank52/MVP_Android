package com.application.bmiobesity.viewModels.eventManagerMain

import androidx.lifecycle.LiveData
import com.application.bmiobesity.utils.Event

interface MainActivityEvent {
    fun getPreloadSuccessEvent(): LiveData<Event<Boolean>>
}