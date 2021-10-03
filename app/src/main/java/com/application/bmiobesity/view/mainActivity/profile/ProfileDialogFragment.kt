package com.application.bmiobesity.view.mainActivity.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.R
import com.application.bmiobesity.common.MeasuringSystem
import com.application.bmiobesity.common.parameters.AvailableParameters
import com.application.bmiobesity.common.parameters.DailyActivityLevels
import com.application.bmiobesity.databinding.MainProfileDialogFragmentBinding
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardSourceType
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile
import com.application.bmiobesity.utils.*
import com.application.bmiobesity.view.mainActivity.MainActivity
import com.application.bmiobesity.view.mainActivity.medcard.MedCardAdapterRecycler
import com.application.bmiobesity.view.mainActivity.profile.country.ProfileCountryDialogFragment
import com.application.bmiobesity.viewModels.MainViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.text.SimpleDateFormat
import java.util.*

class ProfileDialogFragment : DialogFragment(R.layout.main_profile_dialog_fragment) {

    private var profileBinding: MainProfileDialogFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()

    private lateinit var currentProfile: Profile
    private var currentMeasuringSystem = MeasuringSystem.METRIC
    private lateinit var allDisposable: CompositeDisposable
    private lateinit var availableData: AvailableData
    private lateinit var availableDataSubject: Subject<Boolean>

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var datePickerBuilder: MaterialDatePicker.Builder<Long>
    private lateinit var datePickerConstraintsBuilder: CalendarConstraints.Builder

    private lateinit var gendersAdapter: ListAdapter
    private lateinit var dialogGendersBuilder: MaterialAlertDialogBuilder

    private lateinit var dialogHeightBuilder: MaterialAlertDialogBuilder
    private lateinit var dialogHeightView: View
    private lateinit var heightNumberPicker: NumberPicker

    private lateinit var smokerAdapter: ListAdapter
    private lateinit var dialogSmokerBuilder: MaterialAlertDialogBuilder

