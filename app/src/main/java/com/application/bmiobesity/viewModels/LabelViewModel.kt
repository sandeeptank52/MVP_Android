package com.application.bmiobesity.viewModels

import androidx.datastore.preferences.core.Preferences
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
import com.application.bmiobesity.model.localStorage.LocalStorageRepo
import com.application.bmiobesity.model.retrofit.*
import com.application.bmiobesity.utils.getCurrentLocale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LabelViewModel : ViewModel() {

    @Inject
    lateinit var paramSettingRepo: ParamSettingsRepo
    @Inject
    lateinit var localRepo: LocalStorageRepo
    @Inject
    lateinit var commonSettingRepo: CommonSettingRepo
    @Inject
    lateinit var remoteRepo: RemoteRepo
    @Inject
    lateinit var appSetting: AppSettingDataStore

    private lateinit var appPreference: AppPreference

    init {
        InTimeApp.appComponent.inject(this)
    }

    suspend fun initAppPreference(){
        val deviceUUID = appSetting.getStringParam(AppSettingDataStore.PrefKeys.DEVICE_UUID).first()
        if (deviceUUID.isEmpty()) appSetting.setStringParam(AppSettingDataStore.PrefKeys.DEVICE_UUID, UUID.randomUUID().toString())
        appPreference = appSetting.getAppPreference().first()
        appSetting.setBooleanParam(AppSettingDataStore.PrefKeys.FIRST_TIME, appPreference.firstTime)
    }

    suspend fun initParamSetting(){
        paramSettingRepo.insertAllResultCard(localRepo.getResultCardList())
        paramSettingRepo.insertAllParamUnit(localRepo.getParamUnitList())
        paramSettingRepo.insertAllMedCardSourceType(localRepo.getMedCardSourceType())
        paramSettingRepo.insertAllMedCardParamSetting(localRepo.getMedCardParamSettingList())
    }

    suspend fun initCommonSetting(){
        when (val resultCountries = remoteRepo.getCountries(getCurrentLocale())){
            is RetrofitResult.Success -> {
                prepopulateCountriesFromRemote(resultCountries.value)
            }
            is RetrofitResult.Error -> {
                prepopulateCountriesFromLocal()
            }
        }

        when (val resultGenders = remoteRepo.getGenders(getCurrentLocale())){
            is RetrofitResult.Success -> {
                prepopulateGendersFromRemote(resultGenders.value)
            }
            is RetrofitResult.Error -> {
                prepopulateGendersFromLocal()
            }
        }

        when (val resultPolicy = remoteRepo.getPolicy(getCurrentLocale().locale)){
            is RetrofitResult.Success -> {
                prepopulatePolicyFromRemote(resultPolicy.value)
            }
            is RetrofitResult.Error -> {
                prepopulatePolicyFromLocal()
            }
        }
    }

    fun isFirstTime() = appPreference.firstTime
    fun isNeedShowDisclaimer() = appPreference.showDisclaimer

    fun setBooleanParam(idParam: Preferences.Key<Boolean>, value: Boolean){
        viewModelScope.launch {
            appSetting.setBooleanParam(idParam, value)
            updateAppPreference()
        }
    }

    private fun updateAppPreference(){
        viewModelScope.launch { appPreference = appSetting.getAppPreference().first() }
    }

    private suspend fun prepopulateCountriesFromRemote(result: ResultListCountries){
        val countriesList = result.countries
        if (!countriesList.isNullOrEmpty()){
            val res: ArrayList<Countries> = ArrayList()
            for (i in countriesList.indices){
                val temp = Countries(countriesList[i])
                res.add(temp)
            }
            commonSettingRepo.setAllCountries(res)
        } else prepopulateCountriesFromLocal()
    }
    private suspend fun prepopulateCountriesFromLocal(){
        commonSettingRepo.setAllCountries(localRepo.getCountriesList(getCurrentLocale().locale))
    }

    private suspend fun prepopulateGendersFromRemote(result: ResultListGenders){
        val gendersList = result.genders
        if (!gendersList.isNullOrEmpty()){
            val res: ArrayList<Genders> = ArrayList()
            for (i in gendersList.indices){
                val temp = Genders(gendersList[i])
                res.add(temp)
            }
            commonSettingRepo.setAllGenders(res)
        } else prepopulateGendersFromLocal()
    }
    private suspend fun prepopulateGendersFromLocal(){
        commonSettingRepo.setAllGenders(localRepo.getGendersList(getCurrentLocale().locale))
    }

    private suspend fun prepopulatePolicyFromRemote(result: ResultPolicy){
        if (!result.policy.isNullOrEmpty()){
            val temp = Policy(getCurrentLocale().locale, result.policy)
            commonSettingRepo.setPolicy(temp)
        } else prepopulatePolicyFromLocal()
    }
    private suspend fun prepopulatePolicyFromLocal(){
        commonSettingRepo.setPolicy(localRepo.getPolicy(getCurrentLocale().locale))
    }

    override fun onCleared() {
        super.onCleared()
    }
}