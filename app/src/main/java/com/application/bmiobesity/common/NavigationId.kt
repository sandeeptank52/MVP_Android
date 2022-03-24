package com.application.bmiobesity.common

import com.application.bmiobesity.R

enum class NavigationId(val id: Int) {
    SETTING_FRAGMENT(R.id.mainSettingNav),
    PROFILE_FRAGMENT(R.id.mainProfileDialogNav),
    SUBSCRIPTION_FRAGMENT(R.id.mainSubsNav),
    REPORT_FRAGMENT(R.id.mainReportNav),
    SERVICES_FRAGMENT(R.id.mainServicesNav),
    DEFAULT(0)
}