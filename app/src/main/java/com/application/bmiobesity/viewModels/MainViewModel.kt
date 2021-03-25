package com.application.bmiobesity.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.model.appSettings.AppPreference
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.model.db.commonSettings.CommonSettingRepo
import com.application.bmiobesity.model.db.paramSettings.ParamSettingsRepo
import com.application.bmiobesity.model.localStorage.LocalStorageRepo
import com.application.bmiobesity.model.retrofit.RemoteRepo
import com.application.bmiobesity.model.retrofit.RetrofitResult
import com.application.bmiobesity.viewModels.eventManagerMain.EventManagerMain
import com.application.bmiobesity.viewModels.eventManagerMain.MainViewModelEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

    private lateinit var appPreference: AppPreference
    private val eventManager: MainViewModelEvent = EventManagerMain.getEventManager()

    init {
        InTimeApp.appComponent.inject(this)

        viewModelScope.launch {
            val getAppPreferenceDeferred = async { getAppPreference() }
            appPreference = getAppPreferenceDeferred.await()

            when (val resultProfile = remoteRepo.getProfile("Bearer ${appPreference.accessToken}")){
                is RetrofitResult.Success -> {

                }
                is RetrofitResult.Error -> {

                }
            }


            eventManager.preloadSuccessEvent(true)
        }
    }

    private suspend fun getAppPreference(): AppPreference = appSetting.getAppPreference().first()
}