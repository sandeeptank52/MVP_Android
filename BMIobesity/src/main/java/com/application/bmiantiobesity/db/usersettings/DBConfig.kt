package com.application.bmiantiobesity.db.usersettings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.application.bmiantiobesity.ui.main.TypeOfInformation

@Entity
data class ConfigToDisplay(@PrimaryKey @TypeConverters(TypeOfInformationConverter::class) var type: TypeOfInformation,
                           var name: String,
                           var value: Boolean)

@Dao
interface ConfigDAO {
    @get:Query("SELECT * FROM configtodisplay")
    val getAll: LiveData<List<ConfigToDisplay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(config: ConfigToDisplay)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(configs: List<ConfigToDisplay>)

    @Update
    fun update(config: ConfigToDisplay)

    @Update
    fun updateAll(configs: List<ConfigToDisplay>)

    @Delete
    fun delete(config: ConfigToDisplay)
}

@Database(entities = [ConfigToDisplay::class], version = 1,  exportSchema = false)
@TypeConverters(TypeOfInformationConverter::class)
abstract class DataBase : RoomDatabase() {
    abstract fun configDao(): ConfigDAO
}

object DBConfig{
    private lateinit var myDataBase: DataBase

    fun getInstance(applicationContext: Context, dbName: String): DataBase {
        myDataBase = Room.databaseBuilder(applicationContext, DataBase::class.java, dbName)
            .allowMainThreadQueries()
            .build()
        return myDataBase
    }
}

class TypeOfInformationConverter{
    @TypeConverter
    fun toInt(type: TypeOfInformation): Int = when (type){
        TypeOfInformation.COMMON_RISK_LEVEL -> 1
        TypeOfInformation.BMI -> 2
        TypeOfInformation.OBESITY_LEVEL -> 3
        TypeOfInformation.IDEAL_WEIGHT -> 4
        TypeOfInformation.BASE_METABOLISM -> 5
        TypeOfInformation.CALORIES_TO_LOW_WEIGHT -> 6
        TypeOfInformation.WAIST_TO_HIP_PROPORTIONS -> 7
        TypeOfInformation.BIO_AGE -> 8
        TypeOfInformation.PROGNOSTIC_AGE -> 9
        TypeOfInformation.FAT_PERCENT -> 10
        TypeOfInformation.BODY_TYPE -> 11
        TypeOfInformation.ERROR -> 100
        TypeOfInformation.UNFILLED -> 101
        TypeOfInformation.UNFILLED_BUTTON -> 102
    }

    @TypeConverter
    fun toTypeOfInformation(int: Int): TypeOfInformation = when (int){
        1 -> TypeOfInformation.COMMON_RISK_LEVEL
        2 -> TypeOfInformation.BMI
        3 -> TypeOfInformation.OBESITY_LEVEL
        4 -> TypeOfInformation.IDEAL_WEIGHT
        5 -> TypeOfInformation.BASE_METABOLISM
        6 -> TypeOfInformation.CALORIES_TO_LOW_WEIGHT
        7 -> TypeOfInformation.WAIST_TO_HIP_PROPORTIONS
        8 -> TypeOfInformation.BIO_AGE
        9 -> TypeOfInformation.PROGNOSTIC_AGE
        10 -> TypeOfInformation.FAT_PERCENT
        11 -> TypeOfInformation.BODY_TYPE
        100 -> TypeOfInformation.ERROR
        101 -> TypeOfInformation.UNFILLED
        102 -> TypeOfInformation.UNFILLED_BUTTON
        else -> TypeOfInformation.ERROR
    }
}