package com.application.bmiobesity.model.db.paramSettings.entities.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard
import com.application.bmiobesity.model.retrofit.ResultMedCard

@Entity(tableName = "available_data")
data class AvailableData(

    // Profile
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "height")
    var height: Boolean = false,

    // Medical card
    var weight: Boolean = false,
    var hip: Boolean = false,
    var waist: Boolean = false,
    var wrist: Boolean = false,
    var neck: Boolean = false,
    var heartRateAlone: Boolean = false,
    var dailyActivity: Boolean = false,
    var bloodPressureSys: Boolean = false,
    var bloodPressureDia: Boolean = false,
    var cholesterol: Boolean = false,
    var glucose: Boolean = false
){
    fun updateAvailableParam(card: ResultMedCard){
        this.weight = card.weight != null
        this.hip = card.hip != null
        this.waist = card.waist != null
        this.wrist = card.wrist != null
        this.neck = card.neck != null
        this.heartRateAlone = card.heart_rate_alone != null
        this.dailyActivity = card.daily_activity_level != null
        this.bloodPressureSys = card.blood_pressure_sys != null
        this.bloodPressureDia = card.blood_pressure_dia != null
        this.cholesterol = card.cholesterol != null
        this.glucose = card.glucose != null
    }

    fun updateAvailableProfile(profile: Profile){
        if (profile.email.isNotEmpty()) this.email = profile.email
        this.height = profile.height > 0.0f
    }
}