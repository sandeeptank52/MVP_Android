package com.application.bmiobesity.common.parameters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.common.MeasuringSystem
import com.application.bmiobesity.model.db.paramSettings.ParamSettingsRepo
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSimpleValue
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.model.db.paramSettings.entities.ParamUnit
import com.application.bmiobesity.model.retrofit.ResultMedCard
import com.application.bmiobesity.model.retrofit.UpdateResultDashBoard
import com.application.bmiobesity.utils.numberToWithoutDigit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MedCard {
    @Inject
    lateinit var paramSettingRepo: ParamSettingsRepo

    private val mParametersLive: MutableLiveData<MutableMap<String, MedCardParamSetting>> = MutableLiveData<MutableMap<String, MedCardParamSetting>>()
    val parametersLive: LiveData<MutableMap<String, MedCardParamSetting>> = mParametersLive

    private var parameters: MutableMap<String, MedCardParamSetting> = mutableMapOf()
    private var waitingUpdatesParameters: MutableMap<String, MedCardParamSetting> = mutableMapOf()

    init {
        InTimeApp.appComponent.inject(this)
    }

    fun setParameters(p: List<MedCardParamSetting>){
        p.forEach {
            parameters[it.id] = it
        }
        waitingUpdatesParameters = parameters
        mParametersLive.postValue(parameters)
    }
    fun setPreferMeasurementSystem(system: MeasuringSystem){
        parameters.values.forEach {
            it.preferMeasuringSystem = system.id
            updateSettingDB(it)
        }
        waitingUpdatesParameters = parameters
        mParametersLive.postValue(parameters)
    }
    fun setSourceType(s: MedCardSourceType, p: MedCardParamSetting){
        val parameter = parameters[p.id]
        parameter?.let {
            it.sourceTypeID = s.id
            updateSettingDB(it)
            waitingUpdatesParameters = parameters
            mParametersLive.postValue(parameters)
        }
    }

    fun successUpdate(){
        parameters = waitingUpdatesParameters
        mParametersLive.postValue(parameters)
    }
    fun errorUpdate(){
        waitingUpdatesParameters = parameters
    }

    fun setMetricValue(v: Float, p: MedCardParamSetting){
        val parameter = waitingUpdatesParameters[p.id]
        parameter?.let {
            val simpleValue = when (val id = it.id){
                AvailableParameters.WRIST.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertSmToIn(v))}
                AvailableParameters.BLOOD_PRESSURE_DIASTOLIC.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertMmRtStToKPa(v))}
                AvailableParameters.BLOOD_PRESSURE_SYSTOLIC.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertMmRtStToKPa(v))}
                AvailableParameters.CHOLESTEROL.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertMmolLToMgDl(v, it.molarMass))}
                AvailableParameters.DAILY_ACTIVITY_LEVEL.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = v)}
                AvailableParameters.GLUCOSE.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertMmolLToMgDl(v, it.molarMass))}
                AvailableParameters.HEART_RATE_ALONE.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = v)}
                AvailableParameters.HIP.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertSmToIn(v))}
                AvailableParameters.NECK.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertSmToIn(v))}
                AvailableParameters.WAIST.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertSmToIn(v))}
                AvailableParameters.WEIGHT.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = ParamUnit.convertKgToLb(v))}
                else -> {MedCardParamSimpleValue(paramID = "", value = 0.0f, valueImp = 0.0f)}
            }
            simpleValue.timestamp = Calendar.getInstance(TimeZone.getDefault()).timeInMillis
            it.values.add(simpleValue)
            insertSimpleValueToDB(simpleValue)
            //mParametersLive.postValue(parameters)
        }
    }
    fun setImpValue(v: Float, p: MedCardParamSetting){
        val parameter = waitingUpdatesParameters[p.id]
        parameter?.let {
            val simpleValue = when (val id = it.id){
                AvailableParameters.WRIST.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertInToSm(v),
                    valueImp = v)}
                AvailableParameters.BLOOD_PRESSURE_DIASTOLIC.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertKPaToMmRtSt(v),
                    valueImp = v)}
                AvailableParameters.BLOOD_PRESSURE_SYSTOLIC.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertKPaToMmRtSt(v),
                    valueImp = v)}
                AvailableParameters.CHOLESTEROL.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertMgDlToMmolL(v, it.molarMass),
                    valueImp = v)}
                AvailableParameters.DAILY_ACTIVITY_LEVEL.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = v)}
                AvailableParameters.GLUCOSE.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertMgDlToMmolL(v, it.molarMass),
                    valueImp = v)}
                AvailableParameters.HEART_RATE_ALONE.id -> {MedCardParamSimpleValue(paramID = id,
                    value = v,
                    valueImp = v)}
                AvailableParameters.HIP.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertInToSm(v),
                    valueImp = v)}
                AvailableParameters.NECK.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertInToSm(v),
                    valueImp = v)}
                AvailableParameters.WAIST.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertInToSm(v),
                    valueImp = v)}
                AvailableParameters.WEIGHT.id -> {MedCardParamSimpleValue(paramID = id,
                    value = ParamUnit.convertLbToKg(v),
                    valueImp = v)}
                else -> {MedCardParamSimpleValue(paramID = "", value = 0.0f, valueImp = 0.0f)}
            }
            simpleValue.timestamp = Calendar.getInstance(TimeZone.getDefault()).timeInMillis
            it.values.add(simpleValue)
            insertSimpleValueToDB(simpleValue)
            //mParametersLive.postValue(parameters)
        }
    }
    fun setValues(v: ResultMedCard){
        val values = parameters.values
        values.forEach {
            //val temp: MutableList<MedCardParamSimpleValue> = mutableListOf()
            val simpleValue = when (val id = it.id){
                AvailableParameters.WRIST.id -> {MedCardParamSimpleValue(paramID = id,
                                                                        value = v.wrist,
                                                                        valueImp = ParamUnit.convertSmToIn(v.wrist))}
                AvailableParameters.BLOOD_PRESSURE_DIASTOLIC.id -> {MedCardParamSimpleValue(paramID = id,
                                                                                            value = v.blood_pressure_dia?.toFloat(),
                                                                                            valueImp = ParamUnit.convertMmRtStToKPa(v.blood_pressure_dia?.toFloat()))}
                AvailableParameters.BLOOD_PRESSURE_SYSTOLIC.id -> {MedCardParamSimpleValue(paramID = id,
                                                                                            value = v.blood_pressure_sys?.toFloat(),
                                                                                            valueImp = ParamUnit.convertMmRtStToKPa(v.blood_pressure_sys?.toFloat()))}
                AvailableParameters.CHOLESTEROL.id -> {MedCardParamSimpleValue(paramID = id,
                                                                                value = v.cholesterol,
                                                                                valueImp = ParamUnit.convertMmolLToMgDl(v.cholesterol, it.molarMass))}
                AvailableParameters.DAILY_ACTIVITY_LEVEL.id -> {MedCardParamSimpleValue(paramID = id,
                                                                                        value = v.daily_activity_level,
                                                                                        valueImp = v.daily_activity_level)}
                AvailableParameters.GLUCOSE.id -> {MedCardParamSimpleValue(paramID = id,
                                                                            value = v.glucose,
                                                                            valueImp = ParamUnit.convertMmolLToMgDl(v.glucose, it.molarMass))}
                AvailableParameters.HEART_RATE_ALONE.id -> {MedCardParamSimpleValue(paramID = id,
                                                                                    value = v.heart_rate_alone?.toFloat(),
                                                                                    valueImp = v.heart_rate_alone?.toFloat())}
                AvailableParameters.HIP.id -> {MedCardParamSimpleValue(paramID = id,
                                                                        value = v.hip,
                                                                        valueImp = ParamUnit.convertSmToIn(v.hip))}
                AvailableParameters.NECK.id -> {MedCardParamSimpleValue(paramID = id,
                                                                        value = v.neck,
                                                                        valueImp = ParamUnit.convertSmToIn(v.neck))}
                AvailableParameters.WAIST.id -> {MedCardParamSimpleValue(paramID = id,
                                                                        value = v.waist,
                                                                        valueImp = ParamUnit.convertSmToIn(v.waist))}
                AvailableParameters.WEIGHT.id -> {MedCardParamSimpleValue(paramID = id,
                                                                            value = v.weight,
                                                                            valueImp = ParamUnit.convertKgToLb(v.weight))}
                else -> {MedCardParamSimpleValue(paramID = "", value = 0.0f, valueImp = 0.0f)}
            }
            simpleValue.timestamp = Calendar.getInstance(TimeZone.getDefault()).timeInMillis
            //temp.add(simpleValue)
            it.values.add(simpleValue)
            insertSimpleValueToDB(simpleValue)
        }
        waitingUpdatesParameters = parameters
        mParametersLive.postValue(parameters)
    }

    fun getResultMedCard(): ResultMedCard{
        return ResultMedCard(waitingUpdatesParameters["weight"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["hip"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["waist"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["wrist"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["neck"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["heart_rate_alone"]?.values?.lastOrNull()?.value?.toInt(),
            waitingUpdatesParameters["daily_activity_level"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["blood_pressure_sys"]?.values?.lastOrNull()?.value?.toInt(),
            waitingUpdatesParameters["blood_pressure_dia"]?.values?.lastOrNull()?.value?.toInt(),
            waitingUpdatesParameters["cholesterol"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["glucose"]?.values?.lastOrNull()?.value)
    }
    fun getDashBoardMedCard(): UpdateResultDashBoard{
        return UpdateResultDashBoard(
            null,
            null,
            null,
            null,
            waitingUpdatesParameters["weight"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["hip"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["waist"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["wrist"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["heart_rate_alone"]?.values?.lastOrNull()?.value?.toInt(),
            waitingUpdatesParameters["blood_pressure_sys"]?.values?.lastOrNull()?.value?.toInt(),
            waitingUpdatesParameters["blood_pressure_dia"]?.values?.lastOrNull()?.value?.toInt(),
            waitingUpdatesParameters["cholesterol"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["glucose"]?.values?.lastOrNull()?.value,
            null,
            null,
            waitingUpdatesParameters["neck"]?.values?.lastOrNull()?.value,
            waitingUpdatesParameters["daily_activity_level"]?.values?.lastOrNull()?.value,
            1
        )
    }

    private fun updateSettingDB(item: MedCardParamSetting){
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch(Dispatchers.IO) {
            paramSettingRepo.updateParamSetting(item)
        }
    }
    private fun insertSimpleValueToDB(item: MedCardParamSimpleValue){
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch(Dispatchers.IO) {
            paramSettingRepo.insertSimpleValue(item)
        }
    }
}