package com.application.bmiobesity.common

import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.OnBoardingSteps
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile

class ProfileManager private constructor(){

    lateinit var currentProfile: Profile
    lateinit var currentAvailableData: AvailableData
    lateinit var currentOnBoardingSteps: OnBoardingSteps

    companion object{
        @Volatile
        private var INSTANCE: ProfileManager? = null

        fun getProfileManager(): ProfileManager{
            return INSTANCE ?: synchronized(this){
                val instance = ProfileManager()
                INSTANCE = instance
                instance
            }
        }
    }
}