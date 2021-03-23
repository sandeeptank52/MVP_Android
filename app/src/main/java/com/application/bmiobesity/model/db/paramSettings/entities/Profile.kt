package com.application.bmiobesity.model.db.paramSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.bmiobesity.model.retrofit.ResultProfile
import com.google.gson.annotations.SerializedName

@Entity(tableName = "profile")
data class Profile(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("email")
    var email: String,

    @ColumnInfo(name = "first_name")
    @SerializedName("first_name")
    var firstName: String,

    @ColumnInfo(name = "last_name")
    @SerializedName("last_name")
    var lastName: String,

    /*
    @ColumnInfo(name = "image")
    @SerializedName("image")
    var image: String
     */
){
    constructor(resultProfile: ResultProfile) : this(
        resultProfile.email ?: "",
        resultProfile.first_name ?: "First Name",
        resultProfile.last_name ?: "Last Name"
    )
}