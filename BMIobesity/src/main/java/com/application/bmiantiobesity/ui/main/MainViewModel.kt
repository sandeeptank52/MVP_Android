package com.application.bmiantiobesity.ui.main

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.db.usersettings.ConfigDAO
import com.application.bmiantiobesity.db.usersettings.ConfigToDisplay
import com.application.bmiantiobesity.models.MeasuringSystem
import com.application.bmiantiobesity.models.SetNewDataValue
import com.application.bmiantiobesity.retrofit.*
import com.application.bmiantiobesity.utilits.getCurrentLocale
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel : ViewModel() {

    // Чтобы передавать между Activity
    companion object {
        //var liveDashBoard = MutableLiveData<DashBoard>()

        var userToken: ResultToken = ResultToken("", "")
        // Система исчисления

        const val MEASURING_SYSTEM = "MeasuringSystem"
        var measuringSystem = MeasuringSystem.SI

        var publishNewValueFromEvent: PublishSubject<SetNewDataValue> = PublishSubject.create()

        var singleProfile: Profile? = null
        var singleGenders: Genders? = null

        var singleTimeOfFirstResult: Long? = null
        var singleCountries: Countries? = null

        var needToShowRecommendation = true
        //var singleMainData: MutableList<MainResult>? = null

        val updateSingleResult = MutableLiveData<Result>()
        val updateRecomendations = MutableLiveData<MutableList<CommonRecommendationsAdapter>>()
        val mediatorLiveData = MediatorLiveData<MutableList<CommonRecommendationsAdapter>>()
        val updateProfile = MutableLiveData<Profile>()
        val isSkeletonLoading = MutableLiveData<Boolean>()
        val isNeedToShowEditDialog = MutableLiveData<Boolean?>()
        val updateDataAdapter = MutableLiveData<MainRiskSetAdapter>()
        val updateCommonRecommendationsAdapter = MutableLiveData<CommonRecommendationsSetAdapter>()
        val updateSwipeRefresh = MutableLiveData<Boolean>()

        var singleDashBoard: DashBoard? = null
        val changeDashBoard: PublishSubject<DashBoard> = PublishSubject.create()
        val publishSetDashboard: PublishSubject<DashBoard> = PublishSubject.create()
        var isUpdateDashboard = false

        var singleMedCard: MedCard? = null
        val changeMedCard: PublishSubject<MedCard> = PublishSubject.create()
        val publishSetMedCard: PublishSubject<MedCard> = PublishSubject.create()
        var isUpdateMedCard = false

        private var singleSettings: List<ConfigToDisplay>? = null

        // Значения от Google Fit
        var accessToGoogleFitApi = MutableLiveData<Boolean>()
        //var bloodPressureDiastolic = MutableLiveData<Int>()
        var steps = MutableLiveData<Int>()

    }

    //Данные
    var intData = MutableLiveData<Int>()

    @Inject
    lateinit var inTimeDigital: InTimeDigitalApi
    @Inject
    lateinit var dbConfig: ConfigDAO

    init {
        //Dagger2
        InTimeApplication.component?.injectToViewModel(this)
    }

    //Settings
    fun getUserSettings(activity: FragmentActivity): LiveData<List<ConfigToDisplay>>{
        val result = MutableLiveData<List<ConfigToDisplay>>()
        if (singleSettings != null) result.value = singleSettings
        else dbConfig.getAll.observe(activity, Observer<List<ConfigToDisplay>> {
                if (it.isNotEmpty()) {
                    result.value = it
                    singleSettings = it
                }
                else{
                    val firstConfig = listOf(ConfigToDisplay(TypeOfInformation.COMMON_RISK_LEVEL, activity.getString(R.string.common_risk_level), true),
                        ConfigToDisplay(TypeOfInformation.BMI, activity.getString(R.string.bmi), true),
                        ConfigToDisplay(TypeOfInformation.OBESITY_LEVEL, activity.getString(R.string.obesity_level), true),
                        ConfigToDisplay(TypeOfInformation.IDEAL_WEIGHT, activity.getString(R.string.ideal_weight), true),
                        ConfigToDisplay(TypeOfInformation.BASE_METABOLISM, activity.getString(R.string.base_metabolism), true),
                        ConfigToDisplay(TypeOfInformation.CALORIES_TO_LOW_WEIGHT, activity.getString(R.string.calories_to_low_weight), true),
                        ConfigToDisplay(TypeOfInformation.WAIST_TO_HIP_PROPORTIONS, activity.getString(R.string.waist_to_hip_proportion), true),
                        ConfigToDisplay(TypeOfInformation.BIO_AGE, activity.getString(R.string.bio_age), true),
                        ConfigToDisplay(TypeOfInformation.PROGNOSTIC_AGE, activity.getString(R.string.progrostic_age), true),
                        ConfigToDisplay(TypeOfInformation.FAT_PERCENT, activity.getString(R.string.fat_percent), true),
                        ConfigToDisplay(TypeOfInformation.BODY_TYPE, activity.getString(R.string.body_type), true)
                    )
                    result.value = firstConfig

                    insertConfigToDBAll(firstConfig)
                }
            })
        return result
    }

    private fun insertConfigToDBAll(configs:List<ConfigToDisplay>) = viewModelScope.launch(Dispatchers.IO) {
        dbConfig.insertAll(configs)
    }

    fun updateConfigToDBAll(configs:List<ConfigToDisplay>) = viewModelScope.launch(Dispatchers.IO) {
        dbConfig.updateAll(configs)
    }


    // Genders
    fun getGenders(locale: Locale) = if (singleGenders != null) Observable.just(singleGenders)
    else inTimeDigital.getGenders(locale).doOnNext { singleGenders = it }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    // Countries
    fun getCountries(bodyCountries: BodyCountries) = if (singleCountries != null) Observable.just(singleCountries)
    else inTimeDigital.getCountries(bodyCountries).doOnNext { singleCountries = it }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


    // Profile
    fun setModelProfile(profile: Profile) {
        singleProfile = profile
        updateProfile.value = profile
    }

    fun getProfileFromInternet() = if (singleProfile != null) Observable.just(singleProfile)
    else inTimeDigital.getProfile("Bearer ${userToken.access}").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext { setModelProfile(it) } // doOnNext здесь из-за LiveData

    fun setProfileToInternet(profile: Profile?) =
        if (profile == null) Observable.error<Profile>(ExceptionInInitializerError("Profile not initialize!"))
        else inTimeDigital.setProfile("Bearer ${userToken.access}", profile).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext { setModelProfile(it) } // doOnNext здесь из-за LiveData


    //Dashboard
    private fun setModelDashBoard(dashBoard: DashBoard) {
        singleDashBoard = dashBoard
    }

    fun getDashBoardFromInternet() = if (singleDashBoard != null) Observable.just(singleDashBoard)
    else inTimeDigital.getDashBoard("Bearer ${userToken.access}").doOnNext { setModelDashBoard(it) }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun setDashBoardToInternet(dashBoard: DashBoard) {
        publishSetDashboard.onNext(dashBoard) //inTimeDigital.updateDashBoard("Bearer ${userToken.access}", dashBoard)
    }

    // должен быть один подписчик
    fun  getSubcriberToSetDashBoard() = publishSetDashboard
        .doOnNext {
            isUpdateDashboard = true
            setModelDashBoard(it)
            setChangeDashBoard(it) // Обновляем поля у пользователя сразу
        }
        .debounce(1, TimeUnit.SECONDS) // Задержка отправки данных
        //.distinctUntilChanged() // Отбрасывал лишние значения
        .switchMap { inTimeDigital.updateDashBoard("Bearer ${userToken.access}", it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError { isUpdateDashboard = false }
        .doOnNext { isUpdateDashboard = false }

    // Рассылка изменённых дашбоардов
    fun setChangeDashBoard(dashBoard: DashBoard) { changeDashBoard.onNext(dashBoard)}

    fun getChangeDashBoard() = changeDashBoard.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


    //MedCard
    private fun setModelMedCard(medCard: MedCard) {
        singleMedCard = medCard
    }

    fun getMedCardFromInternet() = if (singleMedCard != null) Observable.just(singleMedCard)
    else inTimeDigital.getMedCard("Bearer ${userToken.access}").doOnNext { setModelMedCard(it) }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun setMedCardToInternet(medCard: MedCard) {
        publishSetMedCard.onNext(medCard) //inTimeDigital.updateDashBoard("Bearer ${userToken.access}", dashBoard)
    }

    // должен быть один подписчик
    fun  getSubcriberToSetMedCard() = publishSetMedCard
        .doOnNext {
            isUpdateDashboard = true
            setModelMedCard(it)
            setChangeMedCard(it) // Обновляем поля у пользователя сразу
        }
        .debounce(1, TimeUnit.SECONDS) // Задержка отправки данных
        //.distinctUntilChanged() // Отбрасывал лишние значения
        .switchMap { inTimeDigital.updateMedCard("Bearer ${userToken.access}", it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError { isUpdateDashboard = false }
        .doOnNext { isUpdateDashboard = false }

    // Рассылка изменённых дашбоардов
    fun setChangeMedCard(medCard: MedCard) { changeMedCard.onNext(medCard)}

    fun getChangeMedCard() = changeMedCard.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


    //Result
    /*fun getResult(): Observable<Result> = when{
        (updateSingleResult != null)&&(updateSingleResult?.unfilled == null) -> Observable.just(updateSingleResult)
        else -> getResultFromInternet()
    }*/

    //private fun setResult(result: Result) { updateSingleResult.value = result }

    fun getResultFromInternet(locale: String) = inTimeDigital.getResult("Bearer ${userToken.access}", locale).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun getRecomendationsFromInternet(locale: String) = inTimeDigital
        .getRecomedations("Bearer ${userToken.access}", locale)
        .subscribeOn(Schedulers.io())
        .doOnNext { sleep(1000) }
        .flatMap {  Observable.fromIterable(it) }
        .map { CommonRecommendationsAdapter(TypeOfAdapter.DATA, null, it.name, null ) }
        .toList()
        .observeOn(AndroidSchedulers.mainThread())

    // Получение времени первого рассчета
    fun getTimeOfFirstResultFromInternet() = if (singleTimeOfFirstResult != null) Observable.just(GetFirstResultTime(singleTimeOfFirstResult.toString()))
        else inTimeDigital.getFirstResultTime("Bearer ${userToken.access}").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    /*fun testTime() = Observable.just("12312312a")
        .map { try { it.substringBefore('.')?.toLong() }
               catch (ex:Exception){ null }}*/

    //Обновление токена
    fun refreshToken(refresh: SendRefresh) = inTimeDigital.refreshUserToken(refresh)
    fun showEditDialog() {
        isNeedToShowEditDialog.postValue(true)
    }

    fun setShowFalse(){
        isNeedToShowEditDialog.postValue(false)
    }
}