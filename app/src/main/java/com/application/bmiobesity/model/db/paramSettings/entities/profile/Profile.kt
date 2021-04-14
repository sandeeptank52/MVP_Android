package com.application.bmiobesity.model.db.paramSettings.entities.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.bmiobesity.model.retrofit.ResultProfile
import com.application.bmiobesity.model.retrofit.ResultUserProfile
import com.application.bmiobesity.model.retrofit.SendProfile
import com.application.bmiobesity.model.retrofit.SendUserProfile
import com.google.gson.annotations.SerializedName

@Entity(tableName = "profile")
data class Profile(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "first_name")
    var firstName: String = "",

    @ColumnInfo(name = "last_name")
    var lastName: String = "",

    @ColumnInfo(name = "image")
    var imageURI: String = "",

    @ColumnInfo(name = "birth_date")
    var birthDate: String = "",

    @ColumnInfo(name = "country")
    var country: Int = 0,

    @ColumnInfo(name = "gender")
    var gender: Int = 0,

    @ColumnInfo(name = "height")
    var height: Float = 0.0f,

    @ColumnInfo(name = "smoker")
    var smoker: Boolean = false,

    @ColumnInfo(name = "measuring_system")
    var measuringSystem: Int = 0,

    @ColumnInfo(name = "saved_pass")
    var savedPassword: Boolean = false
){
    fun loadFromProfile(resProfile: ResultProfile){
        this.firstName = resProfile.first_name ?: "First name"
        this.lastName = resProfile.last_name ?: "Last name"
        this.imageURI = resProfile.image ?: ""
    }

    fun loadFromUserProfile(resUserProfile: ResultUserProfile){
        this.birthDate = resUserProfile.birth_date ?: ""
        this.country = resUserProfile.country ?: 0
        this.gender = resUserProfile.gender ?: 0
        this.height = resUserProfile.height ?: 0.0f
        this.smoker = resUserProfile.smoker ?: false
        this.measuringSystem = resUserProfile.measuring_system ?: 0
    }

    fun getSendProfile(): SendProfile = SendProfile(this.firstName, this.lastName, this.email)
    fun getSendUserProfile(): SendUserProfile = SendUserProfile(this.birthDate, this.country, this.gender, this.height, this.smoker, this.measuringSystem)
}