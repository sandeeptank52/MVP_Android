package com.application.bmiobesity.model.db.paramSettings

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.application.bmiobesity.model.db.paramSettings.entities.*
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile

@Database(
    entities = [MedCardParamSetting::class,
        MedCardSourceType::class,
        ParamUnit::class,
        ResultCard::class,
        Profile::class],
    version = 1000,
    exportSchema = true
)
abstract class ParamSettingsDB : RoomDatabase() {

    abstract fun getParamSettingDao(): ParamSettingDAO

    companion object {
        @Volatile
        private var INSTANCE: ParamSettingsDB? = null

        fun getDataBase(context: Context): ParamSettingsDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParamSettingsDB::class.java,
                    "param_settings"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}