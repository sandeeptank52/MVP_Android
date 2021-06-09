package com.application.bmiobesity.common.eventManagerMain

interface MainViewModelEvent {
    // Main activity
    fun preloadSuccessEvent(e: Boolean)

    // Setting fragment
    fun startUserDeleting(e: Boolean)
    fun endUserDeleting(e: Boolean)
}