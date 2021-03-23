package com.application.bmiobesity.model.db.commonSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.bmiobesity.model.retrofit.ResultSimpleGender
import com.google.gson.annotations.SerializedName

@Entity(tableName = "genders")
data class Genders(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    var id: Int,

    @ColumnInfo(name = "value")
    @SerializedName("value")
    var value: String
){
    constructor(resultSimpleGender: ResultSimpleGender) : this(
        resultSimpleGender.id ?: 0,
        resultSimpleGender.value ?: ""
    )
}