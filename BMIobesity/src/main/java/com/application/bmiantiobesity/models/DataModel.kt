package com.application.bmiantiobesity.models

import android.content.Context
import com.application.bmiantiobesity.retrofit.DashBoard
import com.application.bmiantiobesity.retrofit.LoadData
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.retrofit.MedCard
import com.application.bmiantiobesity.ui.main.MainViewModel
import com.application.bmiantiobesity.utilits.roundTo
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class SetNewDataValue(val type: DataTypeInTime, val value:String, val dataTime: Long?)

enum class DataTypeInTime{
    HEIGHT, AGE, NATIONALITY, COUNTRY, WEIGHT, GROWTH, BODY_WEIGHT, HIP, WAIST, NECK_CIRCUMFERENCE, WRIST, BLOOD_PRESSURE_DIA, BLOOD_PRESSURE,
    BLOOD_PRESSURE_SYS, HEARTS_RATE_ALONE, HEARTS_RATE_VARIABILITY, VO2MAX, RAFFER_DIXON_INDEX, BALANCING_TEST_ON_ONE_LEG,
    CHOLESTEROL, REACTION_TEST, NECK, DAILY_ACTIVITY_LEVEL, DISEASES, BIRTH_DATE, PRESCRIBED_MEDICINES, MOVEMENT_AND_SLEEP_PATTERNS, MOOD_AND_MENTAL_PERFORMANCE,
    MEASURING_SYSTEM, LIBIDO, BODY_TEMPERATURE, LUNG_FUNCTION, GLUCOSE, THE_POWER_OF_BRUSH, STANGE_AND_GENCHI_TEST, SMOKER, GENDER,
    HIGH_FREQUENCY_AUDITORY_TEST, VISUAL_ACUITY_CHECK, VARIOUS_ACTIVE_MOTION_TESTS, ORTHOSTATIC_BLOOD_PRESSURE_RECOVERY_TEST,
    ECG, EEG, PWV, A_TEST_TO_GET_UP_FROM_THE_FLOOR_WITHOUT_THE_HELP_HANDS, BREATHING_DELAY_TIME_AFTER_DEEP_EXHALATHION,
    FLEXIBILITY_TEST, TEMPERATURE, HUMIDITY, ILLUMINATION, ELECTRO_MAGNETIC_FIELDS, IONIZING_RADIATION, SEPARATOR
}

fun dataTypeConverter(data:String, dataTypeInTime: DataTypeInTime):Any =
    when (dataTypeInTime){
        DataTypeInTime.HEIGHT, DataTypeInTime.NATIONALITY, DataTypeInTime.BALANCING_TEST_ON_ONE_LEG -> data

        DataTypeInTime.AGE -> try {
            val dsf = SimpleDateFormat("dd-mm-yyyy")
            dsf.parse(data)
        } catch (e:Exception) { Date()}

        DataTypeInTime.GROWTH, DataTypeInTime.BODY_WEIGHT, DataTypeInTime.HIP, DataTypeInTime.WAIST, DataTypeInTime.NECK_CIRCUMFERENCE,
        DataTypeInTime.WRIST, DataTypeInTime.BODY_TEMPERATURE -> try { data.toFloat() } catch (e: Exception ) { 0f }

        DataTypeInTime.BLOOD_PRESSURE_SYS, DataTypeInTime.HEARTS_RATE_VARIABILITY, DataTypeInTime.HEARTS_RATE_ALONE, DataTypeInTime.VO2MAX,
        DataTypeInTime.RAFFER_DIXON_INDEX, DataTypeInTime.REACTION_TEST -> try { data.toInt() } catch (e: Exception ) { 0 }

        else -> data
    }

enum class MeasuringSystem { SI, IMPERIAL ;
    fun toInt() = when (this) {
        SI -> 1
        IMPERIAL -> 2
    }

    companion object {
        fun fromInt(int: Int) = when (int) {
            1 -> SI
            2 -> IMPERIAL
            else -> SI
        }
    }
}

// Конвертеры величин
fun converterSmToIn(smValue:Float?) = if (smValue != null) smValue / 2.54f else null
fun converterInToSm(ftValue:Float?) = if (ftValue != null) ftValue * 2.54f else null

fun converterKgToLb(kgValue:Float?) = if (kgValue != null) kgValue / 0.45359237f else null
fun converterLbToKg(lbValue:Float?) = if (lbValue != null) lbValue * 0.45359237f else null

fun converterMmolLToMgDl(mmolL:Float?) = if (mmolL != null) mmolL / 18.016f else null
fun converterMgDlToMmolL(mgDl:Float?) = if (mgDl != null) mgDl * 18.018f else null

