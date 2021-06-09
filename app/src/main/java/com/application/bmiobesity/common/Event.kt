package com.application.bmiobesity.common

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getEventIfNotHandled(): T?{
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}