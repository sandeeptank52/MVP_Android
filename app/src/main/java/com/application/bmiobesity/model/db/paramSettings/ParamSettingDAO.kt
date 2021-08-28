package com.application.bmiobesity.model.db.paramSettings

import androidx.lifecycle.LiveData
import androidx.room.*
import com.application.bmiobesity.model.db.paramSettings.entities.*
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile

@Dao
interface ParamSettingDAO {

    // Result card
    @Query("SELECT * FROM result_card")
    suspend fun getAllFromResultCard(): List<ResultCard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllResultCard(items: List<ResultCard>)

    @Query("DELETE FROM result_card")
    fun deleteAllFromResultCard()

    // Param unit
    @Query("SELECT * FROM param_unit")
    suspend fun getAllFromParamUnit(): List<ParamUnit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllParamUnit(items: List<ParamUnit>)

    @Query("DELETE FROM param_unit")
    fun deleteAllFromParamUnit()

    // Source Type
    @Query("SELECT * FROM medcard_source_type")
    suspend fun getAllFromMedCardSourceType(): List<MedCardSourceType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMedCardSourceType(items: List<MedCardSourceType>)

    @Query("DELETE FROM medcard_source_type")
    fun deleteAllFromMedCardSourceType()

    // Param Setting
    @Query("SELECT * FROM medcard_param_setting")
    suspend fun getAllFromMedCardParamSetting(): List<MedCardParamSetting>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMedCardParamSetting(items: List<MedCardParamSetting>)

    @Update
    suspend fun updateMedCardParamSetting(item: MedCardParamSetting)

    @Query("DELETE FROM medcard_param_setting")
    suspend fun deleteAllFromMedCardParamSetting()

    // Simple Value
    @Query("SELECT * FROM medcard_param_simple_values")
    suspend fun getAllSimpleValues(): List<MedCardParamSimpleValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimpleValue(item: MedCardParamSimpleValue)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimpleValues(items: List<MedCardParamSimpleValue>)

    @Update
    suspend fun updateSimpleValue(item: MedCardParamSimpleValue)

    @Query("SELECT * FROM medcard_param_simple_values WHERE param_id = :paramID")
    suspend fun getValuesFromParamID(paramID: String): List<MedCardParamSimpleValue>

    @Query("DELETE FROM medcard_param_simple_values")
    suspend fun deleteAllFromSimpleValue()

    // Profile
    @Query("SELECT * FROM profile")
    suspend fun getAllProfile(): List<Profile>
    /*@Query("SELECT * FROM on_boarding_steps")
    suspend fun getAllOnBoardingSteps(): List<OnBoardingSteps>
    @Query("SELECT * FROM available_data")
    suspend fun getAllAvailableData(): List<AvailableData>*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(item: Profile)
    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnBoardingStep(item: OnBoardingSteps)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailableData(item: AvailableData)*/

    @Update
    suspend fun updateProfile(item: Profile)
    /*@Update
    suspend fun updateOnBoardingStep(item: OnBoardingSteps)
    @Update
    suspend fun updateAvailableData(item: AvailableData)*/

    @Query("SELECT * FROM profile WHERE email = :mail")
    suspend fun getProfileFromMail(mail: String): Profile

    @Query("SELECT * FROM profile WHERE email = :mail")
    fun getProfileFromMailLive(mail: String): LiveData<Profile>
    /*@Query("SELECT * FROM on_boarding_steps WHERE email = :mail")
    suspend fun getOnBoardStepsFromMail(mail: String): OnBoardingSteps
    @Query("SELECT * FROM available_data WHERE email = :mail")
    suspend fun getAvailableData(mail: String): AvailableData*/

    @Query("DELETE FROM profile")
    suspend fun deleteAllFromProfile()
}