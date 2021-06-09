package com.application.bmiobesity.common.eventManagerMain

import androidx.lifecycle.LiveData
import com.application.bmiobesity.common.Event

interface MainSettingEvent {
    fun getStartUserDeleting(): LiveData<Event<Boolean>>
    fun getEndUserDeleting(): LiveData<Event<Boolean>>
}