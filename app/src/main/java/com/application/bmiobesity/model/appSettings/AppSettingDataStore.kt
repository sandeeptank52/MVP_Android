package com.application.bmiobesity.model.appSettings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class AppSettingDataStore private constructor(context: Context){

    private val dataStore: DataStore<Preferences>
    private val cryptoApi: CryptoApi

    init {
        dataStore = context.createDataStore(PrefKeys.USER_SETTINGS)
        cryptoApi = CryptoApi.getCryptoApi()
    }

    object PrefKeys{
        const val USER_SETTINGS = "user_settings"

        val FIRST_TIME = booleanPreferencesKey("first_time")
        val SHOW_DISCLAIMER = booleanPreferencesKey("show_disclaimer")

        val USER_MAIL = stringPreferencesKey("user_mail")
        val USER_PASS = stringPreferencesKey("user_pass")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val DEVICE_UUID = stringPreferencesKey("device_uuid")
    }

    object Constants{
        const val OS_NAME = "Android OS"
    }

    suspend fun setStringParam(idParam: Preferences.Key<String>, value: String) = dataStore.edit { it[idParam] = cryptoApi.encryptString(value) }
    suspend fun setBooleanParam(idParam: Preferences.Key<Boolean>, value: Boolean) = dataStore.edit { it[idParam] = value }

    suspend fun getStringParam(idParam: Preferences.Key<String>): Flow<String>{
        return dataStore.data
            .catch{
                if (it is IOException){
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                val result = it[idParam] ?: ""
                cryptoApi.decryptString(result)
            }
    }

    suspend fun getBoolParam(idParam: Preferences.Key<Boolean>): Flow<Boolean>{
        return dataStore.data
            .catch {
                if (it is IOException){
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[idParam] ?: false
            }
    }

    fun getAppPreference(): Flow<AppPreference>{
        return dataStore.data
            .catch {
                if (it is IOException){
                    it.printStackTrace()
                } else {
                    throw it
                }
            }.map {
                AppPreference(
                    it[PrefKeys.FIRST_TIME] ?: true,
                    it[PrefKeys.SHOW_DISCLAIMER] ?: true,
                    cryptoApi.decryptString(it[PrefKeys.REFRESH_TOKEN]),
                    cryptoApi.decryptString(it[PrefKeys.ACCESS_TOKEN]),
                    cryptoApi.decryptString(it[PrefKeys.DEVICE_UUID])
                )
            }
    }


    companion object {
        @Volatile
        var INSTANCE: AppSettingDataStore? = null

        fun getSettingApi(context: Context): AppSettingDataStore{
            return INSTANCE ?: synchronized(this){
                val instance = AppSettingDataStore(context)
                INSTANCE = instance
                instance
            }
        }
    }
}