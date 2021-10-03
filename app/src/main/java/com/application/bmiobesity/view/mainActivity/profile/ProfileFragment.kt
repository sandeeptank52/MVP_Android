package com.application.bmiobesity.view.mainActivity.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.common.MeasuringSystem
import com.application.bmiobesity.databinding.MainProfileFragmentBinding
import com.application.bmiobesity.model.db.commonSettings.entities.Countries
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile
import com.application.bmiobesity.utils.convertDateLongToString
import com.application.bmiobesity.utils.convertDateStringToMs
import com.application.bmiobesity.view.mainActivity.profile.country.ProfileCountryDialogFragment
import com.application.bmiobesity.viewModels.MainViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.*

class ProfileFragment : BaseFragment(R.layout.main_profile_fragment) {

    private var profileBinding: MainProfileFragmentBinding? = null
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

    private var isFirstTime: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding = MainProfileFragmentBinding.bind(view)
        profileBinding?.vm = mainModel
        profileBinding?.lifecycleOwner = this
        arguments?.let {
            isFirstTime = it.getBoolean("isFirstTime")
            //showFirstTimeMsgVisibility(isFirstTime)
            if (isFirstTime) showInfoDialog()
        }

        init()
        addRX()

        initListeners()
        setLayoutListeners()
    }

    private fun showInfoDialog() {
        val infoDialog = MaterialAlertDialogBuilder(requireContext())
        infoDialog.setPositiveButton(getString(R.string.button_ok)) { _, _ -> }
        infoDialog.setTitle(getString(R.string.profile_welcome_dialog_title))
        infoDialog.setMessage(getString(R.string.profile_welcome_dialog_message))
        infoDialog.show()
    }

    @SuppressLint("InflateParams")
    private fun init() {
        allDisposable = CompositeDisposable()
        availableData = AvailableData("")
        availableDataSubject = PublishSubject.create()

        // Init date picker calendar
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentYear = calendar.get(Calendar.YEAR)
        calendar.set(Calendar.YEAR, currentYear - 122)
        val startTime = calendar.timeInMillis
        calendar.clear()
        calendar.set(Calendar.YEAR, currentYear - 5)
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
    }
    private fun addRX() {
        val nameDisposable = profileBinding?.nameEt?.textChanges()
            ?.skipInitialValue()
            ?.subscribe {
                currentProfile.firstName = it.toString()
                updateAvailableProfile(currentProfile)

                if (it.isNullOrEmpty()) {
                    setNameError(getString(R.string.error_form_profile_name))
                } else {
                    setNameError(null)
                }
            }

        val surnameDisposable = profileBinding?.surnameEt?.textChanges()
            ?.skipInitialValue()
            ?.subscribe {
                currentProfile.lastName = it.toString()
                updateAvailableProfile(currentProfile)

                if (it.isNullOrEmpty()) {
                    setSurnameError((getString(R.string.error_form_profile_surname)))
                } else {
                    setSurnameError(null)
                }
            }

        val availableDataDisposable = availableDataSubject.subscribe {
            setEnabledConfirmButton(it)
        }

        allDisposable.addAll(
            nameDisposable,
            surnameDisposable,
            availableDataDisposable
        )
    }

    private fun showDatePickerDialog(date: String) {
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
                profileBinding?.profileBirthDateTextView?.text = convertDateLongToString(it)
                currentProfile.birthDate = convertDateLongToString(it)
                updateAvailableProfile(currentProfile)
            }
        }
        datePicker.show(childFragmentManager, "")
    }
    private fun showHeightDialog(valuePicker: Int) {
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
        }
        dialogHeightBuilder.setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }
        dialogHeightBuilder.setOnDismissListener {
            val parent = dialogHeightView.parent as ViewGroup
            parent.removeView(dialogHeightView)
        }
        dialogHeightBuilder.show()
    }
    private fun showCountriesDialog(countryID: Int) {
        // Get country from id
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
    private fun showSmokerDialog(item: Int) {
        var index = item
        dialogSmokerBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogSmokerBuilder.setTitle(getString(R.string.profile_smoker_title))
        dialogSmokerBuilder.setSingleChoiceItems(smokerAdapter, item) { _, which ->
            index = which
        }
        dialogSmokerBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
            val smokeStr =
                if (index == 0) getString(R.string.button_yes)
                else getString(R.string.button_no)
            profileBinding?.profileSmokeTextView?.text = smokeStr
            currentProfile.smoker = index == 0
            updateAvailableProfile(currentProfile)
        }
        dialogSmokerBuilder.show()
    }
    private fun showGendersDialog(item: Int) {
        var index = item
        dialogGendersBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogGendersBuilder.setTitle(getString(R.string.profile_gender_title))
        dialogGendersBuilder.setSingleChoiceItems(gendersAdapter, item) { _, which ->
            index = which
        }
        dialogGendersBuilder.setPositiveButton(getString(R.string.button_ok)) { _, _ ->
            profileBinding?.profileGenderTextView?.text = mainModel.genders[index].value
            currentProfile.gender = index + 1
            updateAvailableProfile(currentProfile)
        }
        dialogGendersBuilder.show()
    }

    private fun initListeners() {
        profileBinding?.profileFirstTimeButton?.clicks()?.subscribe {
            mainModel.patchProfile(currentProfile)

            if (isFirstTime) {
                val bundle = bundleOf("isFirstTime" to true)
                findNavController().navigate(R.id.mainNavProfileToMedcard, bundle)
            } else {
                Toast.makeText(requireActivity(), resources.getString(R.string.profile_save_successfully), Toast.LENGTH_SHORT).show()
            }
        }

        mainModel.profileManager.currentProfile.observe(viewLifecycleOwner, {
            it?.let { profile ->
                currentProfile = profile
                updateAvailableProfile(profile)

                // Set text views
                profileBinding?.nameEt?.setText(profile.firstName)
                profileBinding?.surnameEt?.setText(profile.lastName)
                profileBinding?.profileBirthDateTextView?.text = profile.birthDate
                profileBinding?.profileGenderTextView?.text = mainModel.genders[profile.gender - 1].value

                val height = "${profile.height} cm"
                profileBinding?.profileHeightTextView?.text = height

                val smokeStr =
                    if (profile.smoker) getString(R.string.button_yes)
                    else getString(R.string.button_no)
                profileBinding?.profileSmokeTextView?.text = smokeStr

                val country = mainModel.countries.find { country ->
                    country.id == profile.country
                }
                profileBinding?.profileCountriesTextView?.text = country?.value
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

    private fun setLayoutListeners() {
        profileBinding?.profileBirthDateConstraint?.setOnClickListener {
            showDatePickerDialog(currentProfile.birthDate)
        }
        profileBinding?.profileHeightConstraint?.setOnClickListener {
            showHeightDialog(currentProfile.height.toInt())
        }
        profileBinding?.profileCountriesConstraint?.setOnClickListener {
            showCountriesDialog(currentProfile.country)
        }
        profileBinding?.profileGenderConstraint?.setOnClickListener {
            showGendersDialog(currentProfile.gender - 1)
        }

        profileBinding?.profileSmokerConstraint?.setOnClickListener {
            val smokerItemChoices = if (currentProfile.smoker) 0 else 1
            showSmokerDialog(smokerItemChoices)
        }
    }

    private fun setNameError(msg: String?) {
        profileBinding?.nameInputText?.error = msg
        if (msg == null) {
            profileBinding?.nameInputText?.hint = resources.getString(R.string.profile_hint_name)
        } else {
            profileBinding?.nameInputText?.hint = msg
        }
    }
    private fun setSurnameError(msg: String?) {
        profileBinding?.surnameInputText?.error = msg
        if (msg == null) {
            profileBinding?.surnameInputText?.hint =
                resources.getString(R.string.profile_hint_surname)
        } else {
            profileBinding?.surnameInputText?.hint = msg
        }
    }
    private fun setEnabledConfirmButton(isEnabled: Boolean) {
        profileBinding?.profileFirstTimeButton?.isEnabled = isEnabled
        if (isEnabled) {
            profileBinding?.profileFirstTimeButton?.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.all_round_blue,
                null
            )
            profileBinding?.profileFirstTimeButton?.setTextColor(
                resources.getColor(
                    R.color.color_white,
                    null
                )
            )
        } else {
            profileBinding?.profileFirstTimeButton?.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.all_round_gray,
                null
            )
            profileBinding?.profileFirstTimeButton?.setTextColor(
                resources.getColor(
                    R.color.colorPrimary,
                    null
                )
            )
        }
    }

//    private fun showFirstTimeMsgVisibility(firsTime: Boolean) {
//        if (firsTime) {
//            profileBinding?.profileFirstTimeInfo?.visibility = View.VISIBLE
//        } else {
//            profileBinding?.profileFirstTimeInfo?.visibility = View.GONE
//        }
//    }
//    private fun createFirsTimeMsg(data: AvailableData) {
//        if (!data.getProfileAvailable()) {
//            val msgBuilder = StringBuilder()
//            msgBuilder.append(getString(R.string.profile_firs_time_msg))
//            if (!data.firstNameAvailable) msgBuilder.append("${getString(R.string.name)}  ")
//            if (!data.lastNameAvailable) msgBuilder.append("${getString(R.string.surname)}  ")
//            if (!data.genderAvailable) msgBuilder.append("${getString(R.string.gender)}  ")
//            if (!data.heightAvailable) msgBuilder.append("${getString(R.string.height)}  ")
//            if (!data.countryAvailable) msgBuilder.append("${getString(R.string.countries)}  ")
//            profileBinding?.profileFirstTimeInfo?.text = msgBuilder.toString()
//            msgBuilder.clear()
//        } else {
//            profileBinding?.profileFirstTimeInfo?.text = ""
//        }
//    }

    override fun onDestroyView() {
        profileBinding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (!allDisposable.isDisposed) allDisposable.dispose()
        super.onDestroy()
    }
}