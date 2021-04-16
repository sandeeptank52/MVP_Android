package com.application.bmiobesity.model.db.paramSettings

import android.content.Context
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.model.db.paramSettings.entities.ParamUnit
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.OnBoardingSteps
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

    suspend fun getAllResultCard() = paramSettingDAO.getAllFromResultCard()
    suspend fun getAllParamUnit() = paramSettingDAO.getAllFromParamUnit()
    suspend fun getAllMedCardSourceType() = paramSettingDAO.getAllFromMedCardSourceType()
    suspend fun getAllMedCardParamSetting() = paramSettingDAO.getAllFromMedCardParamSetting()

    // Profile
    suspend fun getProfileFromMail(mail: String): Profile = paramSettingDAO.getProfileFromMail(mail)
    suspend fun getAvailableDataFromMail(mail: String): AvailableData = paramSettingDAO.getAvailableData(mail)
    suspend fun getOnBoardingStepFromMail(mail: String): OnBoardingSteps = paramSettingDAO.getOnBoardStepsFromMail(mail)

    suspend fun insertProfile(item: Profile) = paramSettingDAO.insertProfile(item)
    suspend fun insertAvailableData(item: AvailableData) = paramSettingDAO.insertAvailableData(item)
    suspend fun insertOnBoardingStep(item: OnBoardingSteps) = paramSettingDAO.insertOnBoardingStep(item)

    suspend fun updateProfile(item: Profile) = paramSettingDAO.updateProfile(item)

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