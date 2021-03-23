package com.application.bmiobesity.model.db.commonSettings

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.application.bmiobesity.model.db.commonSettings.entities.*

@Database(
    entities = [Countries::class, Genders::class, Policy::class],
    version = 1000,
    exportSchema = false
)
abstract class CommonSettingDB : RoomDatabase(){

    abstract fun getCommonSettingDao(): CommonSettingDao

    companion object{
        @Volatile
        private var INSTANCE: CommonSettingDB? = null

        fun getCommonSettingDB(context: Context): CommonSettingDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CommonSettingDB::class.java,
                    "common_setting"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}