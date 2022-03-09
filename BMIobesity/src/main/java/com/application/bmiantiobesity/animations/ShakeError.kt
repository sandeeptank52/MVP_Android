package com.application.bmiantiobesity.animations

import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation

object ShakeError: TranslateAnimation(0f,15f,0f,0f){

    init {
        this.duration = 500
        this.interpolator = CycleInterpolator(3f)
    }
}