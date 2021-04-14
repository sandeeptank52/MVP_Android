package com.application.bmiobesity.model.db.paramSettings.entities.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "available_data")
data class AvailableData(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "birth_date")
    var birthDate: Boolean = false,
)