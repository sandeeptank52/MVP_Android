package com.application.bmiobesity.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.OnBoardingSteps
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile
import com.application.bmiobesity.model.retrofit.ResultMedCard
import kotlinx.coroutines.coroutineScope

class ProfileManager private constructor(){

    private val mCurrentProfile: MutableLiveData<Profile> by lazy { MutableLiveData<Profile>() }
    val currentProfile: LiveData<Profile> = mCurrentProfile

    private val mCurrentAvailableData: MutableLiveData<AvailableData> by lazy { MutableLiveData<AvailableData>() }
    val currentAvailableData: LiveData<AvailableData> = mCurrentAvailableData

    private var availableData: AvailableData = AvailableData("")
    private var currentOnBoardingSteps: OnBoardingSteps = OnBoardingSteps("")

    fun setProfile(item: Profile) = this.mCurrentProfile.postValue(item)

    fun updateAvailableMedCardData(item: ResultMedCard){
        availableData.updateAvailableParam(item)
        mCurrentAvailableData.postValue(availableData)
    }
    fun updateAvailableProfileData(item: Profile){
        availableData.updateAvailableProfile(item)
        mCurrentAvailableData.postValue(availableData)
    }

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