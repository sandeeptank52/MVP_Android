package com.application.bmiobesity.common.eventManagerMain

import androidx.lifecycle.LiveData
import com.application.bmiobesity.common.Event

interface MainActivityEvent {
    fun getPreloadSuccessEvent(): LiveData<Event<Boolean>>
}