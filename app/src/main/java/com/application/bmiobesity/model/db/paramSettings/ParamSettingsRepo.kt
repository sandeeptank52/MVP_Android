package com.application.bmiobesity.model.db.paramSettings

import android.content.Context
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.model.db.paramSettings.entities.ParamUnit
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard

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