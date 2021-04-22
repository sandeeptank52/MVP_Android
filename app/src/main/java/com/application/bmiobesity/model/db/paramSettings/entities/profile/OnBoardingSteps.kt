package com.application.bmiobesity.model.db.paramSettings.entities.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "on_boarding_steps")
data class OnBoardingSteps(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "want_to_help")
    var wantToHelp: Boolean = false,
    @ColumnInfo(name = "receive_question")
    var receiveQuestion: Boolean = false,
    @ColumnInfo(name = "receive_updates")
    var receiveUpdates: Boolean = false,

    @ColumnInfo(name = "fill_profile")
    var fillProfile: Boolean = false,
    @ColumnInfo(name = "fill_medical_card")
    var fillMedCard: Boolean = false
)