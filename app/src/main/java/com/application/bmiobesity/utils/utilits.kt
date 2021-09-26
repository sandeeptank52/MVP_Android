package com.application.bmiobesity.utils

import com.application.bmiobesity.model.retrofit.CurrentLocale
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentLocale(): CurrentLocale{
    return CurrentLocale(Locale.getDefault().language)
}

fun getFormatData(pattern: String): String{
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
}

fun getDateStrFromMS(ms: Long): String{
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(ms))
}
fun getTimeStrFromMS(ms: Long): String{
    return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(ms))
}

fun convertDateStringToMs(date: String): Long {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    if (date.isNotEmpty()) {
        val splitDate = date.split(".")
        if (splitDate.size == 3) {
            val day = splitDate[0].toInt()
            val month = splitDate[1].toInt() - 1
            val year = splitDate[2].toInt()
            calendar.set(year, month, day)
            return calendar.timeInMillis
        }
    }
    return calendar.timeInMillis
}

fun convertDateLongToString(date: Long): String {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    calendar.timeInMillis = date
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val dayStr = if (day > 9) day.toString() else "0${day}"
    val month = (calendar.get(Calendar.MONTH) + 1)
    val mothStr = if (month > 9) month.toString() else "0${month}"
    val year = calendar.get(Calendar.YEAR).toString()
    return "${dayStr}.${mothStr}.${year}"
}

fun numberToWithoutDigit(n: Float): Float{
    val decFormat = DecimalFormat("#")
    decFormat.decimalFormatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
    return decFormat.format(n).toFloat()
}
fun numberToOneDigit(n: Float): Float{
    val decFormat = DecimalFormat("#.#")
    decFormat.decimalFormatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
    return decFormat.format(n).toFloat()
}

fun getFirstNumber(n: Float): Int{
    val split = n.toString().split(".")
    return split[0].toInt()
}
fun getSecondNumber(n: Float): Int{
    val split = n.toString().split(".")
    return split[1].toInt()
}
fun getFloatFromTwoInt(first: Int, second: Int): Float{
    val str = "${first}.${second}"
    return str.toFloat()
}

class Duration8601(){
    companion object{
        const val SECONDS_PER_MINUTES = 60L
        const val MINUTES_PER_HOURS = 60L
        const val HOURS_PER_DAY = 24L
        const val DAYS_PER_WEEK = 7L

        const val SECONDS_PER_HOURS = SECONDS_PER_MINUTES * MINUTES_PER_HOURS
        const val SECONDS_PER_DAY = SECONDS_PER_HOURS * HOURS_PER_DAY
        const val SECONDS_PER_WEEK = SECONDS_PER_DAY * DAYS_PER_WEEK
    }
}