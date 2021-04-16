package com.application.bmiobesity.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.model.db.paramSettings.ParamSettingsRepo
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.OnBoardingSteps
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile
import com.application.bmiobesity.model.retrofit.RemoteRepo
import com.application.bmiobesity.model.retrofit.RetrofitResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserManagerViewModel : ViewModel() {





    /*private fun createNewProfile(mail: String){
        currentProfile = Profile(mail)
        insertNewProfile(currentProfile)
    }

    private fun createNewAvailableData(mail: String){
        currentAvailableData = AvailableData(mail)
        insertNewAvailableData(currentAvailableData)
    }

    private fun createNewOnBoardingStep(mail: String){
        currentOnBoardingSteps = OnBoardingSteps(mail)
        insertNewOnBoardingSteps(currentOnBoardingSteps)
    }

    private fun insertNewProfile(item: Profile) = viewModelScope.launch(Dispatchers.IO) { paramSettingRepo.insertProfile(item) }
    private fun insertNewAvailableData(item: AvailableData) = viewModelScope.launch(Dispatchers.IO) { paramSettingRepo.insertAvailableData(item) }
    private fun insertNewOnBoardingSteps(item: OnBoardingSteps) = viewModelScope.launch(Dispatchers.IO) { paramSettingRepo.insertOnBoardingStep(item) }

    private fun getAvailableDataByMailAsync(mail: String) = viewModelScope.async(Dispatchers.IO) { paramSettingRepo.getAvailableDataFromMail(mail) }

    private suspend fun getAccessTokenAsync() = viewModelScope.async(Dispatchers.IO) { appSetting.getStringParam(AppSettingDataStore.PrefKeys.ACCESS_TOKEN).first() }
    private suspend fun getCurrentMailAsync() = viewModelScope.async(Dispatchers.IO) { appSetting.getStringParam(AppSettingDataStore.PrefKeys.USER_MAIL).first() }


    private fun test(){
        val i = 0
    }*/
}