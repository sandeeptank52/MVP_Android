package com.application.bmiobesity.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.OnBoardingSteps
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile
import com.application.bmiobesity.model.retrofit.ResultMedCard
import com.application.bmiobesity.model.retrofit.ResultProfile
import com.application.bmiobesity.model.retrofit.ResultUserProfile
import com.application.bmiobesity.model.retrofit.UpdateResultDashBoard

class ProfileManager private constructor(){

    private val mCurrentProfile: MutableLiveData<Profile> by lazy { MutableLiveData<Profile>() }
    val currentProfile: LiveData<Profile> = mCurrentProfile

    private val mCurrentAvailableData: MutableLiveData<AvailableData> by lazy { MutableLiveData<AvailableData>() }
    val currentAvailableData: LiveData<AvailableData> = mCurrentAvailableData

    private val mCurrentMeasuringSystem: MutableLiveData<MeasuringSystem> by lazy { MutableLiveData<MeasuringSystem>() }
    val currentMeasurementSystem: LiveData<MeasuringSystem> = mCurrentMeasuringSystem

    val trialPeriodExpired: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    private var availableData: AvailableData = AvailableData("")
    private var currentOnBoardingSteps: OnBoardingSteps = OnBoardingSteps("")

    fun setProfile(item: Profile){
        this.mCurrentProfile.postValue(item)
        when(item.measuringSystem){
            1 -> setMeasuringSystem(MeasuringSystem.METRIC)
            2 -> setMeasuringSystem(MeasuringSystem.IMPERIAL)
        }
    }
    fun setMeasuringSystem(system: MeasuringSystem) = this.mCurrentMeasuringSystem.postValue(system)
    fun setAvatarUrl(url: String){
        val profile = mCurrentProfile.value
        profile?.let {
            it.imageURI = url
            mCurrentProfile.postValue(it)
        }
    }

    fun updateAvailableMedCardData(item: ResultMedCard){
        availableData.updateAvailableParam(item)
        mCurrentAvailableData.postValue(availableData)
    }
    fun updateAvailableProfileData(item: Profile){
        availableData.updateAvailableProfile(item)
        mCurrentAvailableData.postValue(availableData)
    }
    fun updateAvailableDashBoard(item: UpdateResultDashBoard){
        availableData.updateAvailableFromDashBoard(item)
        mCurrentAvailableData.postValue(availableData)
    }
    fun updateAvailableResultProfileData(item: ResultProfile){
        availableData.updateResultProfile(item)
        mCurrentAvailableData.postValue(availableData)
    }
    fun updateAvailableResultUserProfile(item: ResultUserProfile){
        availableData.updateResultUserProfile(item)
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