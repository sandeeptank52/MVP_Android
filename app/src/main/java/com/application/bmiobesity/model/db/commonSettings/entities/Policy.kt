package com.application.bmiobesity.model.db.commonSettings.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.bmiobesity.model.retrofit.ResultPolicy

@Entity(tableName = "policy")
data class Policy(
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo(name = "value")
    val value: String
){
    constructor(locale: String, resultPolicy: ResultPolicy) : this(
        locale,
        resultPolicy.policy ?: ""
    )
}