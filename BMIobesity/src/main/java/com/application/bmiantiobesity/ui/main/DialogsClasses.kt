package com.application.bmiantiobesity.ui.main

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.models.*
import com.application.bmiantiobesity.utilits.roundTo
import kotlin.math.roundToInt

enum class DialogType{
    DIALOG_SEEKBAR, DIALOG_PICKER, DIALOG_DOUBLE
}

class DialogsClasses(private val viewModel: MainViewModel, private val typeDialog: DialogType, private val typeData: DataTypeInTime, private val value: Float?, private val secondValue: Int?) : DialogFragment(){


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        context?.let {
            when (typeDialog) {
                DialogType.DIALOG_SEEKBAR -> {
                    val builder = AlertDialog.Builder(it)

                    val view = activity?.layoutInflater?.inflate(R.layout.dialog_set_int, null)

                    val textView = view?.findViewById<TextView>(R.id.dialog_textView)
                    val seekBar = view?.findViewById<SeekBar>(R.id.dialog_seekBar)
                    seekBar?.max = 100
                    //seekBar?.min = 0
                    seekBar?.progress = 50
                    seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            textView?.text = "${it.getText(R.string.data)} - $progress"
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

                    })

                    builder.setView(view)
                        .setPositiveButton(R.string.save) { dialogInterface, _ ->
                            viewModel.intData.value = seekBar?.progress
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                            dialogInterface.cancel()
                        }

                    return builder.create()
                }
                DialogType.DIALOG_PICKER ->{

                    val builder = when (typeData) {
                        /*DataTypeInTime.BLOOD_PRESSURE_DIA -> showNumberPickerDialog(getText(R.string.blood_pressure_dia).toString(), value ?: 0f, 0f..200f, 0.01f,
                            {rangeValue -> rangeValue.toString()},
                            {action -> Toast.makeText(this.requireContext(), action.toString(), Toast.LENGTH_SHORT).show()}) */
                        DataTypeInTime.HEIGHT -> when (MainViewModel.measuringSystem) {
                            MeasuringSystem.SI -> showNumberPickerDialog(getText(R.string.height).toString(),value ?: 170f,100f..300f,1f,0,
                                { rangeValue -> "$rangeValue ${getText(R.string.cm)}" },
                                { action -> updateDashBoard(action) })
                            MeasuringSystem.IMPERIAL -> showNumberPickerDialog(getText(R.string.height).toString(),
                                value ?: converterSmToIn(170f)!!, converterSmToIn(100f)!!..converterSmToIn(300f)!!,0.5f,2,
                                { rangeValue -> "$rangeValue ${getText(R.string.inch)}" },
                                { action -> updateDashBoard(action) })
                        }
                        DataTypeInTime.WEIGHT -> when (MainViewModel.measuringSystem) {
                            MeasuringSystem.SI -> showNumberPickerDialog(getText(R.string.weight).toString(), value ?: 70f, 10f..300f, 0.5f, 1,
                                {rangeValue -> "$rangeValue ${getText(R.string.kg)}"},
                                {action -> updateDashBoard(action)})
                            MeasuringSystem.IMPERIAL -> showNumberPickerDialog(getText(R.string.weight).toString(),
                                value ?: converterKgToLb(70f)!!, converterKgToLb(10f)!!..converterKgToLb(300f)!!,0.25f,2,
                                { rangeValue -> "$rangeValue ${getText(R.string.funt)}" },
                                { action -> updateDashBoard(action) })
                            }
                        DataTypeInTime.HIP -> when (MainViewModel.measuringSystem) {
                            MeasuringSystem.SI -> showNumberPickerDialog(getText(R.string.hip).toString(), value ?: 90f, 30f..200f, 0.5f, 1,
                                {rangeValue -> "$rangeValue ${getText(R.string.cm)}"},
                                {action -> updateDashBoard(action) })
                            MeasuringSystem.IMPERIAL -> showNumberPickerDialog(getText(R.string.hip).toString(),
                                value ?: converterSmToIn(
                                    90f
                                )!!,
                                converterSmToIn(
                                    30f
                                )!!..converterSmToIn(
                                    200f
                                )!!,0.25f,2,
                                { rangeValue -> "$rangeValue ${getText(R.string.inch)}" },
                                { action -> updateDashBoard(action) })
                        }
                        DataTypeInTime.NECK -> when (MainViewModel.measuringSystem) {
                            MeasuringSystem.SI -> showNumberPickerDialog(getText(R.string.neck).toString(), value ?: 25f, 1f..150f, 0.5f, 1,
                                {rangeValue -> "$rangeValue ${getText(R.string.cm)}"},
                                {action -> updateDashBoard(action) })
                            MeasuringSystem.IMPERIAL -> showNumberPickerDialog(getText(R.string.neck).toString(),
                                value ?: converterSmToIn(
                                    25f
                                )!!,
                                converterSmToIn(
                                    1f
                                )!!..converterSmToIn(
                                    150f
                                )!!,0.25f,2,
                                { rangeValue -> "$rangeValue ${getText(R.string.inch)}" },
                                { action -> updateDashBoard(action) })
                        }
                        DataTypeInTime.WAIST -> when (MainViewModel.measuringSystem) {
                            MeasuringSystem.SI -> showNumberPickerDialog(getText(R.string.waist).toString(), value ?: 70f, 30f..200f, 0.5f, 1,
                                {rangeValue -> "$rangeValue ${getText(R.string.cm)}"},
                                {action -> updateDashBoard(action) })
                            MeasuringSystem.IMPERIAL -> showNumberPickerDialog(getText(R.string.waist).toString(),
                                value ?: converterSmToIn(
                                    70f
                                )!!,
                                converterSmToIn(
                                    30f
                                )!!..converterSmToIn(
                                    200f
                                )!!,0.25f,2,
                                { rangeValue -> "$rangeValue ${getText(R.string.inch)}" },
                                { action -> updateDashBoard(action) })
                        }
                        DataTypeInTime.WRIST -> when (MainViewModel.measuringSystem) {
                            MeasuringSystem.SI -> showNumberPickerDialog(getText(R.string.wrist).toString(), value ?: 15f, 5f..50f, 0.5f,1,
                                {rangeValue -> "$rangeValue ${getText(R.string.cm)}"},
                                {action -> updateDashBoard(action) })
                            MeasuringSystem.IMPERIAL -> showNumberPickerDialog(getText(R.string.wrist).toString(),
                                value ?: converterSmToIn(
                                    15f
                                )!!,
                                converterSmToIn(
                                    5f
                                )!!..converterSmToIn(
                                    50f
                                )!!,0.25f,2,
                                { rangeValue -> "$rangeValue ${getText(R.string.inch)}" },
                                { action -> updateDashBoard(action) })
                        }
                        DataTypeInTime.HEARTS_RATE_ALONE -> showNumberPickerDialog(getText(R.string.heart_rate_alone).toString(), value ?: 70f, 1f..400f, 1f,1,
                            {rangeValue -> "$rangeValue ${getText(R.string.ud_min)}"},
                            {action -> updateDashBoard(action)})
                        DataTypeInTime.HEARTS_RATE_VARIABILITY -> showNumberPickerDialog(getText(R.string.heart_rate_variability).toString(), value ?: 70f, 1f..400f, 1f,0,
                            {rangeValue -> "$rangeValue ${getText(R.string.ud_min)}"},
                            {action -> updateDashBoard(action)})
                        DataTypeInTime.CHOLESTEROL ->  when (MainViewModel.measuringSystem) {
                            MeasuringSystem.SI -> showNumberPickerDialog(getText(R.string.cholesterol).toString(),
                                value ?: 4f,
                                1f..40f,
                                0.1f,
                                1,
                                { rangeValue -> "$rangeValue ${getText(R.string.mmol_l)}" },
                                { action -> updateDashBoard(action) })
                            MeasuringSystem.IMPERIAL -> showNumberPickerDialog(getText(R.string.cholesterol).toString(),
                                value ?: converterMmolLToMgDl(4f)!!,
                                converterMmolLToMgDl(1f)!!..converterMmolLToMgDl(40f)!!,
                                0.02f,
                                2,
                                { rangeValue -> "$rangeValue ${getText(R.string.mg_dl)}" },
                                { action -> updateDashBoard(action) })
                        }
                        DataTypeInTime.GLUCOSE -> when (MainViewModel.measuringSystem) {
                            MeasuringSystem.SI -> showNumberPickerDialog(getText(R.string.glucose).toString(),
                                value ?: 4f,
                                1f..40f,
                                0.1f,
                                1,
                                { rangeValue -> "$rangeValue ${getText(R.string.mmol_l)}" },
                                { action -> updateDashBoard(action) })
                            MeasuringSystem.IMPERIAL -> showNumberPickerDialog(getText(R.string.glucose).toString(),
                                value ?: converterMmolLToMgDl(4f)!!,
                                converterMmolLToMgDl(1f)!!..converterMmolLToMgDl(40f)!!,
                                0.02f,
                                2,
                                { rangeValue -> "$rangeValue ${getText(R.string.mg_dl)}" },
                                { action -> updateDashBoard(action) })
                        }
                        else -> showNumberPickerDialog(getText(R.string.input_value).toString(), value ?: 0f, 0f..200f, 1f, 0,
                            {rangeValue -> "$rangeValue ${getText(R.string.cm)}"},
                            {action -> Toast.makeText(this.requireContext(), action.toString(), Toast.LENGTH_SHORT).show()})
                    }
                    return builder.create()
                }
                DialogType.DIALOG_DOUBLE -> {
                    val builder = AlertDialog.Builder(it)

                    val view = activity?.layoutInflater?.inflate(R.layout.double_number_picker, null)

                    val devider = view?.findViewById<TextView>(R.id.separate_picker)
                    devider?.text = getText(R.string.local_separator_slash)

                    val numberFirst = view?.findViewById<NumberPicker>(R.id.first_picker)
                    numberFirst?.maxValue = when (MainViewModel.measuringSystem)
                    {   MeasuringSystem.SI -> 300
                        MeasuringSystem.IMPERIAL -> converterMmRtStToKPa(300f)!!.toInt() }
                    numberFirst?.minValue = when (MainViewModel.measuringSystem)
                    {   MeasuringSystem.SI -> 1
                        MeasuringSystem.IMPERIAL -> converterMmRtStToKPa(1f)!!.toInt() }
                    numberFirst?.value = when (MainViewModel.measuringSystem)
                    {   MeasuringSystem.SI -> value?.roundToInt() ?: 120
                        MeasuringSystem.IMPERIAL -> (value ?: 16f).toInt() }
                    val numberSecond = view?.findViewById<NumberPicker>(R.id.second_picker)
                    numberSecond?.maxValue = when (MainViewModel.measuringSystem)
                    {   MeasuringSystem.SI -> 200
                        MeasuringSystem.IMPERIAL -> converterMmRtStToKPa(200f)!!.toInt() }
                    numberSecond?.minValue = when (MainViewModel.measuringSystem)
                    {   MeasuringSystem.SI -> 1
                        MeasuringSystem.IMPERIAL -> converterMmRtStToKPa(1f)!!.toInt() }
                    numberSecond?.value = when (MainViewModel.measuringSystem)
                    {   MeasuringSystem.SI -> secondValue ?: 80
                        MeasuringSystem.IMPERIAL -> (secondValue?.toFloat() ?: 10f).toInt() }


                    val titleString = "${getString(R.string.blood_pressure)} " + when (MainViewModel.measuringSystem)
                            {   MeasuringSystem.SI -> getString(R.string.mm_rt_st)
                                MeasuringSystem.IMPERIAL -> getString(R.string.k_pa) }

                    builder.setTitle(titleString)
                    builder.setView(view)
                        .setPositiveButton(R.string.save) { dialogInterface, _ ->
                            //Toast.makeText(this.requireContext(), "${numberFirst?.value} / ${numberSecond?.value}", Toast.LENGTH_SHORT).show()
                            MainViewModel.singleDashBoard?.updateField(
                                SetNewDataValue(
                                    DataTypeInTime.BLOOD_PRESSURE_SYS,
                                    when (MainViewModel.measuringSystem)
                                    {   MeasuringSystem.SI -> numberFirst?.value.toString()
                                        MeasuringSystem.IMPERIAL -> converterKPaToMmRtSt(numberFirst?.value?.toFloat()).roundTo(1).toString() },
                                    System.currentTimeMillis())
                            )
                            MainViewModel.singleDashBoard?.updateField(
                                SetNewDataValue(
                                    DataTypeInTime.BLOOD_PRESSURE_DIA,
                                    when (MainViewModel.measuringSystem)
                                    {   MeasuringSystem.SI -> numberSecond?.value.toString()
                                        MeasuringSystem.IMPERIAL -> converterKPaToMmRtSt(numberSecond?.value?.toFloat()).roundTo(1).toString() },
                                    System.currentTimeMillis())
                            )
                            Log.d("Dialog Blood Update db", MainViewModel.singleDashBoard.toString()) // Проверить в живую не накапливаются значения!!!
                            MainViewModel.singleDashBoard?.let { viewModel.setDashBoardToInternet(it)}
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                            dialogInterface.cancel()
                        }

                    return builder.create()
                }
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    private fun updateDashBoard(action: Float) {
        val result = when (MainViewModel.measuringSystem){
            MeasuringSystem.SI -> action
            MeasuringSystem.IMPERIAL -> when (typeData) {
                DataTypeInTime.HEIGHT,  DataTypeInTime.HIP,  DataTypeInTime.WAIST,  DataTypeInTime.WRIST, DataTypeInTime.NECK ->
                    converterInToSm(action)?.roundTo(1)
                DataTypeInTime.WEIGHT -> converterLbToKg(action)?.roundTo(1)
                DataTypeInTime.GLUCOSE, DataTypeInTime.CHOLESTEROL -> converterMgDlToMmolL(action)?.roundTo(1)
                else -> action
            }
        }

        MainViewModel.singleDashBoard?.updateField(
            SetNewDataValue(typeData, result.toString(), System.currentTimeMillis())
        )
        //viewModel.setModelDashBoard(MainViewModel.singleDashBoard!!)
        Log.d("Dialog Update db", MainViewModel.singleDashBoard.toString()) // Проверить в живую не накапливаются значения!!!
        MainViewModel.singleDashBoard?.let { viewModel.setDashBoardToInternet(it) }
    }
}

// Функция расширения по соднаю диалога
fun Fragment.showNumberPickerDialog(
    title: String,
    value: Float,
    range: ClosedRange<Float>,
    stepSize: Float,
    roundTo: Int,
    formatToString: (Float) -> String,
    valueChooseAction: (Float) -> Unit
):AlertDialog.Builder {
    val numberPicker = NumberPicker(context).apply {
        setFormatter {  formatToString((it.toFloat() * stepSize).roundTo(roundTo) ?: 0f) }
        wrapSelectorWheel = false

        minValue = (range.start / stepSize).toInt()
        maxValue = (range.endInclusive / stepSize).toInt()
        this.value = (value / stepSize).toInt()


        // NOTE: workaround for a bug that rendered the selected value wrong until user scrolled, see also: https://stackoverflow.com/q/27343772/3451975
        (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }.get(this) as EditText).filters = emptyArray()
    }

    return AlertDialog.Builder(context!!)
        .setTitle(title)
        .setView(numberPicker)
        .setPositiveButton(R.string.save) { dialogInterface, _ ->
            valueChooseAction((numberPicker.value.toFloat() * stepSize).roundTo(roundTo) ?: 0f)
            dialogInterface.dismiss()
        }
        .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
            dialogInterface.cancel()
        }
}

