package com.application.bmiobesity.viewModels

import android.os.Build
import androidx.lifecycle.*
import com.application.bmiobesity.BuildConfig
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.common.ProfileManager
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
import com.application.bmiobesity.common.parameters.MedCard
import com.application.bmiobesity.model.retrofit.*
import com.application.bmiobesity.utils.getCurrentLocale
import com.application.bmiobesity.common.eventManagerMain.EventManagerMain
import com.application.bmiobesity.common.eventManagerMain.MainViewModelEvent
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
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
    val profileManager = ProfileManager.getProfileManager()

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

    init {
        InTimeApp.appComponent.inject(this)

        medCard = MedCard()

        viewModelScope.launch(Dispatchers.IO) {

            val upd = updateLocal()
            upd.join()

            //val access = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhY2Nlc3MiOiJhY2Nlc3MiLCJleHAiOjE2MTg2MjEzNzYsImp0aSI6ImFmMDViMGY0ZDYyMzQ0MDk4ZDQ4ZDgwZTQ1YTI5NDQ0IiwidXNlcl9pZCI6IjcxNTgxOTFhLThkZDQtNGU0Mi1hNzBiLTQ1YWI3Mjk3NzcwMyJ9.RLIZ8HrZK-O7J4noQnrtDNUxFAmz9FPt-pqKDeIMZ_buwwwOl6er9pYfvdvgSheGdM6RSxfdFIey8nR3dV_OQvwQpy51efAoPlKPdR91cz0KvZOqE0RyGS-PeaG42AqH7eAwDRKdQIM3uhcmphIIaq0ErY3ji47Jo_kGhZig2XXE_lW6JGsYLTDQkORfzC8P-wByxlRJZtyjrNHX-qvZfSz_DCW1QLT4Fs0vzCXlpdnEKQu8rMhBrx0s9POwsFqDnY-Kfj1DB70GpfB4A5zokfZX3naRkrqPiKJ-zlKnFK89oDPo9Lk45OFv9AXjowh-9DUFPXkBGdd-wWga2HAlSNHzZwUvMFYS5C0IHZC8SzEXKijCYIXlCB5LvZghQ_iBzVopEU3hFOuhyqu1ATy4clFI7D_BqfaEbGSUjv4bvZDTPxlE6DTc-Kz0S58A8nppC4t4bi0110z5sYWuPw39dfrXe8cyRGo-OogUdnN3BSfDo0cCfun4SHDNbfxOAyJ-x6mp1r7y7yQDnI122WwXGyCltFgtKE2jQ7rQbZTlFkFonxgLqROAPBz3Ip_9V3XXO29uNQMsGF9JOrM-P_0sNeLTt_Sfxx6UgGqP7tBb5RHOYuAYACCnDrT3K0q1T-uR8zd27nSTRQeyhGoD1E0agSBDJFtXvGJVO3DpzkRZx9M"
            val access = "Bearer ${appPreference.accessToken}"

            setUpProfileManager(access).join()

            updateMedCard(access).join()
            updateResultCardServer(access, getCurrentLocale().locale).join()
            updateAnalyzeServer(access, getCurrentLocale().locale).join()
            updateRecommendationsServer(access, getCurrentLocale().locale).join()

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

    private suspend fun setUpProfileManager(access: String): Job{
        return viewModelScope.launch(Dispatchers.IO) {
            val currentMail = getCurrentMailAsync().await()

            val tempProfile = Profile(currentMail)
            val tempAvailableData = AvailableData(currentMail)

            loadProfile(access, tempProfile).join()
            loadUserProfile(access, tempProfile).join()
            updateProfile(tempProfile)

            profileManager.updateAvailableProfileData(tempProfile)
        }
    }

    private suspend fun updateProfile (item: Profile){
        val temp = paramSettingRepo.getProfileFromMail(item.email)
        if (temp == null){
            paramSettingRepo.insertProfile(item)
        } else {
            paramSettingRepo.updateProfile(item)
        }
        profileManager.setProfile(item)
    }

    private suspend fun loadProfile(access: String, profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        when (val resultProfile = remoteRepo.getProfile(access)){
            is RetrofitResult.Success -> {
                profile.loadFromProfile(resultProfile.value)
            }
            is RetrofitResult.Error -> {
                val i = 0
            }
        }
    }
    private suspend fun loadUserProfile(access: String, profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        when (val resultProfile = remoteRepo.getUserProfile(access)){
            is RetrofitResult.Success -> {
                profile.loadFromUserProfile(resultProfile.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }

    private fun test(){
        val i = 0
    }
}