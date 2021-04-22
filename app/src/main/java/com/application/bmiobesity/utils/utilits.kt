package com.application.bmiobesity.utils

import com.application.bmiobesity.model.retrofit.CurrentLocale
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentLocale(): CurrentLocale{
    return CurrentLocale(Locale.getDefault().language)
}

fun getFormatData(pattern: String): String{
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
}