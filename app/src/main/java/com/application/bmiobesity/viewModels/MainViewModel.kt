package com.application.bmiobesity.viewModels

import android.os.Build
import androidx.lifecycle.*
import com.application.bmiobesity.BuildConfig
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.model.appSettings.AppPreference
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.model.db.commonSettings.CommonSettingRepo
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import com.application.bmiobesity.model.db.commonSettings.entities.Genders
import com.application.bmiobesity.model.db.commonSettings.entities.Policy
import com.application.bmiobesity.model.db.paramSettings.ParamSettingsRepo
import com.application.bmiobesity.model.db.paramSettings.entities.*
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile
import com.application.bmiobesity.model.localStorage.LocalStorageRepo
import com.application.bmiobesity.model.parameters.MedCard
import com.application.bmiobesity.model.retrofit.*
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

    // Common setting
    private lateinit var genders: List<Genders>
    private lateinit var countries: List<Countries>
    private lateinit var policy: List<Policy>

    // Result
    // Favorite screen
    private val mResultCard: MutableLiveData<List<ResultCard>> by lazy { MutableLiveData<List<ResultCard>>() }
    val resultCard: LiveData<List<ResultCard>> = mResultCard
    // Analyze screen
    private val mResultRiskAnalyze: MutableLiveData<List<ResultDiseaseRisk>> by lazy { MutableLiveData<List<ResultDiseaseRisk>>() }
    val resultRiskAnalyze: LiveData<List<ResultDiseaseRisk>> = mResultRiskAnalyze
    // Recommendations screen
    // Common
    private val mResultCommonRecommendations: MutableLiveData<List<ResultCommonRecommendation>> by lazy { MutableLiveData<List<ResultCommonRecommendation>>() }
    val resultCommonRecommendations: LiveData<List<ResultCommonRecommendation>> = mResultCommonRecommendations
    // Personal
    private val mResultPersonalRecommendations: MutableLiveData<List<ResultRecommendation>> by lazy { MutableLiveData<List<ResultRecommendation>>() }
    val resultPersonalRecommendations: LiveData<List<ResultRecommendation>> = mResultPersonalRecommendations

    // MedCard
    private var medCard: MedCard
    private lateinit var paramUnit: List<ParamUnit>
    private lateinit var medCardSourceType: List<MedCardSourceType>

    // User profile
    private lateinit var currentProfile: Profile

    init {
        InTimeApp.appComponent.inject(this)

        medCard = MedCard()


        viewModelScope.launch(Dispatchers.IO) {

            val upd = updateLocal()
            upd.join()

            val currentMail = getCurrentMailAsync().await()
            currentProfile = Profile(currentMail)

            //val refreshJob = refreshToken()
            //refreshJob.join()

            val access = "Bearer ${appPreference.accessToken}"

            val updateProfileJob = updateProfile(access)
            val updateUserProfileJob = updateUserProfile(access)
            val updateMedCardJob = updateMedCard(access)
            val updateResultFavoritesJob = updateResultCardServer(access, getCurrentLocale().locale)
            val updateAnalyzeJob = updateAnalyzeServer(access, getCurrentLocale().locale)
            val updateRecommendationsJob = updateRecommendationsServer(access, getCurrentLocale().locale)

            updateProfileJob.join()
            updateUserProfileJob.join()
            updateMedCardJob.join()
            updateResultFavoritesJob.join()
            updateAnalyzeJob.join()
            updateRecommendationsJob.join()

            eventManager.preloadSuccessEvent(true)
            test()
        }
    }

    private suspend fun refreshToken() = viewModelScope.launch(Dispatchers.IO) {
        val refresh = SendRefreshToken(SendRefresh(appPreference.refreshToken), getDevice())
        when (val result = remoteRepo.refreshToken(refresh)){
            is RetrofitResult.Success -> {
                appSetting.setStringParam(AppSettingDataStore.PrefKeys.ACCESS_TOKEN, result.value.access ?: "")
                appSetting.setStringParam(AppSettingDataStore.PrefKeys.REFRESH_TOKEN, result.value.refresh ?: "")
                val updateJob = updateAppPreference()
                updateJob.join()
            }
            is RetrofitResult.Error -> {

            }
        }
    }

    private suspend fun updateProfile(access: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val resultProfile = remoteRepo.getProfile(access)){
            is RetrofitResult.Success -> {
                currentProfile.loadFromProfile(resultProfile.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    private suspend fun updateUserProfile(access: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val resultProfile = remoteRepo.getUserProfile(access)){
            is RetrofitResult.Success -> {
                currentProfile.loadFromUserProfile(resultProfile.value)
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

        val resultCardJob = updateResultCardDB()
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


    private suspend fun updateParamUnit() = viewModelScope.launch(Dispatchers.IO) { paramUnit = paramSettingRepo.getAllParamUnit() }
    private suspend fun updateMedCardSourceType() = viewModelScope.launch(Dispatchers.IO) { medCardSourceType = paramSettingRepo.getAllMedCardSourceType() }
    private suspend fun updateMedCardParamSetting() = viewModelScope.launch(Dispatchers.IO) { medCard.setParameters(paramSettingRepo.getAllMedCardParamSetting()) }

    // Update settings result parameters from DB
    private suspend fun updateResultCardDB() = viewModelScope.launch(Dispatchers.IO) { mResultCard.postValue(paramSettingRepo.getAllResultCard()) }

    // Update result from server
    // Update favorite screen
    private suspend fun updateResultCardServer(access: String, locale: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = remoteRepo.getFavorites(access, locale)){
            is RetrofitResult.Success -> {
                val temp = mResultCard.value
                temp?.forEach { card ->
                    card.setValues(result.value.params?.find { it.name == card.id })
                }
                mResultCard.postValue(temp)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    // Update disease risk and common recommendations
    private suspend fun updateAnalyzeServer(access: String, locale: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = remoteRepo.getResultAnalyze(access, locale)){
            is RetrofitResult.Success -> {
                mResultRiskAnalyze.postValue(result.value.disease_risk)
                mResultCommonRecommendations.postValue(result.value.common_recomendations)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    // Update personal recommendations
    private suspend fun updateRecommendationsServer(access: String, locale: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = remoteRepo.getRecommendations(access, locale)){
            is RetrofitResult.Success -> {
                mResultPersonalRecommendations.postValue(result.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }

    private fun getDevice() = SendDevice(
        appPreference.deviceUUID,
        AppSettingDataStore.Constants.OS_NAME,
        Build.VERSION.RELEASE,
        "${Build.BRAND} - ${Build.MODEL}",
        BuildConfig.VERSION_NAME
    )

    suspend fun isFirstTimeAsync(): Deferred<Boolean> = viewModelScope.async { appSetting.getBoolParam(AppSettingDataStore.PrefKeys.FIRST_TIME).first() }
    private suspend fun getCurrentMailAsync(): Deferred<String> = viewModelScope.async { appSetting.getStringParam(AppSettingDataStore.PrefKeys.USER_MAIL).first() }

    private fun test(){
        val i = 0
    }
}