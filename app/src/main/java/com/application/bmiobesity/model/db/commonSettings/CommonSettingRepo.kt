package com.application.bmiobesity.model.db.commonSettings

import android.content.Context
import androidx.lifecycle.LiveData
import com.application.bmiobesity.model.db.commonSettings.entities.*

class CommonSettingRepo private constructor(context: Context){

    private val commonSettingDao = CommonSettingDB.getCommonSettingDB(context).getCommonSettingDao()

    suspend fun setAllGenders(items: List<Genders>){
        commonSettingDao.deleteAllGenders()
        commonSettingDao.insertAllGenders(items)
    }
    suspend fun getAllGenders(): List<Genders>{
        return commonSettingDao.getAllGenders()
    }

    suspend fun setAllCountries(items: List<Countries>){
        commonSettingDao.deleteAllCountries()
        commonSettingDao.insertAllCountries(items)
    }
    suspend fun getAllCountries(): List<Countries>{
        return commonSettingDao.getAllCountries()
    }
    suspend fun getCountriesByParam(param: String): List<Countries> {
        val param1 = "%$param%"
        return commonSettingDao.getCountriesByParam(param1)
    }

    suspend fun setPolicy(item: Policy){
        commonSettingDao.deleteAllPolicy()
        commonSettingDao.insertPolicy(item)
    }
    suspend fun getAllPolicy(): List<Policy>{
        return commonSettingDao.getAllPolicy()
    }
    suspend fun getPolicyLocale(locale: String): Policy{
        return commonSettingDao.getPolicyLocale(locale)
    }

    companion object{
        @Volatile
        private var INSTANCE: CommonSettingRepo? = null

        fun getCommonSettingRepo(context: Context): CommonSettingRepo{
            return INSTANCE ?: synchronized(this){
                val instance = CommonSettingRepo(context)
                INSTANCE = instance
                instance
            }
        }
    }
}