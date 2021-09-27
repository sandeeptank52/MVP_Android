package com.application.bmiobesity.model.db.paramSettings.entities.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.bmiobesity.model.retrofit.*
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
    var gender: Int = 3,

    @ColumnInfo(name = "height")
    var height: Float = 0.0f,

    @ColumnInfo(name = "smoker")
    var smoker: Boolean = false,

    @ColumnInfo(name = "measuring_system")
    var measuringSystem: Int = 1,

    @ColumnInfo(name = "saved_pass")
    var savedPassword: Boolean = false,

    @ColumnInfo(name = "firs_time_stamp")
    var firsTimeStamp: Long = 0L
){
    fun loadFromProfile(resProfile: ResultProfile){
        if (!resProfile.email.isNullOrEmpty()) this.email = resProfile.email
        this.firstName = resProfile.first_name ?: ""
        this.lastName = resProfile.last_name ?: ""
        this.imageURI = resProfile.image ?: ""
    }

    fun loadFromUserProfile(resUserProfile: ResultUserProfile){
        this.birthDate = resUserProfile.birth_date ?: ""
        this.country = resUserProfile.country ?: 0
        this.gender = resUserProfile.gender ?: 3
        this.height = resUserProfile.height ?: 0.0f
        this.smoker = resUserProfile.smoker ?: false
        this.measuringSystem = resUserProfile.measuring_system ?: 1
    }

    fun loadFromDashBoard(dashBoard: UpdateResultDashBoard){
        this.birthDate = dashBoard.birth_date ?: ""
        this.country = dashBoard.country ?: 0
        this.gender = dashBoard.gender ?: 3
        this.height = dashBoard.height ?: 0.0f
        this.smoker = dashBoard.smoker ?: false
        this.measuringSystem = dashBoard.measuring_system ?: 1
    }

    fun getSendProfile(): SendProfile = SendProfile(this.firstName, this.lastName, this.email)
    fun getSendUserProfile(): SendUserProfile = SendUserProfile( if (this.birthDate.isEmpty()) null else this.birthDate, if (this.country > 0) this.country else null, this.gender, if (this.height > 0) this.height else null, this.smoker, 1)
    fun getSendDashBoard(): UpdateResultDashBoard{
        return UpdateResultDashBoard(
            this.gender,
            if (this.birthDate.isEmpty()) null else this.birthDate,
            if (this.country > 0) this.country else null,
            if (this.height > 0) this.height else null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            this.smoker,
            null,
            null,
            null,
            1
        )
    }
}