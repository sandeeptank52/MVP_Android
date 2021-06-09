package com.application.bmiobesity.common.eventManagerMain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.bmiobesity.common.Event

class EventManagerMain private constructor() : MainViewModelEvent,
                                                MainActivityEvent,
                                                MainSettingEvent{

    // Main activity
    // View Model side
    private val mPreloadSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()

    override fun preloadSuccessEvent(e: Boolean) = mPreloadSuccess.postValue(Event(e))

    // Activity side
    private val preloadSuccess: LiveData<Event<Boolean>> = mPreloadSuccess

    override fun getPreloadSuccessEvent(): LiveData<Event<Boolean>> = preloadSuccess

    // Setting fragment
    // View Model side
    private val mStartDeletingUser: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val mEndDeletingUser: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()

    override fun startUserDeleting(e: Boolean) = mStartDeletingUser.postValue(Event(e))
    override fun endUserDeleting(e: Boolean) = mEndDeletingUser.postValue(Event(e))

    // fragment Side
    private val startDeletingUser: LiveData<Event<Boolean>> = mStartDeletingUser
    private val endDeletingUser: LiveData<Event<Boolean>> = mEndDeletingUser

    override fun getStartUserDeleting() = startDeletingUser
    override fun getEndUserDeleting() = endDeletingUser

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