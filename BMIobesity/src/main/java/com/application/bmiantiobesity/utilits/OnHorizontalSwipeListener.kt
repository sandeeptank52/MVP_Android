package com.application.bmiantiobesity.utilits

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

// Определитель свайп эффекта
abstract class OnHorizontalSwipeListener(val context: Context) : View.OnTouchListener{
    companion object{
        const val SWIPE_MIN = 50
        const val SWIPE_VELOCITY_MIN = 100
    }

    private val detector = GestureDetectorCompat(context, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent?) = detector.onTouchEvent(event)

    abstract fun onRightSwipe()

    abstract fun onLeftSwipe()

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?) = true

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            try{
                if ((e1 == null) or (e2 == null)) return false
                val deltaY = e2!!.y - e1!!.y
                val deltaX = e2.x - e1.x

                if (abs(deltaX) < abs(deltaY)) return false

                if (abs(deltaX) < SWIPE_MIN && abs(velocityX) < SWIPE_VELOCITY_MIN) return false

                if (deltaX > 0) onRightSwipe() else onLeftSwipe()

                return true
            }catch (ex:Exception){
                return false
            }
        }
    }

}