package com.application.bmiobesity.model.db.paramSettings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.model.db.paramSettings.entities.ParamUnit
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard

@Dao
interface ParamSettingDAO {

    @Query("SELECT * FROM result_card")
    suspend fun getAllFromResultCard(): List<ResultCard>
    @Query("SELECT * FROM param_unit")
    suspend fun getAllFromParamUnit(): List<ParamUnit>
    @Query("SELECT * FROM medcard_source_type")
    suspend fun getAllFromMedCardSourceType(): List<MedCardSourceType>
    @Query("SELECT * FROM medcard_param_setting")
    suspend fun getAllFromMedCardParamSetting(): List<MedCardParamSetting>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllResultCard(items: List<ResultCard>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllParamUnit(items: List<ParamUnit>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMedCardSourceType(items: List<MedCardSourceType>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMedCardParamSetting(items: List<MedCardParamSetting>)

    @Query("DELETE FROM result_card")
    fun deleteAllFromResultCard()
    @Query("DELETE FROM param_unit")
    fun deleteAllFromParamUnit()
    @Query("DELETE FROM medcard_source_type")
    fun deleteAllFromMedCardSourceType()
    @Query("DELETE FROM medcard_param_setting")
    fun deleteAllFromMedCardParamSetting()
}