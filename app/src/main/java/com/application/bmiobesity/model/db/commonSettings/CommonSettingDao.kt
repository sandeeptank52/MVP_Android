package com.application.bmiobesity.model.db.commonSettings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import com.application.bmiobesity.model.db.commonSettings.entities.Genders
import com.application.bmiobesity.model.db.commonSettings.entities.Policy

@Dao
interface CommonSettingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGenders(items: List<Genders>)
    @Query("SELECT * FROM genders")
    fun getAllGenders(): List<Genders>
    @Query("DELETE FROM genders")
    fun deleteAllGenders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCountries(items: List<Countries>)
    @Query("SELECT * FROM countries ORDER BY value ASC")
    fun getAllCountries(): List<Countries>
    @Query("DELETE FROM countries")
    fun deleteAllCountries()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPolicy(item: Policy)
    @Query("SELECT * FROM policy")
    fun getAllPolicy(): List<Policy>
    @Query("SELECT * FROM policy WHERE id = :locale")
    fun getPolicyLocale(locale: String): Policy
    @Query("DELETE FROM policy")
    fun deleteAllPolicy()
}