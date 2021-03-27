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
import com.application.bmiobesity.model.retrofit.RemoteRepo
import com.application.bmiobesity.model.retrofit.RetrofitResult
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
    private lateinit var medCardParamSetting: List<MedCardParamSetting>

    private lateinit var profile: Profile


    init {
        InTimeApp.appComponent.inject(this)

        viewModelScope.launch(Dispatchers.IO) {
            /*val getAppPreferenceDeferred = async { getAppPreference() }

            val getGendersDeferred = async { getGenders() }
            val getCountriesDeferred = async { getCountries() }
            val getPolicyDeferred = async { getPolicy() }

            val getResultCardDeferred = async { getResultCard() }
            val getParamUnitDeferred = async { getParamUnit() }
            val getMedCardSourceTypeDeferred = async { getMedCardSourceType() }
            val getMedCardParamSettingDeferred = async { getMedCardParamSetting() }*/

            //appPreference = getAppPreferenceDeferred.await()
            //genders = getGendersDeferred.await()
            //countries = getCountriesDeferred.await()
            //policy = getPolicyDeferred.await()

            val upd = updateFromDB()
            upd.join()

            //resultCard = getResultCardDeferred.await()
            //paramUnit = getParamUnitDeferred.await()
            //medCardSourceType = getMedCardSourceTypeDeferred.await()
            //medCardParamSetting = getMedCardParamSettingDeferred.await()

            //val access = "Bearer ${appPreference.accessToken}"

            /*when (val resultProfile = remoteRepo.getProfile(access)){
                is RetrofitResult.Success -> {
                    profile = Profile(resultProfile.value)
                }
                is RetrofitResult.Error -> {

                }
            }

            when (val resultMedCard = remoteRepo.getMedCard(access)){
                is RetrofitResult.Success -> {
                    val i = 0
                }
                is RetrofitResult.Error -> {
                    val i = 0
                }
            }*/

            eventManager.preloadSuccessEvent(true)
            test()
        }
    }

    private suspend fun updateGenders() = viewModelScope.launch(Dispatchers.IO) { genders =  commonSettingRepo.getAllGenders() }
    private suspend fun updateCountries() = viewModelScope.launch(Dispatchers.IO) { countries =  commonSettingRepo.getAllCountries() }
    private suspend fun updatePolicy() = viewModelScope.launch(Dispatchers.IO) { policy =  commonSettingRepo.getAllPolicy() }
    private suspend fun updateFromDB(): Job{
        return viewModelScope.launch(Dispatchers.IO) {
            val gendersJob = updateGenders()
            val countriesJob = updateCountries()
            val policyJob = updatePolicy()
            gendersJob.join()
            countriesJob.join()
            policyJob.join()
        }
    }

    private suspend fun getAppPreference(): AppPreference = appSetting.getAppPreference().first()

    private suspend fun getGenders(): List<Genders> = commonSettingRepo.getAllGenders()
    private suspend fun getCountries(): List<Countries> = commonSettingRepo.getAllCountries()
    private suspend fun getPolicy(): List<Policy> = commonSettingRepo.getAllPolicy()

    private suspend fun getResultCard(): List<ResultCard> = paramSettingRepo.getAllResultCard()
    private suspend fun getParamUnit(): List<ParamUnit> = paramSettingRepo.getAllParamUnit()
    private suspend fun getMedCardSourceType(): List<MedCardSourceType> = paramSettingRepo.getAllMedCardSourceType()
    private suspend fun getMedCardParamSetting(): List<MedCardParamSetting> = paramSettingRepo.getAllMedCardParamSetting()

    private fun test(){
        val i = 0
    }


}