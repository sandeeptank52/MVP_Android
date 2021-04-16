package com.application.bmiobesity.common.eventManagerMain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.bmiobesity.common.Event

class EventManagerMain private constructor() : MainViewModelEvent,
                                                MainActivityEvent{

    // Main activity
    // View Model side
    private val mPreloadSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()

    override fun preloadSuccessEvent(e: Boolean) = mPreloadSuccess.postValue(Event(e))

    // Activity side
    private val preloadSuccess: LiveData<Event<Boolean>> = mPreloadSuccess

    override fun getPreloadSuccessEvent(): LiveData<Event<Boolean>> = preloadSuccess



    companion object{
        @Volatile
        private var INSTANCE: EventManagerMain? = null

        fun getEventManager(): EventManagerMain {
            return INSTANCE ?: synchronized(this){
                val instance = EventManagerMain()
                INSTANCE = instance
                instance
            }
        }
    }
}