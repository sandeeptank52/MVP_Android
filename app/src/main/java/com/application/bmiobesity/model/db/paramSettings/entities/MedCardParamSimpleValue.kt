package com.application.bmiobesity.model.db.paramSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medcard_param_simple_values")
data class MedCardParamSimpleValue(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0L,

    @ColumnInfo(name = "timestamp_start")
    var timestampStart: Long = 0L,

    @ColumnInfo(name = "timestamp_end")
    var timestampEnd: Long = 0L,

    @ColumnInfo(name = "param_id")
    var paramID: String,

    @ColumnInfo(name = "value")
    var value: Float?,

    @ColumnInfo(name = "value_imp")
    var valueImp: Float?
)