package com.application.bmiobesity.model.db.paramSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "result_card")
data class ResultCard(

    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    var id: String,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var nameRes: String,

    @SerializedName("img")
    @ColumnInfo(name = "img")
    var imgRes: String,

    @SerializedName("short_description")
    @ColumnInfo(name = "short_description")
    var shortDescriptionRes: String,

    @SerializedName("long_description")
    @ColumnInfo(name = "long_description")
    var longDescriptionRes: String,

    @SerializedName("last_modified")
    @ColumnInfo(name = "last_modified")
    var lastModified: Long,

    @SerializedName("data_type")
    @ColumnInfo(name = "data_type")
    var dataType: String,

    @SerializedName("is_visible")
    @ColumnInfo(name = "is_visible")
    var isVisible: Boolean
){
    @Ignore
    @SerializedName("value")
    var value: String = ""

    @Ignore
    @SerializedName("value_color")
    var valueColour: String = ""
}