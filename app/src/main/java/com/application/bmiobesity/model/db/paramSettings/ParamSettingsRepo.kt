package com.application.bmiobesity.model.db.paramSettings

import android.content.Context
import androidx.lifecycle.LiveData
import com.application.bmiobesity.model.db.paramSettings.entities.*
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile

class ParamSettingsRepo private constructor(context: Context){

    private val paramSettingDAO = ParamSettingsDB.getDataBase(context).getParamSettingDao()

    suspend fun insertAllResultCard(items: List<ResultCard>){
        paramSettingDAO.deleteAllFromResultCard()
        paramSettingDAO.insertAllResultCard(items)
    }

    suspend fun insertAllParamUnit(items: List<ParamUnit>){
        paramSettingDAO.deleteAllFromParamUnit()
        paramSettingDAO.insertAllParamUnit(items)
    }

    suspend fun insertAllMedCardSourceType(items: List<MedCardSourceType>){
        paramSettingDAO.deleteAllFromMedCardSourceType()
        paramSettingDAO.insertAllMedCardSourceType(items)
    }

    suspend fun insertAllMedCardParamSetting(items: List<MedCardParamSetting>){
        paramSettingDAO.deleteAllFromMedCardParamSetting()
        paramSettingDAO.insertAllMedCardParamSetting(items)
    }

    suspend fun updateParamSetting(item: MedCardParamSetting) = paramSettingDAO.updateMedCardParamSetting(item)

    suspend fun getAllResultCard() = paramSettingDAO.getAllFromResultCard()
    suspend fun getAllParamUnit() = paramSettingDAO.getAllFromParamUnit()
    suspend fun getAllMedCardSourceType() = paramSettingDAO.getAllFromMedCardSourceType()
    suspend fun getAllMedCardParamSetting() = paramSettingDAO.getAllFromMedCardParamSetting()

    // Profile
    suspend fun getProfileFromMail(mail: String): Profile = paramSettingDAO.getProfileFromMail(mail)
    fun getProfileFromMailLive(mail: String): LiveData<Profile> = paramSettingDAO.getProfileFromMailLive(mail)
    //suspend fun getAvailableDataFromMail(mail: String): AvailableData = paramSettingDAO.getAvailableData(mail)
    //suspend fun getOnBoardingStepFromMail(mail: String): OnBoardingSteps = paramSettingDAO.getOnBoardStepsFromMail(mail)

    suspend fun insertProfile(item: Profile) = paramSettingDAO.insertProfile(item)
    //suspend fun insertAvailableData(item: AvailableData) = paramSettingDAO.insertAvailableData(item)
    //suspend fun insertOnBoardingStep(item: OnBoardingSteps) = paramSettingDAO.insertOnBoardingStep(item)

    suspend fun updateProfile(item: Profile) = paramSettingDAO.updateProfile(item)

    suspend fun getAllSimpleValues(): List<MedCardParamSimpleValue> = paramSettingDAO.getAllSimpleValues()
    suspend fun insertSimpleValue(item: MedCardParamSimpleValue) = paramSettingDAO.insertSimpleValue(item)
    suspend fun getValuesFromParamID(paramID: String) = paramSettingDAO.getValuesFromParamID(paramID)
    suspend fun insertSimpleValues(items: List<MedCardParamSimpleValue>) = paramSettingDAO.insertSimpleValues(items)
    suspend fun updateSimpleValue(item: MedCardParamSimpleValue) = paramSettingDAO.updateSimpleValue(item)

    suspend fun clearDbToDeleteUser(){
        paramSettingDAO.deleteAllFromMedCardParamSetting()
        paramSettingDAO.deleteAllFromSimpleValue()
        paramSettingDAO.deleteAllFromProfile()
        paramSettingDAO.deleteAllFromResultCard()
    }

    companion object{
        @Volatile
        private var INSTANCE: ParamSettingsRepo? = null

        fun getRepo(context: Context): ParamSettingsRepo{
            return INSTANCE ?: synchronized(this){
                val instance = ParamSettingsRepo(context)
                INSTANCE = instance
                instance
            }
        }
    }
}