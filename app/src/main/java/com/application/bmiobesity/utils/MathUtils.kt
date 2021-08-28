package com.application.bmiobesity.utils

import kotlin.math.acos
import kotlin.math.sqrt


fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    return sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2))
}

fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Float {
    val ux = x2 - x1
    val uy = y2 - y1
    val vx = x2 - x3
    val vy = y2 - y3
    val cos = (ux * vx + uy * vy) / (sqrt(ux * ux + uy * uy) * sqrt(vx * vx + vy * vy))
    return Math.toDegrees(acos(cos.toDouble())).toFloat()
}
