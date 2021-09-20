package com.application.bmiobesity.view.mainActivity.medcard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.application.bmiobesity.R
import com.application.bmiobesity.common.MeasuringSystem
import com.application.bmiobesity.common.parameters.AvailableParameters
import com.application.bmiobesity.common.parameters.DailyActivityLevels
import com.application.bmiobesity.databinding.MainMedcardFragmentBinding
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.utils.getFirstNumber
import com.application.bmiobesity.utils.getFloatFromTwoInt
import com.application.bmiobesity.utils.getSecondNumber
import com.application.bmiobesity.view.mainActivity.MainActivity
import com.application.bmiobesity.viewModels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MedcardFragment : Fragment(R.layout.main_medcard_fragment) {

    private var medcardBinding: MainMedcardFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()
    private lateinit var sourceTypeSpinnerAdapter: ArrayAdapter<MedCardSourceType>
    private lateinit var dialogListSpinnerAdapter: ArrayAdapter<String>
    private var currentParameters: MutableMap<String, MedCardParamSetting> = mutableMapOf()

    // Single number picker dialog
    private lateinit var dialogSingleView: View
    private lateinit var dialogSingleViewTitle: TextView
    private lateinit var dialogSingleViewUnit: TextView
    private lateinit var dialogSingleViewDescription: TextView
    private lateinit var dialogSingleViewPicker: NumberPicker
    private lateinit var dialogSingleViewSourceSpinner: Spinner

    // Double number picker dialog
    private lateinit var dialogDoubleView: View
    private lateinit var dialogDoubleViewTitle: TextView
    private lateinit var dialogDoubleViewUnit: TextView
    private lateinit var dialogDoubleViewDescription: TextView
    private lateinit var dialogDoubleViewPicker: NumberPicker
    private lateinit var dialogDoubleViewPicker2: NumberPicker
    private lateinit var dialogDoubleViewSourceSpinner: Spinner

    // List dialog
    private lateinit var dialogListView: View
    private lateinit var dialogListViewTitle: TextView
    private lateinit var dialogListViewDescription: TextView
    private lateinit var dialogListViewSourceSpinner: Spinner
    private lateinit var dialogListViewValueSpinner: Spinner

    // Dialog builders
    private lateinit var dialogBuilder: MaterialAlertDialogBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        medcardBinding = MainMedcardFragmentBinding.bind(view)

        var isFirstTime: Boolean

        arguments?.let {
            isFirstTime = it.getBoolean("isFirstTime")
            if (isFirstTime) {
                medcardBinding?.medCardButtonDone?.visibility = View.VISIBLE
                showFirsTimeInfoDialog()
            } else {
                medcardBinding?.medCardButtonDone?.visibility = View.GONE
            }
        }

        init()
        initRecycler()
        initListeners()
    }

    private fun init(){
        sourceTypeSpinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item, mainModel.medCardSourceType)
        val dailyLevels = arrayListOf<String>(
            getString(R.string.medcard_name_daily_minimum),
            getString(R.string.medcard_name_daily_lower),
            getString(R.string.medcard_name_daily_medium),
            getString(R.string.medcard_name_daily_high),
            getString(R.string.medcard_name_daily_very_high))
        dialogListSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, dailyLevels)
        // Single number picker dialog
        dialogSingleView = layoutInflater.inflate(R.layout.main_medcard_inputdialog_single, null)
        dialogSingleViewTitle = dialogSingleView.findViewById(R.id.medCardDialogTitle)
        dialogSingleViewUnit = dialogSingleView.findViewById(R.id.medCardDialogUnit)
        dialogSingleViewDescription = dialogSingleView.findViewById(R.id.medCardDialogDescription)
        dialogSingleViewPicker = dialogSingleView.findViewById(R.id.medCardDialogPicker)
        dialogSingleViewSourceSpinner = dialogSingleView.findViewById(R.id.medCardDialogSpinnerSourceType)
        // Double number picker dialog
        dialogDoubleView = layoutInflater.inflate(R.layout.main_medcard_inputdialog_double, null)
        dialogDoubleViewTitle = dialogDoubleView.findViewById(R.id.medCardDoubleDialogTitle)
        dialogDoubleViewUnit = dialogDoubleView.findViewById(R.id.medCardDoubleDialogUnit)
        dialogDoubleViewDescription = dialogDoubleView.findViewById(R.id.medCardDoubleDialogDescription)
        dialogDoubleViewPicker = dialogDoubleView.findViewById(R.id.medCardDoubleDialogPicker)
        dialogDoubleViewPicker2 = dialogDoubleView.findViewById(R.id.medCardDoubleDialogPicker2)
        dialogDoubleViewSourceSpinner = dialogDoubleView.findViewById(R.id.medCardDoubleDialogSpinnerSourceType)
        // List dialog
        dialogListView = layoutInflater.inflate(R.layout.main_medcard_inputdialog_list, null)
        dialogListViewTitle = dialogListView.findViewById(R.id.medCardListDialogTitle)
        dialogListViewDescription = dialogListView.findViewById(R.id.medCardListDialogDescription)
        dialogListViewSourceSpinner = dialogListView.findViewById(R.id.medCardListDialogSpinnerSourceType)
        dialogListViewValueSpinner = dialogListView.findViewById(R.id.medCardListDialogSpinnerValue)
        dialogListViewValueSpinner.adapter = dialogListSpinnerAdapter
        //
        dialogSingleViewSourceSpinner.adapter = sourceTypeSpinnerAdapter
        dialogDoubleViewSourceSpinner.adapter = sourceTypeSpinnerAdapter
        dialogListViewSourceSpinner.adapter = sourceTypeSpinnerAdapter
        dialogDoubleViewPicker2.minValue = 0
        dialogDoubleViewPicker2.maxValue = 9
    }
    private fun initRecycler(){
        val medCardAdapter = MedCardAdapterRecycler(mainModel.paramUnit, mainModel.medCardSourceType) { onClickMedCard(it) }
        medcardBinding?.mainMedCardRecycler?.adapter = medCardAdapter
        mainModel.medCard.parametersLive.observe(viewLifecycleOwner, {
            it?.let {
                medCardAdapter.submitList(it.values.toList() as MutableList<MedCardParamSetting>)
                medCardAdapter.notifyDataSetChanged()
            }
        })
    }
    private fun initListeners(){
        mainModel.medCard.parametersLive.observe(viewLifecycleOwner, {
            it?.let {
                currentParameters = it
            }
        })

        mainModel.profileManager.currentAvailableData.observe(viewLifecycleOwner, {
            it?.let {
                medcardBinding?.medCardButtonDone?.isEnabled = it.getMedCardAvailable()
            }
        })

        medcardBinding?.medCardButtonDone?.setOnClickListener {
            lifecycleScope.launch {
                mainModel.setFirstTime(false).join()
                withContext(Dispatchers.Main){
                    requireActivity().finish()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        mainModel.profileManager.currentMeasurementSystem.observe(viewLifecycleOwner, {
            it?.let {
                mainModel.medCard.setPreferMeasurementSystem(it)
            }
        })
    }

    private fun onClickMedCard(item: MedCardParamSetting){
        when (item.id) {
            AvailableParameters.WEIGHT.id -> {showIntAndIntDialog(item)}
            AvailableParameters.HIP.id -> {showIntAndFloatDialog(item)}
            AvailableParameters.WAIST.id -> {showIntAndFloatDialog(item)}
            AvailableParameters.WRIST.id -> {showIntAndFloatDialog(item)}
            AvailableParameters.NECK.id -> {showIntAndFloatDialog(item)}
            AvailableParameters.HEART_RATE_ALONE.id -> {showIntAndIntDialog(item)}
            AvailableParameters.DAILY_ACTIVITY_LEVEL.id -> {showListDialog(item)}
            AvailableParameters.BLOOD_PRESSURE_SYSTOLIC.id -> {showIntAndFloatDialog(item)}
            AvailableParameters.BLOOD_PRESSURE_DIASTOLIC.id -> {showIntAndFloatDialog(item)}
            AvailableParameters.CHOLESTEROL.id -> {showFloatAndIntDialog(item)}
            AvailableParameters.GLUCOSE.id -> {showFloatAndIntDialog(item)}
        }
    }

    private fun showIntAndIntDialog(item: MedCardParamSetting){
        dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setNegativeButton(getString(R.string.button_cancel)){_, _ -> }
        dialogBuilder.setView(dialogSingleView)
        dialogBuilder.setOnDismissListener {
            val parent = dialogSingleView.parent as ViewGroup
            parent.removeView(dialogSingleView)
        }

        val unit = mainModel.paramUnit.findLast { item.unitID == it.id }
        val sourceType = mainModel.medCardSourceType.findLast { item.sourceTypeID == it.id }
        val sourceTypePosition = sourceTypeSpinnerAdapter.getPosition(sourceType)

        dialogSingleViewTitle.setText(resources.getIdentifier(item.nameRes, "string", "com.application.bmiobesity"))
        dialogSingleViewDescription.setText(resources.getIdentifier(item.longDescriptionRes, "string", "com.application.bmiobesity"))
        dialogSingleViewSourceSpinner.setSelection(sourceTypePosition)
        dialogSingleViewSourceSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                val srcType = sourceTypeSpinnerAdapter.getItem(position)
                srcType?.let {
                    mainModel.medCard.setSourceType(it, item)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        when(item.preferMeasuringSystem){
            MeasuringSystem.METRIC.id -> {
                dialogSingleViewUnit.setText(resources.getIdentifier(unit?.nameMetricRes, "string", "com.application.bmiobesity"))
                dialogSingleViewPicker.minValue = item.minMetricValue
                dialogSingleViewPicker.maxValue = item.maxMetricValue
                val value = item.values.lastOrNull()?.value?.toInt()
                if (value == null){
                    dialogSingleViewPicker.value = item.defaultValue
                } else {
                    dialogSingleViewPicker.value = value
                }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ -> setMetricValue(dialogSingleViewPicker.value.toFloat(), item) }
                dialogBuilder.show()
            }
            MeasuringSystem.IMPERIAL.id -> {
                dialogSingleViewUnit.setText(resources.getIdentifier(unit?.nameImperialRes, "string", "com.application.bmiobesity"))
                dialogSingleViewPicker.minValue = item.minImpValue
                dialogSingleViewPicker.maxValue = item.maxImpValue
                val valueImp = item.values.lastOrNull()?.valueImp?.toInt()
                if (valueImp == null){
                    dialogSingleViewPicker.value = item.defaultValueImp
                } else {
                    dialogSingleViewPicker.value = valueImp
                }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ -> setImpValue(dialogSingleViewPicker.value.toFloat(), item) }
                dialogBuilder.show()
            }
        }
    }
    private fun showIntAndFloatDialog(item: MedCardParamSetting){
        dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setNegativeButton(getString(R.string.button_cancel)){_, _ -> }

        val unit = mainModel.paramUnit.findLast { item.unitID == it.id }
        val sourceType = mainModel.medCardSourceType.findLast { item.sourceTypeID == it.id }
        val sourceTypePosition = sourceTypeSpinnerAdapter.getPosition(sourceType)

        when(item.preferMeasuringSystem){
            MeasuringSystem.METRIC.id -> {
                dialogBuilder.setView(dialogSingleView)
                dialogBuilder.setOnDismissListener {
                    val parent = dialogSingleView.parent as ViewGroup
                    parent.removeView(dialogSingleView)
                }
                dialogSingleViewTitle.setText(resources.getIdentifier(item.nameRes, "string", "com.application.bmiobesity"))
                dialogSingleViewDescription.setText(resources.getIdentifier(item.longDescriptionRes, "string", "com.application.bmiobesity"))
                dialogSingleViewUnit.setText(resources.getIdentifier(unit?.nameMetricRes, "string", "com.application.bmiobesity"))
                dialogSingleViewPicker.minValue = item.minMetricValue
                dialogSingleViewPicker.maxValue = item.maxMetricValue
                val value = item.values.lastOrNull()?.value?.toInt()
                if (value == null){
                    dialogSingleViewPicker.value = item.defaultValue
                } else {
                    dialogSingleViewPicker.value = value
                }
                dialogSingleViewSourceSpinner.setSelection(sourceTypePosition)
                dialogSingleViewSourceSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                        val srcType = sourceTypeSpinnerAdapter.getItem(position)
                        srcType?.let {
                            mainModel.medCard.setSourceType(it, item)
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ -> setMetricValue(dialogSingleViewPicker.value.toFloat(), item)}
                dialogBuilder.show()
            }
            MeasuringSystem.IMPERIAL.id -> {
                dialogBuilder.setView(dialogDoubleView)
                dialogBuilder.setOnDismissListener {
                    val parent = dialogDoubleView.parent as ViewGroup
                    parent.removeView(dialogDoubleView)
                }
                dialogDoubleViewTitle.setText(resources.getIdentifier(item.nameRes, "string", "com.application.bmiobesity"))
                dialogDoubleViewDescription.setText(resources.getIdentifier(item.longDescriptionRes, "string", "com.application.bmiobesity"))
                dialogDoubleViewUnit.setText(resources.getIdentifier(unit?.nameImperialRes, "string", "com.application.bmiobesity"))
                dialogDoubleViewPicker.minValue = item.minImpValue
                dialogDoubleViewPicker.maxValue = item.maxImpValue
                val valueImp = item.values.lastOrNull()?.valueImp
                if (valueImp == null){
                    dialogDoubleViewPicker.value = item.defaultValueImp
                } else {
                    dialogDoubleViewPicker.value = getFirstNumber(valueImp)
                    dialogDoubleViewPicker2.value = getSecondNumber(valueImp)
                }
                dialogDoubleViewSourceSpinner.setSelection(sourceTypePosition)
                dialogDoubleViewSourceSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                        val srcType = sourceTypeSpinnerAdapter.getItem(position)
                        srcType?.let {
                            mainModel.medCard.setSourceType(it, item)
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ ->
                    val val1 = dialogDoubleViewPicker.value
                    val val2 = dialogDoubleViewPicker2.value
                    setImpValue(getFloatFromTwoInt(val1, val2), item)
                }
                dialogBuilder.show()
            }
        }
    }
    private fun showFloatAndIntDialog(item: MedCardParamSetting){
        dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setNegativeButton(getString(R.string.button_cancel)){_, _ -> }

        val unit = mainModel.paramUnit.findLast { item.unitID == it.id }
        val sourceType = mainModel.medCardSourceType.findLast { item.sourceTypeID == it.id }
        val sourceTypePosition = sourceTypeSpinnerAdapter.getPosition(sourceType)

        when(item.preferMeasuringSystem){
            MeasuringSystem.METRIC.id -> {
                dialogBuilder.setView(dialogDoubleView)
                dialogBuilder.setOnDismissListener {
                    val parent = dialogDoubleView.parent as ViewGroup
                    parent.removeView(dialogDoubleView)
                }
                dialogDoubleViewTitle.setText(resources.getIdentifier(item.nameRes, "string", "com.application.bmiobesity"))
                dialogDoubleViewDescription.setText(resources.getIdentifier(item.longDescriptionRes, "string", "com.application.bmiobesity"))
                dialogDoubleViewUnit.setText(resources.getIdentifier(unit?.nameMetricRes, "string", "com.application.bmiobesity"))
                dialogDoubleViewPicker.minValue = item.minMetricValue
                dialogDoubleViewPicker.maxValue = item.maxMetricValue
                val value = item.values.lastOrNull()?.value
                if (value == null){
                    dialogDoubleViewPicker.value = item.defaultValue
                } else {
                    dialogDoubleViewPicker.value = getFirstNumber(value)
                    dialogDoubleViewPicker2.value = getSecondNumber(value)
                }
                dialogDoubleViewSourceSpinner.setSelection(sourceTypePosition)
                dialogDoubleViewSourceSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                        val srcType = sourceTypeSpinnerAdapter.getItem(position)
                        srcType?.let {
                            mainModel.medCard.setSourceType(it, item)
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ ->
                    val val1 = dialogDoubleViewPicker.value
                    val val2 = dialogDoubleViewPicker2.value
                    setMetricValue(getFloatFromTwoInt(val1, val2), item)
                }
                dialogBuilder.show()
            }
            MeasuringSystem.IMPERIAL.id -> {
                dialogBuilder.setView(dialogSingleView)
                dialogBuilder.setOnDismissListener {
                    val parent = dialogSingleView.parent as ViewGroup
                    parent.removeView(dialogSingleView)
                }
                dialogSingleViewTitle.setText(resources.getIdentifier(item.nameRes, "string", "com.application.bmiobesity"))
                dialogSingleViewDescription.setText(resources.getIdentifier(item.longDescriptionRes, "string", "com.application.bmiobesity"))
                dialogSingleViewUnit.setText(resources.getIdentifier(unit?.nameImperialRes, "string", "com.application.bmiobesity"))
                dialogSingleViewPicker.minValue = item.minImpValue
                dialogSingleViewPicker.maxValue = item.maxImpValue
                val valueImp = item.values.lastOrNull()?.valueImp?.toInt()
                if (valueImp == null){
                    dialogSingleViewPicker.value = item.defaultValueImp
                } else {
                    dialogSingleViewPicker.value = valueImp
                }
                dialogSingleViewSourceSpinner.setSelection(sourceTypePosition)
                dialogSingleViewSourceSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                        val srcType = sourceTypeSpinnerAdapter.getItem(position)
                        srcType?.let {
                            mainModel.medCard.setSourceType(it, item)
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ -> setImpValue(dialogSingleViewPicker.value.toFloat(), item)}
                dialogBuilder.show()
            }
        }
    }
    private fun showListDialog(item: MedCardParamSetting){
        dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setNegativeButton(getString(R.string.button_cancel)){_, _ -> }
        dialogBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ ->
            val position = dialogListViewValueSpinner.selectedItemPosition
            val choiceActivityLevel = DailyActivityLevels.values()[position]
            setMetricValue(choiceActivityLevel.id, item)
        }
        dialogBuilder.setView(dialogListView)
        dialogBuilder.setOnDismissListener {
            val parent = dialogListView.parent as ViewGroup
            parent.removeView(dialogListView)
        }

        dialogListViewTitle.setText(resources.getIdentifier(item.nameRes, "string", "com.application.bmiobesity"))
        val sourceType = mainModel.medCardSourceType.findLast { item.sourceTypeID == it.id }
        val sourceTypePosition = sourceTypeSpinnerAdapter.getPosition(sourceType)
        dialogListViewSourceSpinner.setSelection(sourceTypePosition)

        val currValue = when(item.values.lastOrNull()?.value){
            DailyActivityLevels.MINIMUM.id -> {DailyActivityLevels.MINIMUM}
            DailyActivityLevels.LOWER.id -> {DailyActivityLevels.LOWER}
            DailyActivityLevels.MEDIUM.id -> {DailyActivityLevels.MEDIUM}
            DailyActivityLevels.HIGH.id -> {DailyActivityLevels.HIGH}
            DailyActivityLevels.VERY_HIGH.id -> {DailyActivityLevels.VERY_HIGH}
            else -> {DailyActivityLevels.MEDIUM}
        }

        dialogListViewDescription.setText(resources.getIdentifier(currValue.descriptionRes, "string", "com.application.bmiobesity"))
        dialogListViewValueSpinner.setSelection(currValue.pos)

        dialogListViewValueSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                val choiceActivityLevel = DailyActivityLevels.values()[position]
                dialogListViewDescription.setText(resources.getIdentifier(choiceActivityLevel.descriptionRes, "string", "com.application.bmiobesity"))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        dialogListViewSourceSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                val srcType = sourceTypeSpinnerAdapter.getItem(position)
                srcType?.let {
                    mainModel.medCard.setSourceType(it, item)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        dialogBuilder.show()
    }
    private fun showFirsTimeInfoDialog(){
        val infoDialog = MaterialAlertDialogBuilder(requireContext())
        infoDialog.setPositiveButton(getString(R.string.button_ok)){_, _ -> }
        infoDialog.setTitle(getString(R.string.medcard_first_dialog_title))
        infoDialog.setMessage(getString(R.string.medcard_first_dialog_message))
        infoDialog.show()
    }

    private fun setMetricValue(v: Float, p: MedCardParamSetting){
        mainModel.medCard.setMetricValue(v, p)
        mainModel.updateMedCard()
    }
    private fun setImpValue(v: Float, p: MedCardParamSetting){
        mainModel.medCard.setImpValue(v, p)
        mainModel.updateMedCard()
    }

    override fun onDestroyView() {
        medcardBinding = null
        super.onDestroyView()
    }
}