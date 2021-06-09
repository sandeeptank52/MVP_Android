package com.application.bmiobesity.model.db.paramSettings.entities.profile

data class OnBoardingSteps(

    var email: String,
    var wantToHelp: Boolean = false,
    var receiveQuestion: Boolean = false,
    var receiveUpdates: Boolean = false,
    var fillProfile: Boolean = false,
    var fillMedCard: Boolean = false
)