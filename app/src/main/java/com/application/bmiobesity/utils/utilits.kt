package com.application.bmiobesity.utils

import com.application.bmiobesity.model.retrofit.CurrentLocale
import java.util.*

fun getCurrentLocale(): CurrentLocale{
    return CurrentLocale(Locale.getDefault().language)
}