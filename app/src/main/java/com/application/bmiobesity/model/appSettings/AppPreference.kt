package com.application.bmiobesity.model.appSettings

data class AppPreference(
        var firstTime: Boolean,
        var showDisclaimer: Boolean,
        var refreshToken: String,
        var accessToken: String,
        var deviceUUID: String
)