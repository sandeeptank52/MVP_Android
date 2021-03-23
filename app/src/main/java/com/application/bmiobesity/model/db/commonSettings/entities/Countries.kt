package com.application.bmiobesity.model.db.commonSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.bmiobesity.model.retrofit.ResultSimpleCountry
import com.google.gson.annotations.SerializedName

@Entity(tableName = "countries")
data class Countries(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    var id: Int,

    @ColumnInfo(name = "value")
    @SerializedName("value")
    var value: String
){
    constructor(resultSimpleCountry: ResultSimpleCountry):this(
        resultSimpleCountry.id ?: 0,
        resultSimpleCountry.value ?: ""
    )
}