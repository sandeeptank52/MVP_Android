package com.application.bmiobesity.view.mainActivity.home.report

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.common.MeasuringSystem
import com.application.bmiobesity.common.parameters.DailyActivityLevels
import com.application.bmiobesity.databinding.MainHomeReportCardViewBinding
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSimpleValue
import com.application.bmiobesity.model.db.paramSettings.entities.ParamUnit
import com.application.bmiobesity.utils.getDateStrFromMS
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class HomeReportAdapterRecycler(
    private val context: Context,
    private val paramUnits: List<ParamUnit>,
    var medCards: List<MedCardParamSetting>,
) : ListAdapter<List<MedCardParamSimpleValue>, HomeReportAdapterRecycler.HomeReportViewHolder>(
    HomeReportDiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeReportViewHolder {
        val binding = MainHomeReportCardViewBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return HomeReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeReportViewHolder, position: Int) {
        val items = getItem(position)
        holder.bind(items)
    }

    inner class HomeReportViewHolder(
        private val binding: MainHomeReportCardViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(items: List<MedCardParamSimpleValue>) {
            try {
                // Get value of med card and unit
                val paramID = items.first().paramID
                val medCard = medCards.find { it.id == paramID }
                val measuringSystem = medCard?.preferMeasuringSystem
                val paramUnit = paramUnits.findLast { it.id == medCard?.unitID }

                // Set text parameter name and unit
                setText(medCard, measuringSystem, paramUnit)

                // Set axis
                setXAxis()
                setYAxis()

                // Set marker
                val markerView = ReportMarkerView(context, medCard)
                markerView.chartView = binding.reportCardViewLineChart
                binding.reportCardViewLineChart.marker = markerView

                // Set line data set
                val entries = getEntries(items, measuringSystem)
                val lineDataSet = setLineDataSet(entries)

                // Set chart
                val lineData = LineData(lineDataSet)
                setChart(lineData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @SuppressLint("SetTextI18n")
        private fun setText(
            medCard: MedCardParamSetting?,
            measuringSystem: Int?,
            paramUnit: ParamUnit?
        ) {
            // Set text parameter name
            val parameter = InTimeApp.APPLICATION.resources.getIdentifier(
                medCard?.nameRes,
                "string",
                "com.application.bmiobesity"
            )
            binding.reportCardViewParameter.setText(parameter)

            // Set text parameter unit
            if (medCard?.displayType.equals("picker")) {
                when (measuringSystem) {
                    MeasuringSystem.IMPERIAL.id -> {
                        if (paramUnit != null && paramUnit.nameImperialRes.isNotEmpty()) {
                            val unitRes = InTimeApp.APPLICATION.resources.getIdentifier(
                                paramUnit.nameImperialRes,
                                "string",
                                "com.application.bmiobesity"
                            )
                            val str = InTimeApp.APPLICATION.resources.getString(unitRes)
                            binding.reportCardViewUnit.text = "($str)"
                        }
                    }
                    MeasuringSystem.METRIC.id -> {
                        if (paramUnit != null && paramUnit.nameMetricRes.isNotEmpty()) {
                            val unitRes = InTimeApp.APPLICATION.resources.getIdentifier(
                                paramUnit.nameMetricRes,
                                "string",
                                "com.application.bmiobesity"
                            )
                            val str = InTimeApp.APPLICATION.resources.getString(unitRes)
                            binding.reportCardViewUnit.text = "($str)"
                        }
                    }
                }
            } else {
                binding.reportCardViewUnit.text = ""
            }
        }

        private fun getEntries(
            items: List<MedCardParamSimpleValue>,
            measuringSystem: Int?
        ): MutableList<Entry> {
            val entries: MutableList<Entry> = mutableListOf()
            when (measuringSystem) {
                MeasuringSystem.IMPERIAL.id -> {
                    items.forEach { item ->
                        item.valueImp?.let {
                            Entry(item.timestamp.toFloat(), it)
                        }?.let {
                            entries.add(it)
                        }
                    }
                }
                else -> {
                    items.forEach { item ->
                        item.value?.let {
                            Entry(item.timestamp.toFloat(), it)
                        }?.let {
                            entries.add(it)
                        }
                    }
                }
            }
            return entries
        }

        private fun setXAxis() {
            val xAxis = binding.reportCardViewLineChart.xAxis
            xAxis?.setDrawGridLines(false)
            xAxis?.position = XAxis.XAxisPosition.BOTTOM
            xAxis?.textColor = context.resources.getColor(R.color.colorPrimary, null)
            xAxis?.granularity = 2F
            xAxis?.labelCount = 4
            xAxis?.valueFormatter = DateAxisValueFormatter()
        }

        private fun setYAxis() {
            binding.reportCardViewLineChart.axisRight?.isEnabled = false
            val yAxis = binding.reportCardViewLineChart.axisLeft
            yAxis?.enableGridDashedLine(10F, 10F, 0F)
            yAxis?.textColor = context.resources.getColor(R.color.colorPrimary, null)
        }

        private fun setLineDataSet(entries: List<Entry>): LineDataSet {
            val lineDataSet = LineDataSet(entries, binding.reportCardViewParameter.text.toString())
            lineDataSet.setDrawCircles(false)
            lineDataSet.setDrawIcons(false)
            lineDataSet.setDrawValues(false)
            lineDataSet.enableDashedHighlightLine(10F, 10F, 0F)
            lineDataSet.setDrawHighlightIndicators(true)
            lineDataSet.highLightColor = context.resources.getColor(R.color.colorPrimary, null)
            lineDataSet.color = context.resources.getColor(R.color.colorPrimary, null)
            lineDataSet.lineWidth = 1.5F
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            return lineDataSet
        }

        private fun setChart(lineData: LineData) {
            binding.reportCardViewLineChart.data = lineData
            binding.reportCardViewLineChart.description?.isEnabled = false
            binding.reportCardViewLineChart.legend?.isEnabled = false
            binding.reportCardViewLineChart.setPinchZoom(true)
            binding.reportCardViewLineChart.animateX(500)
            binding.reportCardViewLineChart.invalidate()
        }

        // Display the x-axis value in date format "dd.MM.yyyy"
        private inner class DateAxisValueFormatter : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return getDateStrFromMS((value.toLong()))
            }
        }

        // Custom marker view when selecting particular point in chart
        private inner class ReportMarkerView(
            context: Context,
            val medCard: MedCardParamSetting?
        ) : MarkerView(
            context,
            R.layout.main_home_report_marker_view
        ) {
            val reportMarkerViewValue: TextView = findViewById(R.id.reportMarkerViewValue)

            override fun refreshContent(e: Entry?, highlight: Highlight?) {
                when (medCard?.displayType) {
                    "picker" -> reportMarkerViewValue.text = e?.y.toString()
                    "list" -> {
                        val dailyActivity = when (e?.y) {
                            DailyActivityLevels.MINIMUM.id -> DailyActivityLevels.MINIMUM
                            DailyActivityLevels.LOWER.id -> DailyActivityLevels.LOWER
                            DailyActivityLevels.MEDIUM.id -> DailyActivityLevels.MEDIUM
                            DailyActivityLevels.HIGH.id -> DailyActivityLevels.HIGH
                            DailyActivityLevels.VERY_HIGH.id -> DailyActivityLevels.VERY_HIGH
                            else -> DailyActivityLevels.MEDIUM
                        }
                        val nameId = InTimeApp.APPLICATION.resources.getIdentifier(
                            dailyActivity.nameRes,
                            "string",
                            "com.application.bmiobesity"
                        )
                        reportMarkerViewValue.setText(nameId)
                    }
                }

                super.refreshContent(e, highlight)
            }

            override fun getOffset(): MPPointF {
                return MPPointF((-(width / 2)).toFloat(), (-height * 1.2).toFloat())
            }
        }
    }

    object HomeReportDiffCallback : DiffUtil.ItemCallback<List<MedCardParamSimpleValue>>() {

        override fun areItemsTheSame(
            oldItem: List<MedCardParamSimpleValue>,
            newItem: List<MedCardParamSimpleValue>
        ): Boolean {
            if (oldItem.size != newItem.size) {
                return false
            }
            return newItem.zip(oldItem).all { (x, y) ->
                x == y && x.id == y.id
            }
        }

        override fun areContentsTheSame(
            oldItem: List<MedCardParamSimpleValue>,
            newItem: List<MedCardParamSimpleValue>
        ): Boolean {
            if (oldItem.size != newItem.size) {
                return false
            }
            return newItem.zip(oldItem).all { (x, y) ->
                x.value == y.value && x.valueImp == y.valueImp
            }
        }
    }

}