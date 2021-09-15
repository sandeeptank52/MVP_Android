package com.application.bmiobesity.viewModels

import androidx.lifecycle.*
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
import com.application.bmiobesity.common.parameters.AvailableParameters
import com.application.bmiobesity.services.google.billing.GoogleBillingClient
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

    val billingClient = GoogleBillingClient.getGoogleBilling(InTimeApp.APPLICATION)

    private val eventManager: MainViewModelEvent = EventManagerMain.getEventManager()
    private lateinit var appPreference: AppPreference
    val profileManager = ProfileManager.getProfileManager()

    // Common setting
    lateinit var genders: List<Genders>
    lateinit var countries: List<Countries>
    lateinit var policy: List<Policy>

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
    var medCard: MedCard
    lateinit var paramUnit: List<ParamUnit>
    lateinit var medCardSourceType: List<MedCardSourceType>
    lateinit var medCardParamSetting: List<MedCardParamSetting>
    // Selected tab index
    private val mSelectIndex: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val selectedIndex: LiveData<Int> = mSelectIndex

    init {
        InTimeApp.appComponent.inject(this)

        medCard = MedCard()

        viewModelScope.launch(Dispatchers.IO) {

            val updJob = updateLocal()
            val setUpProfileJob = setUpProfileManager()
            updJob.join()
            setUpProfileJob.join()

            val updateResultCardServerJob = updateResultCardServer(getCurrentLocale().locale)
            updateResultCardServerJob.join()

            eventManager.preloadSuccessEvent(true)

            val getMedCardJob = getMedCard()
            val updateAnalyzeServerJob = updateAnalyzeServer(getCurrentLocale().locale)
            val updateRecomServerJob = updateRecommendationsServer(getCurrentLocale().locale)

            getMedCardJob.join()
            updateAnalyzeServerJob.join()
            updateRecomServerJob.join()

            test()
        }
    }

    // Load parameters from DB
    private suspend fun updateGenders() = viewModelScope.launch(Dispatchers.IO) { genders =  commonSettingRepo.getAllGenders() }
    private suspend fun updateCountries() = viewModelScope.launch(Dispatchers.IO) { countries =  commonSettingRepo.getAllCountries() }
    private suspend fun updatePolicy() = viewModelScope.launch(Dispatchers.IO) { policy =  commonSettingRepo.getAllPolicy() }
    private suspend fun updateAppPreference() = viewModelScope.launch(Dispatchers.IO) { appPreference = appSetting.getAppPreference().first() }
    private suspend fun updateResultCardDB() = viewModelScope.launch(Dispatchers.IO) { mResultCard.postValue(paramSettingRepo.getAllResultCard()) }
    private suspend fun updateParamUnit() = viewModelScope.launch(Dispatchers.IO) { paramUnit = paramSettingRepo.getAllParamUnit() }
    private suspend fun updateMedCardSourceType() = viewModelScope.launch(Dispatchers.IO) { medCardSourceType = paramSettingRepo.getAllMedCardSourceType() }
    private suspend fun updateMedCardParamSetting() = viewModelScope.launch(Dispatchers.IO) {
        medCardParamSetting = paramSettingRepo.getAllMedCardParamSetting()
        medCardParamSetting.forEach {
            val simpleValues = paramSettingRepo.getValuesFromParamID(it.id)
            it.values = simpleValues as MutableList<MedCardParamSimpleValue>
        }
        medCard.setParameters(medCardParamSetting)
    }
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

    // Update result from server
    private suspend fun updateAllResult(){
        updateResultCardServer(getCurrentLocale().locale)
        updateAnalyzeServer(getCurrentLocale().locale)
        updateRecommendationsServer(getCurrentLocale().locale)
    }
    // Update favorite screen
    private suspend fun updateResultCardServer(locale: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = remoteRepo.getFavorites(locale)){
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
    private suspend fun updateAnalyzeServer(locale: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = remoteRepo.getResultAnalyze(locale)){
            is RetrofitResult.Success -> {
                mResultRiskAnalyze.postValue(result.value.disease_risk)
                mResultCommonRecommendations.postValue(result.value.common_recomendations)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    // Update personal recommendations
    private suspend fun updateRecommendationsServer(locale: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = remoteRepo.getRecommendations(locale)){
            is RetrofitResult.Success -> {
                mResultPersonalRecommendations.postValue(result.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    // Update Medical card
    private suspend fun getMedCard() = viewModelScope.launch(Dispatchers.IO) {
        when (val resultMedCard = remoteRepo.getMedCard()){
            is RetrofitResult.Success -> {
                medCard.setValues(resultMedCard.value)
                profileManager.updateAvailableMedCardData(resultMedCard.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    fun updateMedCard() = viewModelScope.launch(Dispatchers.IO) {
        /*val mCard = medCard.getResultMedCard()
        when (val resultMedCard = remoteRepo.updateMedCard(mCard)){
            is RetrofitResult.Success -> {
                //medCard.setValues(resultMedCard.value)
                medCard.successUpdate()
                profileManager.updateAvailableMedCardData(resultMedCard.value)
                updateAllResult()
            }
            is RetrofitResult.Error -> {
                medCard.errorUpdate()
            }
        }*/

        val mCard = medCard.getDashBoardMedCard()
        when (val result = remoteRepo.updateDashBoard(mCard)){
            is RetrofitResult.Success -> {
                //medCard.setValues(resultMedCard.value)
                medCard.successUpdate()
                profileManager.updateAvailableDashBoard(result.value)
                updateAllResult()
            }
            is RetrofitResult.Error -> {
                medCard.errorUpdate()
            }
        }
    }

    // Update User Profile
    private suspend fun setUpProfileManager(): Job{
        return viewModelScope.launch(Dispatchers.IO) {
            val currentMail = getCurrentMailAsync().await()

            val tempProfile = Profile(currentMail)

            val loadProfileJob = loadProfile(tempProfile)
            val loadUserProfileJob = loadUserProfile(tempProfile)
            val loadUserFirsTimeStampJob = loadFirsTimeStamp(tempProfile)
            loadProfileJob.join()
            loadUserProfileJob.join()
            loadUserFirsTimeStampJob.join()

            updateProfileDB(tempProfile)

            profileManager.setProfile(tempProfile)
            profileManager.updateAvailableProfileData(tempProfile)
        }
    }
    private suspend fun updateProfileDB (item: Profile){
        val temp = paramSettingRepo.getProfileFromMail(item.email)
        if (temp == null){
            paramSettingRepo.insertProfile(item)
        } else {
            paramSettingRepo.updateProfile(item)
        }
    }
    private suspend fun loadProfile(profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        when (val resultProfile = remoteRepo.getProfile()){
            is RetrofitResult.Success -> {
                profile.loadFromProfile(resultProfile.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    private suspend fun loadUserProfile(profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        when (val resultProfile = remoteRepo.getUserProfile()){
            is RetrofitResult.Success -> {
                profile.loadFromUserProfile(resultProfile.value)
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    private suspend fun loadFirsTimeStamp(profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = remoteRepo.getFirsTimeStamp()){
            is RetrofitResult.Success -> {
                val value = result.value.timestamp
                value?.let {
                    profile.firsTimeStamp = it.substringBefore('.').toLong()
                }
            }
            is RetrofitResult.Error -> {

            }
        }
    }
    // Patch Profile
    fun patchProfile(profile: Profile){
        // TODO: check for profile
          viewModelScope.launch(Dispatchers.IO) {
            when (val result = remoteRepo.patchProfile(profile = profile.getSendProfile())){
                is RetrofitResult.Success -> {
                    profile.loadFromProfile(result.value)
                    profileManager.setProfile(profile)
                    profileManager.updateAvailableProfileData(profile)
                    updateAllResult()
                }
                is RetrofitResult.Error -> {

                }
            }

            when (val result = remoteRepo.updateDashBoard(profile.getSendDashBoard())){
                is RetrofitResult.Success -> {
                    profile.loadFromDashBoard(result.value)
                    profileManager.setProfile(profile)
                    profileManager.updateAvailableProfileData(profile)
                    updateAllResult()
                }
                is RetrofitResult.Error -> {}
            }
            /*
            when (val result = remoteRepo.patchUserProfile(userProfile = profile.getSendUserProfile())){
                is RetrofitResult.Success -> {
                    profile.loadFromUserProfile(result.value)
                    profileManager.setProfile(profile)
                    profileManager.updateAvailableProfileData(profile)
                    updateAllResult()
                }
                is RetrofitResult.Error -> {

                }
            }*/

        }
    }
    // Delete profile
    fun deleteProfile(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = remoteRepo.deleteProfile()){
                is RetrofitResult.Success -> {
                    eventManager.startUserDeleting(true)
                    appSetting.clearAllData()
                    paramSettingRepo.clearDbToDeleteUser()
                    eventManager.endUserDeleting(true)
                }
                is RetrofitResult.Error -> {
                }
            }
        }
    }

    // Avatar
    fun patchAvatar(imageBase64: String){
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = remoteRepo.patchAvatar(UpdateResultAvatar(imageBase64))){
                is RetrofitResult.Success -> {
                    val imageUrl = result.value.image
                    imageUrl?.let {
                        profileManager.setAvatarUrl(it)
                    }
                }
                is RetrofitResult.Error -> {}
            }
        }
    }
    fun setTabIndex(index: Int) {
        mSelectIndex.postValue(index)
    }

    suspend fun isFirstTimeAsync(): Deferred<Boolean> = viewModelScope.async { appSetting.getBoolParam(AppSettingDataStore.PrefKeys.FIRST_TIME).first() }
    private suspend fun getCurrentMailAsync(): Deferred<String> = viewModelScope.async { appSetting.getStringParam(AppSettingDataStore.PrefKeys.USER_MAIL).first() }
    suspend fun setFirstTime(ft: Boolean): Job{
        return viewModelScope.launch(Dispatchers.IO) {
            appSetting.setBooleanParam(AppSettingDataStore.PrefKeys.FIRST_TIME, ft)
        }
    }

    private fun test(){
        val i = 0
    }
}