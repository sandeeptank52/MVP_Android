package com.application.bmiobesity.model.db.paramSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medcard_param_simple_value")
data class MedCardParamSimpleValue(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "timestamp_start")
    val timestampStart: Long = 0L,

    @ColumnInfo(name = "timestamp_end")
    val timestampEnd: Long = 0L,

    @ColumnInfo(name = "param_id")
    val paramID: String,

    @ColumnInfo(name = "value")
    val value: String
)