    private lateinit var countriesAdapter: ListAdapter
    private lateinit var dialogCountriesBuilder: MaterialAlertDialogBuilder

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding = MainProfileDialogFragmentBinding.bind(view)
        profileBinding?.vm = mainModel
        profileBinding?.view = this
        profileBinding?.lifecycleOwner = this
        init()
        initListeners()
        setupRecyclerView()
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    @SuppressLint("InflateParams")
    private fun init() {
        allDisposable = CompositeDisposable()
        availableData = AvailableData("")
        availableDataSubject = PublishSubject.create()

        // Init date picker calendar
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentYear = calendar.get(Calendar.YEAR)
        calendar.set(Calendar.YEAR, currentYear - 150)
        val startTime = calendar.timeInMillis
        calendar.clear()
        calendar.set(Calendar.YEAR, currentYear - 1)
        val endTime = calendar.timeInMillis
        datePickerConstraintsBuilder = CalendarConstraints.Builder()
        datePickerConstraintsBuilder.setStart(startTime)
        datePickerConstraintsBuilder.setEnd(endTime)

        // Init adapters
        dialogHeightView = layoutInflater.inflate(R.layout.main_profile_dialog_height, null)
        heightNumberPicker = dialogHeightView.findViewById(R.id.profileHeightNumberPicker)
        gendersAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.select_dialog_singlechoice,
            mainModel.genders
        )
        smokerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.select_dialog_singlechoice,
            arrayOf(getString(R.string.button_yes), getString(R.string.button_no))
        )
        countriesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.select_dialog_singlechoice,
            mainModel.countries.filter { c -> c.value.isNotEmpty()}
        )
        sourceTypeSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mainModel.medCardSourceType
        )
        val dailyLevels = arrayListOf<String>(
            getString(R.string.medcard_name_daily_minimum),
            getString(R.string.medcard_name_daily_lower),
            getString(R.string.medcard_name_daily_medium),
            getString(R.string.medcard_name_daily_high),
            getString(R.string.medcard_name_daily_very_high)
        )
        dialogListSpinnerAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            dailyLevels
        )
        // Single number picker dialog
        dialogSingleView = layoutInflater.inflate(R.layout.main_medcard_inputdialog_single, null)
        dialogSingleViewTitle = dialogSingleView.findViewById(R.id.medCardDialogTitle)
        dialogSingleViewUnit = dialogSingleView.findViewById(R.id.medCardDialogUnit)
        dialogSingleViewDescription = dialogSingleView.findViewById(R.id.medCardDialogDescription)
        dialogSingleViewPicker = dialogSingleView.findViewById(R.id.medCardDialogPicker)
        dialogSingleViewSourceSpinner =
            dialogSingleView.findViewById(R.id.medCardDialogSpinnerSourceType)
        // Double number picker dialog
        dialogDoubleView = layoutInflater.inflate(R.layout.main_medcard_inputdialog_double, null)
        dialogDoubleViewTitle = dialogDoubleView.findViewById(R.id.medCardDoubleDialogTitle)
        dialogDoubleViewUnit = dialogDoubleView.findViewById(R.id.medCardDoubleDialogUnit)
        dialogDoubleViewDescription =
            dialogDoubleView.findViewById(R.id.medCardDoubleDialogDescription)
        dialogDoubleViewPicker = dialogDoubleView.findViewById(R.id.medCardDoubleDialogPicker)
        dialogDoubleViewPicker2 = dialogDoubleView.findViewById(R.id.medCardDoubleDialogPicker2)
        dialogDoubleViewSourceSpinner =
            dialogDoubleView.findViewById(R.id.medCardDoubleDialogSpinnerSourceType)
        // List dialog
        dialogListView = layoutInflater.inflate(R.layout.main_medcard_inputdialog_list, null)
        dialogListViewTitle = dialogListView.findViewById(R.id.medCardListDialogTitle)
        dialogListViewDescription = dialogListView.findViewById(R.id.medCardListDialogDescription)
        dialogListViewSourceSpinner =
            dialogListView.findViewById(R.id.medCardListDialogSpinnerSourceType)
        dialogListViewValueSpinner = dialogListView.findViewById(R.id.medCardListDialogSpinnerValue)
        dialogListViewValueSpinner.adapter = dialogListSpinnerAdapter
        //
        dialogSingleViewSourceSpinner.adapter = sourceTypeSpinnerAdapter
        dialogDoubleViewSourceSpinner.adapter = sourceTypeSpinnerAdapter
        dialogListViewSourceSpinner.adapter = sourceTypeSpinnerAdapter
        dialogDoubleViewPicker2.minValue = 0
        dialogDoubleViewPicker2.maxValue = 9
    }

    fun showDatePickerDialog() {
        val date = currentProfile.birthDate
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        if (date.isEmpty()) datePickerBuilder.setSelection(calendar.timeInMillis)
        else datePickerBuilder.setSelection(convertDateStringToMs(date))
        datePickerBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        datePickerBuilder.setTheme(R.style.ThemeDatePkr)
        datePickerBuilder.setCalendarConstraints(datePickerConstraintsBuilder.build())
        datePicker = datePickerBuilder.build()
        datePicker.addOnPositiveButtonClickListener {
            it?.let {
                currentProfile.birthDate = convertDateLongToString(it)
                updateAvailableProfile(currentProfile)
                mainModel.patchProfile(currentProfile)
            }
        }
        datePicker.show(childFragmentManager, "")
    }

    fun showHeightDialog() {
        val valuePicker = currentProfile.height.toInt()
        // Set number picker attributes
        heightNumberPicker.minValue = 50
        heightNumberPicker.maxValue = 260
        heightNumberPicker.wrapSelectorWheel = true
        heightNumberPicker.value = if (valuePicker < 50) 160 else valuePicker

        // Set dialog attributes
        dialogHeightBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogHeightBuilder.setTitle(getString(R.string.profile_height_title))
        dialogHeightBuilder.setMessage(R.string.profile_height_message)
        dialogHeightBuilder.setView(dialogHeightView)
        dialogHeightBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
            val newHeight = heightNumberPicker.value.toFloat()
            val newHeightText = "$newHeight cm"
            profileBinding?.profileHeightTextView?.text = newHeightText
            currentProfile.height = newHeight
            updateAvailableProfile(currentProfile)
            mainModel.patchProfile(currentProfile)
        }
        dialogHeightBuilder.setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }
        dialogHeightBuilder.setOnDismissListener {
            val parent = dialogHeightView.parent as ViewGroup
            parent.removeView(dialogHeightView)
        }
        dialogHeightBuilder.show()
    }

    fun showWeightDialog() {

    }

    fun showCountriesDialog() {
        // Get country from id
        val countryID = currentProfile.country
        var country: Countries? =
            if (countryID == 0) {
                null
            } else {
                mainModel.countries.find { country -> country.id == countryID }
            }

        // Show country dialog
        val countryDialog = ProfileCountryDialogFragment(country) {
            country = it
            profileBinding?.profileCountriesTextView?.text = country?.value
            currentProfile.country = country?.id!!
            updateAvailableProfile(currentProfile)
        }
        countryDialog.show(parentFragmentManager, "country_dialog")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val medCardAdapter = MedCardProfileAdapterRecycler(
            mainModel.paramUnit,
            mainModel.medCardSourceType
        ) { onClickMedCard(it) }
        profileBinding?.profileRecycler?.adapter = medCardAdapter
        profileBinding?.profileRecycler?.isNestedScrollingEnabled = false;
        mainModel.medCard.parametersLive.observe(viewLifecycleOwner, {
            it?.let {
                medCardAdapter.submitList(it.values.toList() as MutableList<MedCardParamSetting>)
                medCardAdapter.notifyDataSetChanged()
            }
        })
    }

    fun showSmokerDialog() {
        val smokerItemChoices = if (currentProfile.smoker) 0 else 1
        var index = smokerItemChoices
        dialogSmokerBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogSmokerBuilder.setTitle(getString(R.string.profile_smoker_title))
        dialogSmokerBuilder.setSingleChoiceItems(smokerAdapter, smokerItemChoices) { _, which ->
            index = which
        }
        dialogSmokerBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
            val smokeStr =
                if (index == 0) getString(R.string.button_yes)
                else getString(R.string.button_no)
            profileBinding?.profileSmokeTextView?.text = smokeStr
            currentProfile.smoker = index == 0
            updateAvailableProfile(currentProfile)
            mainModel.patchProfile(currentProfile)
        }
        dialogSmokerBuilder.show()
    }

    fun showNameDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setTitle(getString(R.string.error_form_profile_name))
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        val nameBox = EditText(context)
        nameBox.hint = getString(R.string.profile_hint_name)
        nameBox.setText(currentProfile.firstName)
        layout.addView(nameBox)

        val surnameBox = EditText(context)
        surnameBox.hint = getString(R.string.profile_hint_surname)
        surnameBox.setText(currentProfile.lastName)
        layout.addView(surnameBox)
        dialogBuilder.setView(layout)

        dialogBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
            when {
                nameBox.text.isEmpty() -> {
                    nameBox.error = getString(R.string.error_form_profile_name)
                }
                surnameBox.text.isEmpty() -> {
                    surnameBox.error = getString(R.string.error_form_profile_surname)
                }
                else -> {
                    currentProfile.firstName = nameBox.text.toString()
                    currentProfile.lastName = surnameBox.text.toString()
                    updateAvailableProfile(currentProfile)
                    mainModel.patchProfile(currentProfile)
                }
            }
        }
        dialogBuilder.show()
    }

    fun showGendersDialog() {
        var index = currentProfile.gender - 1
        dialogGendersBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogGendersBuilder.setTitle(getString(R.string.profile_gender_title))
        dialogGendersBuilder.setSingleChoiceItems(
            gendersAdapter,
            currentProfile.gender
        ) { _, which ->
            index = which
        }
        dialogGendersBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
            currentProfile.gender = index + 1
            updateAvailableProfile(currentProfile)
            mainModel.patchProfile(currentProfile)
        }
        dialogGendersBuilder.show()
    }

    private fun initListeners() {

        mainModel.profileManager.currentProfile.observe(viewLifecycleOwner, {
            it?.let { profile ->
                currentProfile = profile
                updateAvailableProfile(profile)
            }
        })

        mainModel.profileManager.currentMeasurementSystem.observe(viewLifecycleOwner, {
            it?.let {
                currentMeasuringSystem = it
            }
        })
    }

    private fun updateAvailableProfile(profile: Profile) {
        availableData.updateAvailableProfile(profile)
        availableDataSubject.onNext(availableData.getProfileAvailable())
    }

    fun getFullName(profile: Profile?): String {
        val name = profile?.firstName + " " + profile?.lastName
        if (name.matches(Regex(".*\\w.*"))) {
            return name
        }

        return getString(R.string.profile_hint_name)
    }

    fun getBirthDate(profile: Profile?): String {
        val birthdateLong = convertDateStringToMs(profile?.birthDate ?: "")
        return SimpleDateFormat("dd.MMM.yyyy", Locale.getDefault()).format(birthdateLong)
    }

    fun getUserGender(profile: Profile?): String {
        return mainModel.genders[profile?.gender?.minus(1) ?: 0].value
    }

    fun getCountry(profile: Profile?): String {
        val country = mainModel.countries.find { country ->
            country.id == profile?.country
        }
        return country?.value ?: ""
    }

    fun getHeight(profile: Profile?): String {
        return "${profile?.height} cm"
    }


    fun getSmoker(profile: Profile?): String {
        return if (profile?.smoker == true) getString(R.string.button_yes)
        else getString(R.string.button_no)
    }

    fun uploadAvatar() {
        when (PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } -> {
                requestImage()
            }
            else -> {
                (activity as MainActivity).requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun requestImage() {
        activity?.let {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(600, 600)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(it)
        }
    }

    override fun onDestroyView() {
        profileBinding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun onClickMedCard(item: MedCardParamSetting) {
        when (item.id) {
            AvailableParameters.WEIGHT.id -> {
                showIntAndIntDialog(item)
            }
            AvailableParameters.HIP.id -> {
                showIntAndFloatDialog(item)
            }
            AvailableParameters.WAIST.id -> {
                showIntAndFloatDialog(item)
            }
            AvailableParameters.WRIST.id -> {
                showIntAndFloatDialog(item)
            }
            AvailableParameters.NECK.id -> {
                showIntAndFloatDialog(item)
            }
            AvailableParameters.HEART_RATE_ALONE.id -> {
                showIntAndIntDialog(item)
            }
            AvailableParameters.DAILY_ACTIVITY_LEVEL.id -> {
                showListDialog(item)
            }
            AvailableParameters.BLOOD_PRESSURE_SYSTOLIC.id -> {
                showIntAndFloatDialog(item)
            }
            AvailableParameters.BLOOD_PRESSURE_DIASTOLIC.id -> {
                showIntAndFloatDialog(item)
            }
            AvailableParameters.CHOLESTEROL.id -> {
                showFloatAndIntDialog(item)
            }
            AvailableParameters.GLUCOSE.id -> {
                showFloatAndIntDialog(item)
            }
        }
    }

    private fun showIntAndIntDialog(item: MedCardParamSetting) {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }
        dialogBuilder.setView(dialogSingleView)
        dialogBuilder.setOnDismissListener {
            val parent = dialogSingleView.parent as ViewGroup
            parent.removeView(dialogSingleView)
        }

        val unit = mainModel.paramUnit.findLast { item.unitID == it.id }
        val sourceType = mainModel.medCardSourceType.findLast { item.sourceTypeID == it.id }
        val sourceTypePosition = sourceTypeSpinnerAdapter.getPosition(sourceType)

        dialogSingleViewTitle.setText(
            resources.getIdentifier(
                item.nameRes,
                "string",
                "com.application.bmiobesity"
            )
        )
        dialogSingleViewDescription.setText(
            resources.getIdentifier(
                item.longDescriptionRes,
                "string",
                "com.application.bmiobesity"
            )
        )
        dialogSingleViewSourceSpinner.setSelection(sourceTypePosition)
        dialogSingleViewSourceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val srcType = sourceTypeSpinnerAdapter.getItem(position)
                    srcType?.let {
                        mainModel.medCard.setSourceType(it, item)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        when (item.preferMeasuringSystem) {
            MeasuringSystem.METRIC.id -> {
                dialogSingleViewUnit.setText(
                    resources.getIdentifier(
                        unit?.nameMetricRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogSingleViewPicker.minValue = item.minMetricValue
                dialogSingleViewPicker.maxValue = item.maxMetricValue
                val value = item.values.lastOrNull()?.value?.toInt()
                if (value == null) {
                    dialogSingleViewPicker.value = item.defaultValue
                } else {
                    dialogSingleViewPicker.value = value
                }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
                    setMetricValue(
                        dialogSingleViewPicker.value.toFloat(),
                        item
                    )
                }
                dialogBuilder.show()
            }
            MeasuringSystem.IMPERIAL.id -> {
                dialogSingleViewUnit.setText(
                    resources.getIdentifier(
                        unit?.nameImperialRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogSingleViewPicker.minValue = item.minImpValue
                dialogSingleViewPicker.maxValue = item.maxImpValue
                val valueImp = item.values.lastOrNull()?.valueImp?.toInt()
                if (valueImp == null) {
                    dialogSingleViewPicker.value = item.defaultValueImp
                } else {
                    dialogSingleViewPicker.value = valueImp
                }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
                    setImpValue(
                        dialogSingleViewPicker.value.toFloat(),
                        item
                    )
                }
                dialogBuilder.show()
            }
        }
    }

    private fun showIntAndFloatDialog(item: MedCardParamSetting) {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }

        val unit = mainModel.paramUnit.findLast { item.unitID == it.id }
        val sourceType = mainModel.medCardSourceType.findLast { item.sourceTypeID == it.id }
        val sourceTypePosition = sourceTypeSpinnerAdapter.getPosition(sourceType)

        when (item.preferMeasuringSystem) {
            MeasuringSystem.METRIC.id -> {
                dialogBuilder.setView(dialogSingleView)
                dialogBuilder.setOnDismissListener {
                    val parent = dialogSingleView.parent as ViewGroup
                    parent.removeView(dialogSingleView)
                }
                dialogSingleViewTitle.setText(
                    resources.getIdentifier(
                        item.nameRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogSingleViewDescription.setText(
                    resources.getIdentifier(
                        item.longDescriptionRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogSingleViewUnit.setText(
                    resources.getIdentifier(
                        unit?.nameMetricRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogSingleViewPicker.minValue = item.minMetricValue
                dialogSingleViewPicker.maxValue = item.maxMetricValue
                val value = item.values.lastOrNull()?.value?.toInt()
                if (value == null) {
                    dialogSingleViewPicker.value = item.defaultValue
                } else {
                    dialogSingleViewPicker.value = value
                }
                dialogSingleViewSourceSpinner.setSelection(sourceTypePosition)
                dialogSingleViewSourceSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val srcType = sourceTypeSpinnerAdapter.getItem(position)
                            srcType?.let {
                                mainModel.medCard.setSourceType(it, item)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
                    setMetricValue(
                        dialogSingleViewPicker.value.toFloat(),
                        item
                    )
                }
                dialogBuilder.show()
            }
            MeasuringSystem.IMPERIAL.id -> {
                dialogBuilder.setView(dialogDoubleView)
                dialogBuilder.setOnDismissListener {
                    val parent = dialogDoubleView.parent as ViewGroup
                    parent.removeView(dialogDoubleView)
                }
                dialogDoubleViewTitle.setText(
                    resources.getIdentifier(
                        item.nameRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogDoubleViewDescription.setText(
                    resources.getIdentifier(
                        item.longDescriptionRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogDoubleViewUnit.setText(
                    resources.getIdentifier(
                        unit?.nameImperialRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogDoubleViewPicker.minValue = item.minImpValue
                dialogDoubleViewPicker.maxValue = item.maxImpValue
                val valueImp = item.values.lastOrNull()?.valueImp
                if (valueImp == null) {
                    dialogDoubleViewPicker.value = item.defaultValueImp
                } else {
                    dialogDoubleViewPicker.value = getFirstNumber(valueImp)
                    dialogDoubleViewPicker2.value = getSecondNumber(valueImp)
                }
                dialogDoubleViewSourceSpinner.setSelection(sourceTypePosition)
                dialogDoubleViewSourceSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val srcType = sourceTypeSpinnerAdapter.getItem(position)
                            srcType?.let {
                                mainModel.medCard.setSourceType(it, item)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
                    val val1 = dialogDoubleViewPicker.value
                    val val2 = dialogDoubleViewPicker2.value
                    setImpValue(getFloatFromTwoInt(val1, val2), item)
                }
                dialogBuilder.show()
            }
        }
    }

    private fun showFloatAndIntDialog(item: MedCardParamSetting) {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }

        val unit = mainModel.paramUnit.findLast { item.unitID == it.id }
        val sourceType = mainModel.medCardSourceType.findLast { item.sourceTypeID == it.id }
        val sourceTypePosition = sourceTypeSpinnerAdapter.getPosition(sourceType)

        when (item.preferMeasuringSystem) {
            MeasuringSystem.METRIC.id -> {
                dialogBuilder.setView(dialogDoubleView)
                dialogBuilder.setOnDismissListener {
                    val parent = dialogDoubleView.parent as ViewGroup
                    parent.removeView(dialogDoubleView)
                }
                dialogDoubleViewTitle.setText(
                    resources.getIdentifier(
                        item.nameRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogDoubleViewDescription.setText(
                    resources.getIdentifier(
                        item.longDescriptionRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogDoubleViewUnit.setText(
                    resources.getIdentifier(
                        unit?.nameMetricRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogDoubleViewPicker.minValue = item.minMetricValue
                dialogDoubleViewPicker.maxValue = item.maxMetricValue
                val value = item.values.lastOrNull()?.value
                if (value == null) {
                    dialogDoubleViewPicker.value = item.defaultValue
                } else {
                    dialogDoubleViewPicker.value = getFirstNumber(value)
                    dialogDoubleViewPicker2.value = getSecondNumber(value)
                }
                dialogDoubleViewSourceSpinner.setSelection(sourceTypePosition)
                dialogDoubleViewSourceSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val srcType = sourceTypeSpinnerAdapter.getItem(position)
                            srcType?.let {
                                mainModel.medCard.setSourceType(it, item)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
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
                dialogSingleViewTitle.setText(
                    resources.getIdentifier(
                        item.nameRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogSingleViewDescription.setText(
                    resources.getIdentifier(
                        item.longDescriptionRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogSingleViewUnit.setText(
                    resources.getIdentifier(
                        unit?.nameImperialRes,
                        "string",
                        "com.application.bmiobesity"
                    )
                )
                dialogSingleViewPicker.minValue = item.minImpValue
                dialogSingleViewPicker.maxValue = item.maxImpValue
                val valueImp = item.values.lastOrNull()?.valueImp?.toInt()
                if (valueImp == null) {
                    dialogSingleViewPicker.value = item.defaultValueImp
                } else {
                    dialogSingleViewPicker.value = valueImp
                }
                dialogSingleViewSourceSpinner.setSelection(sourceTypePosition)
                dialogSingleViewSourceSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val srcType = sourceTypeSpinnerAdapter.getItem(position)
                            srcType?.let {
                                mainModel.medCard.setSourceType(it, item)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                dialogBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
                    setImpValue(
                        dialogSingleViewPicker.value.toFloat(),
                        item
                    )
                }
                dialogBuilder.show()
            }
        }
    }

    private fun showListDialog(item: MedCardParamSetting) {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }
        dialogBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
            val position = dialogListViewValueSpinner.selectedItemPosition
            val choiceActivityLevel = DailyActivityLevels.values()[position]
            setMetricValue(choiceActivityLevel.id, item)
        }
        dialogBuilder.setView(dialogListView)
        dialogBuilder.setOnDismissListener {
            val parent = dialogListView.parent as ViewGroup
            parent.removeView(dialogListView)
        }

        dialogListViewTitle.setText(
            resources.getIdentifier(
                item.nameRes,
                "string",
                "com.application.bmiobesity"
            )
        )
        val sourceType = mainModel.medCardSourceType.findLast { item.sourceTypeID == it.id }
        val sourceTypePosition = sourceTypeSpinnerAdapter.getPosition(sourceType)
        dialogListViewSourceSpinner.setSelection(sourceTypePosition)

        val currValue = when (item.values.lastOrNull()?.value) {
            DailyActivityLevels.MINIMUM.id -> {
                DailyActivityLevels.MINIMUM
            }
            DailyActivityLevels.LOWER.id -> {
                DailyActivityLevels.LOWER
            }
            DailyActivityLevels.MEDIUM.id -> {
                DailyActivityLevels.MEDIUM
            }
            DailyActivityLevels.HIGH.id -> {
                DailyActivityLevels.HIGH
            }
            DailyActivityLevels.VERY_HIGH.id -> {
                DailyActivityLevels.VERY_HIGH
            }
            else -> {
                DailyActivityLevels.MEDIUM
            }
        }

        dialogListViewDescription.setText(
            resources.getIdentifier(
                currValue.descriptionRes,
                "string",
                "com.application.bmiobesity"
            )
        )
        dialogListViewValueSpinner.setSelection(currValue.pos)

        dialogListViewValueSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val choiceActivityLevel = DailyActivityLevels.values()[position]
                    dialogListViewDescription.setText(
                        resources.getIdentifier(
                            choiceActivityLevel.descriptionRes,
                            "string",
                            "com.application.bmiobesity"
                        )
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        dialogListViewSourceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val srcType = sourceTypeSpinnerAdapter.getItem(position)
                    srcType?.let {
                        mainModel.medCard.setSourceType(it, item)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        dialogBuilder.show()
    }
    private fun setMetricValue(v: Float, p: MedCardParamSetting){
        mainModel.medCard.setMetricValue(v, p)
        mainModel.updateMedCard()
    }
    private fun setImpValue(v: Float, p: MedCardParamSetting){
        mainModel.medCard.setImpValue(v, p)
        mainModel.updateMedCard()
    }
}