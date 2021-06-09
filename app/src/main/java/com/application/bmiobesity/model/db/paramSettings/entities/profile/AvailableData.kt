package com.application.bmiobesity.model.db.paramSettings.entities.profile

import com.application.bmiobesity.model.retrofit.ResultMedCard
import com.application.bmiobesity.model.retrofit.ResultProfile
import com.application.bmiobesity.model.retrofit.ResultUserProfile
import com.application.bmiobesity.model.retrofit.UpdateResultDashBoard

data class AvailableData(

    // Profile
    var email: String,
    var heightAvailable: Boolean = false,
    var firstNameAvailable: Boolean = false,
    var lastNameAvailable: Boolean = false,
    var imageURIAvailable: Boolean = false,
    var birthDateAvailable: Boolean = false,
    var countryAvailable: Boolean = false,
    var genderAvailable: Boolean = false,

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
        this.heightAvailable = profile.height > 0.0f
        this.firstNameAvailable = profile.firstName.isNotEmpty()
        this.lastNameAvailable = profile.lastName.isNotEmpty()
        this.imageURIAvailable = profile.imageURI.isNotEmpty()
        this.birthDateAvailable = profile.birthDate.isNotEmpty()
        this.countryAvailable = profile.country > 0
        this.genderAvailable = profile.gender != 3
    }
    fun updateResultProfile(resultProfile: ResultProfile){
        if (!resultProfile.email.isNullOrEmpty()) this.email = resultProfile.email
        this.firstNameAvailable = !resultProfile.first_name.isNullOrEmpty()
        this.lastNameAvailable = !resultProfile.last_name.isNullOrEmpty()
        this.imageURIAvailable = !resultProfile.image.isNullOrEmpty()
    }
    fun updateResultUserProfile(resultUserProfile: ResultUserProfile){
        this.heightAvailable = resultUserProfile.height?.let { it > 0.0f } ?: false
        this.birthDateAvailable = !resultUserProfile.birth_date.isNullOrEmpty()
        this.countryAvailable = resultUserProfile.country?.let { it > 0 } ?: false
        this.genderAvailable = resultUserProfile.gender?.let { it == 1 || it == 2 } ?: false
    }
    fun updateAvailableFromDashBoard(dashBoard: UpdateResultDashBoard){
        this.weight = dashBoard.weight != null
        this.hip = dashBoard.hip != null
        this.waist = dashBoard.waist != null
        this.wrist = dashBoard.wrist != null
        this.neck = dashBoard.neck != null
        this.heartRateAlone = dashBoard.heart_rate_alone != null
        this.dailyActivity = dashBoard.daily_activity_level != null
        this.bloodPressureSys = dashBoard.blood_pressure_sys != null
        this.bloodPressureDia = dashBoard.blood_pressure_dia != null
        this.cholesterol = dashBoard.cholesterol != null
        this.glucose = dashBoard.glucose != null

        this.heightAvailable = dashBoard.height?.let { it > 0.0f } ?: false
        this.birthDateAvailable = !dashBoard.birth_date.isNullOrEmpty()
        this.countryAvailable = dashBoard.country?.let { it > 0 } ?: false
        this.genderAvailable = dashBoard.gender != 3
    }

    fun getProfileAvailable(): Boolean = heightAvailable && firstNameAvailable && lastNameAvailable && birthDateAvailable && countryAvailable && genderAvailable
    fun getMedCardAvailable(): Boolean = weight && hip && waist && wrist && neck && heartRateAlone && dailyActivity && bloodPressureSys && bloodPressureDia && cholesterol && glucose
}