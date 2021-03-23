package com.application.bmiobesity.model.db.paramSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "param_unit")
data class ParamUnit(

    @PrimaryKey(autoGenerate = false)
    var id: String,

    @SerializedName("name_metric_res")
    @ColumnInfo(name = "name_metric_res")
    var nameMetricRes: String,

    @SerializedName("name_imperial_res")
    @ColumnInfo(name = "name_imperial_res")
    var nameImperialRes: String,

    ){
    companion object{
        fun convertSmToIn(sm: Float) = sm / 2.54f
        fun convertInToSm(inch: Float) = inch * 2.54f

        fun convertKgToLb(kg: Float) = kg / 0.45359237f
        fun convertLbToKg(lb: Float) = lb * 0.45359237f

        fun convertMmRtStToKPa(mmRtSt: Float) = mmRtSt / 7.5f
        fun convertKPaToMmRtSt(kPa: Float) = kPa * 7.5f

        fun convertMmolLToMgDl(mmolL: Float, molarMass: Float) = (mmolL * molarMass) / 10.0f
        fun convertMgDlToMmolL(MgDl: Float, molarMass: Float) = if (molarMass != 0.0f) (MgDl * 10.0f) / molarMass else 0.0f
    }
}