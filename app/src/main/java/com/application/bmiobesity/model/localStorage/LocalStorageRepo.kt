package com.application.bmiobesity.model.localStorage

import android.content.Context
import com.application.bmiobesity.R
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import com.application.bmiobesity.model.db.commonSettings.entities.Genders
import com.application.bmiobesity.model.db.commonSettings.entities.Policy
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.model.db.paramSettings.entities.ParamUnit
import com.application.bmiobesity.model.db.paramSettings.entities.ResultCard
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class LocalStorageRepo(private val mContext: Context) {

    fun getCountriesList(locale: String): List<Countries>{
        val result: ArrayList<Countries> = ArrayList()
        val resId = when(locale){
            "en" -> R.raw.countries_config_en
            "ru" -> R.raw.countries_config_ru
            else -> R.raw.countries_config_en
        }
        val countriesJsonArray = readConfigDB(resId, "countries")
        for (i in 0 until countriesJsonArray.length()){
            val temp = Countries(
                countriesJsonArray.getJSONObject(i).getInt("id"),
                countriesJsonArray.getJSONObject(i).getString("value")
            )
            result.add(temp)
        }
        return result
    }

    fun getGendersList(locale: String): List<Genders>{
        val result: ArrayList<Genders> = ArrayList()
        val resId = when(locale){
            "en" -> R.raw.genders_config_en
            "ru" -> R.raw.genders_config_ru
            else -> R.raw.genders_config_en
        }
        val gendersJsonArray = readConfigDB(resId, "genders")
        for (i in 0 until gendersJsonArray.length()){
            val temp = Genders(
                gendersJsonArray.getJSONObject(i).getInt("id"),
                gendersJsonArray.getJSONObject(i).getString("value")
            )
            result.add(temp)
        }
        return result
    }

    fun getPolicy(locale: String): Policy{
        val resId = when(locale){
            "en" -> R.raw.policy_config_en
            "ru" -> R.raw.policy_config_ru
            else -> R.raw.policy_config_en
        }
        val policyJson = readConfigDB(resId, "policies")
        return Policy(locale, policyJson.getJSONObject(0).getString("policy"))
    }

    fun getResultCardList(): List<ResultCard>{
        val result: ArrayList<ResultCard> = ArrayList()
        val resultCardJSONArray = readConfigDB(R.raw.result_card_config, "result_card")
        for (i in 0 until resultCardJSONArray.length()){
            val temp = ResultCard(
                resultCardJSONArray.getJSONObject(i).getString("id"),
                resultCardJSONArray.getJSONObject(i).getString("name"),
                resultCardJSONArray.getJSONObject(i).getString("img"),
                resultCardJSONArray.getJSONObject(i).getString("short_description"),
                resultCardJSONArray.getJSONObject(i).getString("long_description"),
                resultCardJSONArray.getJSONObject(i).getLong("last_modified"),
                resultCardJSONArray.getJSONObject(i).getString("data_type"),
                resultCardJSONArray.getJSONObject(i).getBoolean("is_visible")
            )
            result.add(temp)
        }
        return result
    }

    fun getParamUnitList(): List<ParamUnit>{
        val result: ArrayList<ParamUnit> = ArrayList()
        val paramUnitJSONArray = readConfigDB(R.raw.param_unit_config, "param_unit")
        for (i in 0 until paramUnitJSONArray.length()){
            val temp = ParamUnit(
                paramUnitJSONArray.getJSONObject(i).getString("id"),
                paramUnitJSONArray.getJSONObject(i).getString("name_metric_res"),
                paramUnitJSONArray.getJSONObject(i).getString("name_imperial_res")
            )
            result.add(temp)
        }
        return result
    }

    fun getMedCardSourceType(): List<MedCardSourceType>{
        val result: ArrayList<MedCardSourceType> = ArrayList()
        val medCardSourceTypeJSONArray = readConfigDB(R.raw.medcard_source_type_config, "source_list")
        for (i in 0 until medCardSourceTypeJSONArray.length()){
            val temp = MedCardSourceType(
                medCardSourceTypeJSONArray.getJSONObject(i).getString("id"),
                medCardSourceTypeJSONArray.getJSONObject(i).getString("title")
            )
            result.add(temp)
        }
        return result
    }

    fun getMedCardParamSettingList(): List<MedCardParamSetting>{
        val result: ArrayList<MedCardParamSetting> = ArrayList()
        val medCardParamSettingJSONArray = readConfigDB(R.raw.medcard_param_setting_config, "medcard_param_setting")
        for (i in 0 until medCardParamSettingJSONArray.length()){
            val temp = MedCardParamSetting(
                medCardParamSettingJSONArray.getJSONObject(i).getString("id"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("name_res"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("short_description_res"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("long_description_res"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("img_res"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("default_value"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("data_type"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("comment"),
                medCardParamSettingJSONArray.getJSONObject(i).getBoolean("status"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("display_type"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("source_type_id"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("source_type_id_multi"),
                medCardParamSettingJSONArray.getJSONObject(i).getString("unit_id"),
                medCardParamSettingJSONArray.getJSONObject(i).getDouble("molar_mass").toFloat()
            )
            result.add(temp)
        }
        return result
    }

    private fun readConfigDB(resID: Int, arrayName: String): JSONArray {
        val input = mContext.resources.openRawResource(resID)
        val br = BufferedReader(InputStreamReader(input))
        val sb = StringBuilder()
        var s: String? = ""
        s = br.readLine()
        while (s != null) {
            sb.append(s)
            sb.append("\n")
            s = br.readLine()
        }
        val JSONtext = sb.toString()
        val JSONroot = JSONObject(JSONtext)
        val JSONarray = JSONroot.getJSONArray(arrayName)
        return JSONarray
    }
}