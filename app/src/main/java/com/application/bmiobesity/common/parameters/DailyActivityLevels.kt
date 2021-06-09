package com.application.bmiobesity.common.parameters

enum class DailyActivityLevels(val id: Float, val nameRes: String, val descriptionRes: String, val pos: Int) {
    MINIMUM(1.2f, "medcard_name_daily_minimum", "medcard_daily_minimum_desc", 0),
    LOWER(1.375f, "medcard_name_daily_lower", "medcard_daily_lower_desc", 1),
    MEDIUM(1.55f, "medcard_name_daily_medium", "medcard_daily_medium_desc", 2),
    HIGH(1.725f, "medcard_name_daily_high", "medcard_daily_high_desc", 3),
    VERY_HIGH(1.9f, "medcard_name_daily_very_high", "medcard_daily_very_high_desc", 4)
}