fun converterMmRtStToKPa(mmRtSt:Float?) = if (mmRtSt != null) mmRtSt / 7.5f else null
fun converterKPaToMmRtSt(kPa:Float?) = if (kPa != null) kPa * 7.5f else null

fun converterDashBoardToLoadData(context:Context, dashBoard: DashBoard): MutableList<LoadData>{
    val data = mutableListOf<LoadData>()

    //data.add(LoadData(DataTypeInTime.GENDER, context.getString(R.string.gender),  dashBoard.gender?.toString() ?: "", "", ""))
    //data.add(LoadData(DataTypeInTime.BIRTH_DATE, context.getString(R.string.age),  dashBoard.birth_date ?: "", "", ""))
    //data.add(LoadData(DataTypeInTime.NATIONALITY, context.getString(R.string.nationality), dashBoard.nationality ?: "" , "", ""))
    //data.add(LoadData(DataTypeInTime.COUNTRY, context.getString(R.string.country),  dashBoard.country?.toString() ?: "" , "", ""))
    when (MainViewModel.measuringSystem) {
        MeasuringSystem.SI -> {
            data.add(
                LoadData(
                    DataTypeInTime.HEIGHT,
                    context.getString(R.string.height),
                    dashBoard.height?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WEIGHT,
                    context.getString(R.string.weight),
                    dashBoard.weight?.toString() ?: "",
                    context.getString(R.string.kg),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.HIP,
                    context.getString(R.string.hip),
                    dashBoard.hip?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.NECK,
                    context.getString(R.string.neck),
                    dashBoard.neck?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WAIST,
                    context.getString(R.string.waist),
                    dashBoard.waist?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WRIST,
                    context.getString(R.string.wrist),
                    dashBoard.wrist?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.BLOOD_PRESSURE,
                    context.getString(R.string.blood_pressure),
                    dashBoard.blood_pressure_sys?.toString() ?: "",
                    context.getString(R.string.mm_rt_st),
                    dashBoard.blood_pressure_dia?.toString() ?: ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.CHOLESTEROL,
                    context.getString(R.string.cholesterol),
                    dashBoard.cholesterol?.toString() ?: "",
                    context.getString(R.string.mmol_l),
                    ""
                )
            )
            //data.add(LoadData(DataTypeInTime.DISEASES, context.getString(R.string.diseases),  dashBoard.diseases?.toString() ?: "" , "", ""))
            data.add(
                LoadData(
                    DataTypeInTime.GLUCOSE,
                    context.getString(R.string.glucose),
                    dashBoard.glucose?.toString() ?: "",
                    context.getString(R.string.mmol_l),
                    ""
                )
            )
        }
        MeasuringSystem.IMPERIAL -> {
            data.add(
                LoadData(
                    DataTypeInTime.HEIGHT,
                    context.getString(R.string.height),
                    converterSmToIn(
                        dashBoard.height
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WEIGHT,
                    context.getString(R.string.weight),
                    converterKgToLb(
                        dashBoard.weight
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.funt),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.HIP,
                    context.getString(R.string.hip),
                    converterSmToIn(
                        dashBoard.hip
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.NECK,
                    context.getString(R.string.neck),
                    converterSmToIn(
                        dashBoard.neck
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WAIST,
                    context.getString(R.string.waist),
                    converterSmToIn(
                        dashBoard.waist
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WRIST,
                    context.getString(R.string.wrist),
                    converterSmToIn(
                        dashBoard.wrist
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.BLOOD_PRESSURE,
                    context.getString(R.string.blood_pressure),
                    converterMmRtStToKPa(dashBoard.blood_pressure_sys?.toFloat())?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.k_pa),
                    converterMmRtStToKPa(dashBoard.blood_pressure_dia?.toFloat())?.roundTo(2)?.toString() ?: ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.CHOLESTEROL,
                    context.getString(R.string.cholesterol),
                    converterMmolLToMgDl(dashBoard.cholesterol)?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.mg_dl),
                    ""
                )
            )
            //data.add(LoadData(DataTypeInTime.DISEASES, context.getString(R.string.diseases),  dashBoard.diseases?.toString() ?: "" , "", ""))
            data.add(
                LoadData(
                    DataTypeInTime.GLUCOSE,
                    context.getString(R.string.glucose),
                    converterMmolLToMgDl(dashBoard.glucose)?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.mg_dl),
                    ""
                )
            )
        }
    }


    data.add(
        LoadData(
            DataTypeInTime.HEARTS_RATE_ALONE,
            context.getString(R.string.heart_rate_alone),
            dashBoard.heart_rate_alone?.toString() ?: "",
            context.getString(R.string.ud_min),
            ""
        )
    )

    data.add( LoadData(DataTypeInTime.DAILY_ACTIVITY_LEVEL, context.getString(R.string.activity_level), dashBoard.daily_activity_level?.toString() ?: "1.2", "", ""))

            //data.add(LoadData(DataTypeInTime.HEARTS_RATE_VARIABILITY, context.getString(R.string.heart_rate_variability),  dashBoard.heart_rate_variability?.toString() ?: "" , context.getString(R.string.ud_min), ""))

    //data.add(LoadData(context.getString(R.string.blood_pressure_dia), DataTypeInTime.BLOOD_PRESSURE_DIA,  ))

    //data.add(LoadData(DataTypeInTime.SMOKER, context.getString(R.string.smoker),  dashBoard.smoker?.toString() ?: "" , "", ""))

    return data
}

fun converterMedCardToLoadData(context:Context, medCard: MedCard): MutableList<LoadData>{
    val data = mutableListOf<LoadData>()

    //data.add(LoadData(DataTypeInTime.GENDER, context.getString(R.string.gender),  dashBoard.gender?.toString() ?: "", "", ""))
    //data.add(LoadData(DataTypeInTime.BIRTH_DATE, context.getString(R.string.age),  dashBoard.birth_date ?: "", "", ""))
    //data.add(LoadData(DataTypeInTime.NATIONALITY, context.getString(R.string.nationality), dashBoard.nationality ?: "" , "", ""))
    //data.add(LoadData(DataTypeInTime.COUNTRY, context.getString(R.string.country),  dashBoard.country?.toString() ?: "" , "", ""))
    when (MainViewModel.measuringSystem) {
        MeasuringSystem.SI -> {
            data.add(
                LoadData(
                    DataTypeInTime.WEIGHT,
                    context.getString(R.string.weight),
                    medCard.weight?.toString() ?: "",
                    context.getString(R.string.kg),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.HIP,
                    context.getString(R.string.hip),
                    medCard.hip?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.NECK,
                    context.getString(R.string.neck),
                    medCard.neck?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WAIST,
                    context.getString(R.string.waist),
                    medCard.waist?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WRIST,
                    context.getString(R.string.wrist),
                    medCard.wrist?.toString() ?: "",
                    context.getString(R.string.cm),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.BLOOD_PRESSURE,
                    context.getString(R.string.blood_pressure),
                    medCard.blood_pressure_sys?.toString() ?: "",
                    context.getString(R.string.mm_rt_st),
                    medCard.blood_pressure_dia?.toString() ?: ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.CHOLESTEROL,
                    context.getString(R.string.cholesterol),
                    medCard.cholesterol?.toString() ?: "",
                    context.getString(R.string.mmol_l),
                    ""
                )
            )
            //data.add(LoadData(DataTypeInTime.DISEASES, context.getString(R.string.diseases),  dashBoard.diseases?.toString() ?: "" , "", ""))
            data.add(
                LoadData(
                    DataTypeInTime.GLUCOSE,
                    context.getString(R.string.glucose),
                    medCard.glucose?.toString() ?: "",
                    context.getString(R.string.mmol_l),
                    ""
                )
            )
        }
        MeasuringSystem.IMPERIAL -> {
            data.add(
                LoadData(
                    DataTypeInTime.WEIGHT,
                    context.getString(R.string.weight),
                    converterKgToLb(
                        medCard.weight
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.funt),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.HIP,
                    context.getString(R.string.hip),
                    converterSmToIn(
                        medCard.hip
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.NECK,
                    context.getString(R.string.neck),
                    converterSmToIn(
                        medCard.neck
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WAIST,
                    context.getString(R.string.waist),
                    converterSmToIn(
                        medCard.waist
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.WRIST,
                    context.getString(R.string.wrist),
                    converterSmToIn(
                        medCard.wrist
                    )?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.inch),
                    ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.BLOOD_PRESSURE,
                    context.getString(R.string.blood_pressure),
                    converterMmRtStToKPa(medCard.blood_pressure_sys?.toFloat())?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.k_pa),
                    converterMmRtStToKPa(medCard.blood_pressure_dia?.toFloat())?.roundTo(2)?.toString() ?: ""
                )
            )
            data.add(
                LoadData(
                    DataTypeInTime.CHOLESTEROL,
                    context.getString(R.string.cholesterol),
                    converterMmolLToMgDl(medCard.cholesterol)?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.mg_dl),
                    ""
                )
            )
            //data.add(LoadData(DataTypeInTime.DISEASES, context.getString(R.string.diseases),  dashBoard.diseases?.toString() ?: "" , "", ""))
            data.add(
                LoadData(
                    DataTypeInTime.GLUCOSE,
                    context.getString(R.string.glucose),
                    converterMmolLToMgDl(medCard.glucose)?.roundTo(2)?.toString() ?: "",
                    context.getString(R.string.mg_dl),
                    ""
                )
            )
        }
    }


    data.add(
        LoadData(
            DataTypeInTime.HEARTS_RATE_ALONE,
            context.getString(R.string.heart_rate_alone),
            medCard.heart_rate_alone?.toString() ?: "",
            context.getString(R.string.ud_min),
            ""
        )
    )

    data.add( LoadData(DataTypeInTime.DAILY_ACTIVITY_LEVEL, context.getString(R.string.activity_level), medCard.daily_activity_level?.toString() ?: "1.2", "", ""))

    //data.add(LoadData(DataTypeInTime.HEARTS_RATE_VARIABILITY, context.getString(R.string.heart_rate_variability),  dashBoard.heart_rate_variability?.toString() ?: "" , context.getString(R.string.ud_min), ""))

    //data.add(LoadData(context.getString(R.string.blood_pressure_dia), DataTypeInTime.BLOOD_PRESSURE_DIA,  ))

    //data.add(LoadData(DataTypeInTime.SMOKER, context.getString(R.string.smoker),  dashBoard.smoker?.toString() ?: "" , "", ""))

    return data
}


fun converterLoadDataToDashBoard(loadData: MutableList<LoadData>): DashBoard {
    val  dashBoard = if (MainViewModel.singleDashBoard != null) MainViewModel.singleDashBoard!!
                        else DashBoard(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    //dashBoard.gender = loadData.find { it.type == DataTypeInTime.GENDER }?.contentFirst?.toIntOrNull()
    //dashBoard.birth_date = loadData.find { it.type == DataTypeInTime.BIRTH_DATE }?.contentFirst // Чтобы не отображалось
    //dashBoard.nationality = loadData.find { it.type == DataTypeInTime.NATIONALITY }?.contentFirst
    //dashBoard.country = loadData.find { it.type == DataTypeInTime.COUNTRY }?.contentFirst?.toIntOrNull()
    dashBoard.height = loadData.find { it.type == DataTypeInTime.HEIGHT }?.contentFirst?.replace(',','.')?.toFloatOrNull()
    dashBoard.weight = loadData.find { it.type == DataTypeInTime.WEIGHT }?.contentFirst?.replace(',','.')?.toFloatOrNull()
    dashBoard.hip = loadData.find { it.type == DataTypeInTime.HIP }?.contentFirst?.replace(',','.')?.toFloatOrNull()
    dashBoard.waist = loadData.find { it.type == DataTypeInTime.WAIST }?.contentFirst?.replace(',','.')?.toFloatOrNull()
    dashBoard.wrist = loadData.find { it.type == DataTypeInTime.WRIST }?.contentFirst?.replace(',','.')?.toFloatOrNull()
    dashBoard.neck = loadData.find { it.type == DataTypeInTime.NECK }?.contentFirst?.replace(',','.')?.toFloatOrNull()
    dashBoard.heart_rate_alone = loadData.find { it.type == DataTypeInTime.HEARTS_RATE_ALONE }?.contentFirst?.toIntOrNull()
    dashBoard.heart_rate_variability = loadData.find { it.type == DataTypeInTime.HEARTS_RATE_VARIABILITY }?.contentFirst?.toIntOrNull()
    dashBoard.blood_pressure_sys = loadData.find { it.type == DataTypeInTime.BLOOD_PRESSURE }?.contentFirst?.toIntOrNull()
    dashBoard.blood_pressure_dia = loadData.find { it.type == DataTypeInTime.BLOOD_PRESSURE }?.contentSecond?.toIntOrNull()
    dashBoard.cholesterol = loadData.find { it.type == DataTypeInTime.CHOLESTEROL }?.contentFirst?.replace(',','.')?.toFloatOrNull()
    //dashBoard.diseases = loadData.find { it.type == DataTypeInTime.DISEASES }?.contentFirst?.toIntOrNull()
    dashBoard.glucose = loadData.find { it.type == DataTypeInTime.GLUCOSE }?.contentFirst?.replace(',','.')?.toFloatOrNull()
    //dashBoard.smoker = loadData.find { it.type == DataTypeInTime.SMOKER }?.contentFirst?.toBoolean()
    dashBoard.measuring_system = loadData.find { it.type == DataTypeInTime.MEASURING_SYSTEM }?.contentFirst?.toIntOrNull()
    dashBoard.daily_activity_level = loadData.find { it.type == DataTypeInTime.DAILY_ACTIVITY_LEVEL }?.contentFirst?.replace(',','.')?.toFloatOrNull()

    return dashBoard
}
