package com.application.bmiobesity.utils

import androidx.lifecycle.Observer

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getEventIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}