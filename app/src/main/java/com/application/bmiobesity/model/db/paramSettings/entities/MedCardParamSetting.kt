package com.application.bmiobesity.model.db.paramSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "medcard_param_setting")
data class MedCardParamSetting(

    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    var id: String,

    @SerializedName("name_res")
    @ColumnInfo(name = "name_res")
    var nameRes: String,

    @SerializedName("short_description_res")
    @ColumnInfo(name = "short_description_res")
    var shortDescriptionRes: String,

    @SerializedName("long_description_res")
    @ColumnInfo(name = "long_description_res")
    var longDescriptionRes: String,

    @SerializedName("img_res")
    @ColumnInfo(name = "img_res")
    var imgRes: String,


    @SerializedName("default_value")
    @ColumnInfo(name = "default_value")
    var defaultValue: String,

    @SerializedName("data_type")
    @ColumnInfo(name = "data_type")
    var dataType: String,

    @SerializedName("comment")
    @ColumnInfo(name = "comment")
    var comment: String,

    @SerializedName("status")
    @ColumnInfo(name = "status")
    var status: Boolean,

    @SerializedName("display_type")
    @ColumnInfo(name = "display_type")
    var displayType: String,


    @SerializedName("source_type_id")
    @ColumnInfo(name = "source_type_id")
    var sourceTypeID: String,

    @SerializedName("source_type_id_multi")
    @ColumnInfo(name = "source_type_id_multi")
    var sourceTypeIDForMultipleMeasure: String,

    @SerializedName("unit_id")
    @ColumnInfo(name = "unit_id")
    var unitID: String,

    @SerializedName("molar_mass")
    @ColumnInfo(name = "molar_mass")
    var molarMass: Float
){
    @Ignore
    var values: MutableList<MedCardParamSimpleValue> = mutableListOf()
}