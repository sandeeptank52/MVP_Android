package com.application.bmiobesity.view.mainActivity.medcard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.common.MeasuringSystem
import com.application.bmiobesity.common.parameters.DailyActivityLevels
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.model.db.paramSettings.entities.ParamUnit
import com.application.bmiobesity.utils.getDateStrFromMS
import com.application.bmiobesity.utils.getTimeStrFromMS
import com.google.android.material.card.MaterialCardView
import java.lang.StringBuilder
import java.util.*

class MedCardAdapterRecycler(private val units: List<ParamUnit>,
                             private val srcType: List<MedCardSourceType>,
                             private val onClick: (MedCardParamSetting) -> Unit) : ListAdapter<MedCardParamSetting, MedCardAdapterRecycler.MedCardViewHolder>(MedCardDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_medcard_card_view_item, parent, false)
        return MedCardViewHolder(view, units, srcType, onClick)
    }

    override fun onBindViewHolder(holder: MedCardViewHolder, position: Int) {
        val medCardItem = getItem(position)
        holder.bind(medCardItem)
    }

    class MedCardViewHolder(itemView: View,
                            private val units: List<ParamUnit>,
                            private val srcType: List<MedCardSourceType>,
                            val onClick: (MedCardParamSetting) -> Unit) : RecyclerView.ViewHolder(itemView){

        private var currentCard: MedCardParamSetting? = null
        private val title: TextView = itemView.findViewById(R.id.medCardItemTitle)
        private val paramValue: TextView = itemView.findViewById(R.id.medCardItemValue)
        //private val valueUnit: TextView = itemView.findViewById(R.id.medCardItemUnit)
        private val sourceTypeValue: TextView = itemView.findViewById(R.id.medCardItemSourceTypeValue)
        private val measurementFrequency: TextView = itemView.findViewById(R.id.medCardItemMeasurementFrequencyValue)
        private val lastTime: TextView = itemView.findViewById(R.id.medCardItemLastTime)
        private val lastDate: TextView = itemView.findViewById(R.id.medCardItemLastDate)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.medCardItemMaterialCardView)
        private val divider: View = itemView.findViewById(R.id.medCardItemDivider)

        init {
            itemView.setOnTouchListener { v, event ->
                when (event.action){
                    MotionEvent.ACTION_UP -> {
                        v.isPressed = false
                        v.performClick()
                        return@setOnTouchListener true
                    }
                    MotionEvent.ACTION_DOWN -> {
                        v.isPressed = true
                        return@setOnTouchListener true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        v.isPressed = true
                        return@setOnTouchListener true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        v.isPressed = false
                        return@setOnTouchListener true
                    }
                    else -> return@setOnTouchListener true
                }
            }

            itemView.setOnClickListener {
                currentCard?.let {
                    onClick(it)
                }
            }
        }

        fun bind(item: MedCardParamSetting){
            currentCard = item
            val valHistory = item.values
            val titleId = InTimeApp.APPLICATION.resources.getIdentifier(item.nameRes, "string", "com.application.bmiobesity")
            val freqId = InTimeApp.APPLICATION.resources.getIdentifier(item.measurementFrequencyRes, "string", "com.application.bmiobesity")
            val unit = units.findLast {it.id == item.unitID}
            val srcType = srcType.findLast { it.id == item.sourceTypeID }

            when (item.displayType){

                "picker" -> {
                    when (item.preferMeasuringSystem){

                        MeasuringSystem.IMPERIAL.id -> {
                            val valueStr = valHistory.lastOrNull()?.valueImp?.toString()

                            if (!valueStr.isNullOrEmpty()){
                                paramValue.text = valueStr
                                cardView.strokeColor = Color.parseColor("#f5f5f5")
                                divider.setBackgroundColor(Color.parseColor("#f5f5f5"))
                            } else {
                                paramValue.text = "---"
                                cardView.strokeColor = InTimeApp.APPLICATION.resources.getColor(android.R.color.holo_red_dark, null)
                                divider.setBackgroundColor(InTimeApp.APPLICATION.resources.getColor(android.R.color.holo_red_dark, null))
                            }

                            if (unit != null && unit.nameImperialRes.isNotEmpty()){
                                val unitRes = InTimeApp.APPLICATION.resources.getIdentifier(unit.nameImperialRes, "string", "com.application.bmiobesity")
                                val stringBuilder = StringBuilder(
                                    paramValue.text.toString()
                                            + " "
                                            + InTimeApp.APPLICATION.resources.getText(unitRes).toString()
                                )
                                paramValue.text = stringBuilder.toString()
                                //valueUnit.setText(unitRes)
                            } else {
                                //valueUnit.text = ""
                            }
                        }

                        MeasuringSystem.METRIC.id -> {
                            val valueStr = valHistory.lastOrNull()?.value?.toString()

                            if (!valueStr.isNullOrEmpty()){
                                paramValue.text = valueStr
                                cardView.strokeColor = Color.parseColor("#f5f5f5")
                                divider.setBackgroundColor(Color.parseColor("#f5f5f5"))
                            } else {
                                paramValue.text = "---"
                                cardView.strokeColor = InTimeApp.APPLICATION.resources.getColor(android.R.color.holo_red_dark, null)
                                divider.setBackgroundColor(InTimeApp.APPLICATION.resources.getColor(android.R.color.holo_red_dark, null))
                            }

                            if (unit != null && unit.nameMetricRes.isNotEmpty()){
                                val unitRes = InTimeApp.APPLICATION.resources.getIdentifier(unit.nameMetricRes, "string", "com.application.bmiobesity")
                                val stringBuilder = StringBuilder(
                                    paramValue.text.toString()
                                            + " "
                                            + InTimeApp.APPLICATION.resources.getText(unitRes).toString()
                                )
                                paramValue.text = stringBuilder.toString()
                                //valueUnit.setText(unitRes)
                            } else {
                                //valueUnit.text = ""
                            }
                        }
                    }
                }

                "list" -> {
                    val dailyActivity = when(valHistory.lastOrNull()?.value){
                        DailyActivityLevels.MINIMUM.id -> {DailyActivityLevels.MINIMUM}
                        DailyActivityLevels.LOWER.id -> {DailyActivityLevels.LOWER}
                        DailyActivityLevels.MEDIUM.id -> {DailyActivityLevels.MEDIUM}
                        DailyActivityLevels.HIGH.id -> {DailyActivityLevels.HIGH}
                        DailyActivityLevels.VERY_HIGH.id -> {DailyActivityLevels.VERY_HIGH}
                        else -> {DailyActivityLevels.MEDIUM}
                    }
                    val nameId = InTimeApp.APPLICATION.resources.getIdentifier(dailyActivity.nameRes, "string", "com.application.bmiobesity")
                    paramValue.setText(nameId)
                    //valueUnit.text = ""
                }
            }

            title.setText(titleId)
            measurementFrequency.setText(freqId)

            lastDate.text = getDateStrFromMS(valHistory.lastOrNull()?.timestamp ?: Date().time)
            lastTime.text = getTimeStrFromMS(valHistory.lastOrNull()?.timestamp ?: Date().time)

            sourceTypeValue.text = srcType?.title
        }
    }

    object MedCardDiffCallback: DiffUtil.ItemCallback<MedCardParamSetting>(){
        override fun areItemsTheSame( oldItem: MedCardParamSetting, newItem: MedCardParamSetting ): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame( oldItem: MedCardParamSetting, newItem: MedCardParamSetting ): Boolean {
            return oldItem.id == newItem.id
        }
    }
}