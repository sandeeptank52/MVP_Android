package com.application.bmiobesity.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.model.appSettings.AppPreference
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.model.db.commonSettings.CommonSettingRepo
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import com.application.bmiobesity.model.db.commonSettings.entities.Genders
import com.application.bmiobesity.model.db.commonSettings.entities.Policy
import com.application.bmiobesity.model.db.paramSettings.ParamSettingsRepo
import com.application.bmiobesity.model.db.paramSettings.entities.*
import com.application.bmiobesity.model.localStorage.LocalStorageRepo
import com.application.bmiobesity.model.parameters.MedCard
import com.application.bmiobesity.model.retrofit.RemoteRepo
import com.application.bmiobesity.model.retrofit.ResultFavorites
import com.application.bmiobesity.model.retrofit.ResultUserProfile
import com.application.bmiobesity.model.retrofit.RetrofitResult
import com.application.bmiobesity.utils.getCurrentLocale
import com.application.bmiobesity.viewModels.eventManagerMain.EventManagerMain
import com.application.bmiobesity.viewModels.eventManagerMain.MainViewModelEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MainViewModel : ViewModel() {
    @Inject
    lateinit var remoteRepo: RemoteRepo
    @Inject
    lateinit var appSetting: AppSettingDataStore
    @Inject
    lateinit var commonSettingRepo: CommonSettingRepo
    @Inject
    lateinit var paramSettingRepo: ParamSettingsRepo
    @Inject
    lateinit var localStorageRepo: LocalStorageRepo

    private val eventManager: MainViewModelEvent = EventManagerMain.getEventManager()

    private lateinit var appPreference: AppPreference

    private lateinit var genders: List<Genders>
    private lateinit var countries: List<Countries>
    private lateinit var policy: List<Policy>

    private lateinit var resultCard: List<ResultCard>
    private lateinit var paramUnit: List<ParamUnit>
    private lateinit var medCardSourceType: List<MedCardSourceType>

    private var medCard: MedCard

    private lateinit var profile: Profile
    private lateinit var userProfile: ResultUserProfile

    private lateinit var favorites: ResultFavorites


    init {
        InTimeApp.appComponent.inject(this)
        medCard = MedCard()

        viewModelScope.launch(Dispatchers.IO) {

            val upd = updateLocal()
            upd.join()

            val access = "Bearer ${appPreference.accessToken}"

            val updateProfileJob = updateProfile(access)
            val updateMedCardJob = updateMedCard(access)

            updateProfileJob.join()
            updateMedCardJob.join()

            when (val resultProfile = remoteRepo.getUserProfile(access)){
                is RetrofitResult.Success -> {
                    userProfile = resultProfile.value
                }
                is RetrofitResult.Error -> {

                }
            }

            when (val result = remoteRepo.getFavorites(access, getCurrentLocale().locale)){
                is RetrofitResult.Success -> {
                    resultCard.forEach { card ->
                        card.setValues(result.value.params?.find { it.name == card.id })
                    }

                }
                is RetrofitResult.Error -> {

                }
            }

            eventManager.preloadSuccessEvent(true)
            test()
        }
    }

    private suspend fun updateProfile(access: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val resultProfile = remoteRepo.getProfile(access)){
            is RetrofitResult.Success -> {
                profile = Profile(resultProfile.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    private suspend fun updateMedCard(access: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val resultMedCard = remoteRepo.getMedCard(access)){
            is RetrofitResult.Success -> {
                medCard.setValues(resultMedCard.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }

    private suspend fun updateGenders() = viewModelScope.launch(Dispatchers.IO) { genders =  commonSettingRepo.getAllGenders() }
    private suspend fun updateCountries() = viewModelScope.launch(Dispatchers.IO) { countries =  commonSettingRepo.getAllCountries() }
    private suspend fun updatePolicy() = viewModelScope.launch(Dispatchers.IO) { policy =  commonSettingRepo.getAllPolicy() }
    private suspend fun updateLocal() = viewModelScope.launch(Dispatchers.IO) {
        val gendersJob = updateGenders()
        val countriesJob = updateCountries()
        val policyJob = updatePolicy()

        val preferenceJob = updateAppPreference()

        val resultCardJob = updateResultCard()
        val paramUnitJob = updateParamUnit()
        val medCardSourceTypeJob = updateMedCardSourceType()
        val medCardParamSettingJob = updateMedCardParamSetting()

        gendersJob.join()
        countriesJob.join()
        policyJob.join()
        preferenceJob.join()
        resultCardJob.join()
        paramUnitJob.join()
        medCardSourceTypeJob.join()
        medCardParamSettingJob.join()
    }

    private suspend fun updateAppPreference() = viewModelScope.launch(Dispatchers.IO) { appPreference = appSetting.getAppPreference().first() }

    private suspend fun updateResultCard() = viewModelScope.launch(Dispatchers.IO) { resultCard = paramSettingRepo.getAllResultCard() }
    private suspend fun updateParamUnit() = viewModelScope.launch(Dispatchers.IO) { paramUnit = paramSettingRepo.getAllParamUnit() }
    private suspend fun updateMedCardSourceType() = viewModelScope.launch(Dispatchers.IO) { medCardSourceType = paramSettingRepo.getAllMedCardSourceType() }
    private suspend fun updateMedCardParamSetting() = viewModelScope.launch(Dispatchers.IO) { medCard.setParameters(paramSettingRepo.getAllMedCardParamSetting()) }

    private fun test(){
        val i = 0
    }


}