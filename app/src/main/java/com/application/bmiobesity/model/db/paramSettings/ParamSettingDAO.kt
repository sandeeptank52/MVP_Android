package com.application.bmiobesity.model.db.paramSettings

import androidx.lifecycle.LiveData
import androidx.room.*
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.model.db.paramSettings.entities.ParamUnit
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.OnBoardingSteps
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile

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

    // Profile
    @Query("SELECT * FROM profile")
    suspend fun getAllProfile(): List<Profile>
    @Query("SELECT * FROM on_boarding_steps")
    suspend fun getAllOnBoardingSteps(): List<OnBoardingSteps>
    @Query("SELECT * FROM available_data")
    suspend fun getAllAvailableData(): List<AvailableData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(item: Profile)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnBoardingStep(item: OnBoardingSteps)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailableData(item: AvailableData)

    @Update
    suspend fun updateProfile(item: Profile)
    @Update
    suspend fun updateOnBoardingStep(item: OnBoardingSteps)
    @Update
    suspend fun updateAvailableData(item: AvailableData)

    @Query("SELECT * FROM profile WHERE email = :mail")
    suspend fun getProfileFromMail(mail: String): Profile
    @Query("SELECT * FROM profile WHERE email = :mail")
    fun getProfileFromMailLive(mail: String): LiveData<Profile>
    @Query("SELECT * FROM on_boarding_steps WHERE email = :mail")
    suspend fun getOnBoardStepsFromMail(mail: String): OnBoardingSteps
    @Query("SELECT * FROM available_data WHERE email = :mail")
    suspend fun getAvailableData(mail: String): AvailableData
}