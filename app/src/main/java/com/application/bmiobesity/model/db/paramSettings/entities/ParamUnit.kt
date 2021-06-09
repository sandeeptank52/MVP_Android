package com.application.bmiobesity.model.db.paramSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.bmiobesity.utils.numberToOneDigit
import com.application.bmiobesity.utils.numberToWithoutDigit
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
        fun convertSmToIn(sm: Float?): Float?{
            if (sm == null) return null
            return numberToOneDigit(sm / 2.54f)
        }
        fun convertInToSm(inch: Float?): Float?{
            if (inch == null) return null
            return numberToWithoutDigit(inch * 2.54f)
        }

        fun convertKgToLb(kg: Float?): Float?{
            if (kg == null) return null
            return numberToWithoutDigit(kg / 0.45359237f)
        }
        fun convertLbToKg(lb: Float?): Float?{
            if (lb == null) return null
            return numberToWithoutDigit(lb * 0.45359237f)
        }

        fun convertMmRtStToKPa(mmRtSt: Float?): Float?{
            if (mmRtSt == null) return null
            return numberToOneDigit(mmRtSt / 7.5f)
        }
        fun convertKPaToMmRtSt(kPa: Float?):Float?{
            if (kPa == null) return null
            return numberToWithoutDigit(kPa * 7.5f)
        }

        fun convertMmolLToMgDl(mmolL: Float?, molarMass: Float): Float?{
            if (mmolL == null) return null
            return numberToWithoutDigit ((mmolL * molarMass) / 10.0f)
        }
        fun convertMgDlToMmolL(MgDl: Float?, molarMass: Float): Float?{
            if (MgDl == null) return null
            return numberToOneDigit(if (molarMass != 0.0f) (MgDl * 10.0f) / molarMass else 0.0f)
        }
    }
}