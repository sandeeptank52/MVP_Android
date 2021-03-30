package com.application.bmiobesity.model.parameters

import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSimpleValue
import com.application.bmiobesity.model.retrofit.ResultMedCard

class MedCard {

    private var parameters: MutableMap<String, MedCardParamSetting> = mutableMapOf()

    fun setParameters(p: List<MedCardParamSetting>){
        p.forEach {
            parameters[it.id] = it
        }
    }

    fun setValues(v: ResultMedCard){
        val values = parameters.values
        values.forEach {
            val temp: MutableList<MedCardParamSimpleValue> = mutableListOf()
            val simpleValue = when (val id = it.id){
                AvailableParameters.WRIST.id -> {MedCardParamSimpleValue(paramID = id, value = v.wrist ?: 0.0f)}
                AvailableParameters.BLOOD_PRESSURE_DIASTOLIC.id -> {MedCardParamSimpleValue(paramID = id, value = v.blood_pressure_dia?.toFloat() ?: 0.0f)}
                AvailableParameters.BLOOD_PRESSURE_SYSTOLIC.id -> {MedCardParamSimpleValue(paramID = id, value = v.blood_pressure_sys?.toFloat() ?: 0.0f)}
                AvailableParameters.CHOLESTEROL.id -> {MedCardParamSimpleValue(paramID = id, value = v.cholesterol ?: 0.0f)}
                AvailableParameters.DAILY_ACTIVITY_LEVEL.id -> {MedCardParamSimpleValue(paramID = id, value = v.daily_activity_level ?: 0.0f)}
                AvailableParameters.GLUCOSE.id -> {MedCardParamSimpleValue(paramID = id, value = v.glucose ?: 0.0f)}
                AvailableParameters.HEART_RATE_ALONE.id -> {MedCardParamSimpleValue(paramID = id, value = v.heart_rate_alone?.toFloat() ?: 0.0f)}
                AvailableParameters.HIP.id -> {MedCardParamSimpleValue(paramID = id, value = v.hip ?: 0.0f)}
                AvailableParameters.NECK.id -> {MedCardParamSimpleValue(paramID = id, value = v.neck ?: 0.0f)}
                AvailableParameters.WAIST.id -> {MedCardParamSimpleValue(paramID = id, value = v.waist ?: 0.0f)}
                AvailableParameters.WEIGHT.id -> {MedCardParamSimpleValue(paramID = id, value = v.weight ?: 0.0f)}
                else -> {MedCardParamSimpleValue(paramID = "", value = 0.0f)}
            }
            temp.add(simpleValue)
            it.values = temp
        }
    }
}