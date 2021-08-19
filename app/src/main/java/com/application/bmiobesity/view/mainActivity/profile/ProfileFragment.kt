package com.application.bmiobesity.view.mainActivity.profile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListAdapter
import android.widget.NumberPicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.application.bmiobesity.R
import com.application.bmiobesity.common.MeasuringSystem
import com.application.bmiobesity.databinding.MainProfileFragmentBinding
import com.application.bmiobesity.model.db.paramSettings.entities.profile.AvailableData
import com.application.bmiobesity.model.db.paramSettings.entities.profile.Profile
import com.application.bmiobesity.viewModels.MainViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding4.view.focusChanges
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*

class ProfileFragment : Fragment(R.layout.main_profile_fragment) {

    private var profileBinding: MainProfileFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()

    private lateinit var currentProfile: Profile
    private var currentMeasuringSystem = MeasuringSystem.METRIC
    private lateinit var allDisposable: CompositeDisposable

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding = MainProfileFragmentBinding.bind(view)

        var isFirstTime: Boolean

        arguments?.let {
            isFirstTime = it.getBoolean("isFirstTime")
            showFirstTimeDialog(isFirstTime)
            if (isFirstTime) showInfoDialog()
        }

        init()
        addRX()

        initDatePicker("")
        initGendersDialog(0)
        initHeightDialog(70)
        initSmokerDialog(0)
        initCountriesDialog(1)

        initListeners()
        setLayoutListeners()
    }

    private fun showInfoDialog(){
        val infoDialog = MaterialAlertDialogBuilder(requireContext())
        infoDialog.setPositiveButton(getString(R.string.button_ok)){_, _ -> }
        infoDialog.setTitle(getString(R.string.profile_welcome_dialog_title))
        infoDialog.setMessage(getString(R.string.profile_welcome_dialog_message))
        infoDialog.show()
    }
    
    private fun init(){
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(1891, 5, 1)
        val startTime = calendar.timeInMillis
        calendar.clear()
        calendar.set(2011, 3, 1)
        val endTime = calendar.timeInMillis
        datePickerConstraintsBuilder = CalendarConstraints.Builder()
        datePickerConstraintsBuilder.setStart(startTime)
        datePickerConstraintsBuilder.setEnd(endTime)
        allDisposable = CompositeDisposable()
        gendersAdapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_singlechoice, mainModel.genders)
        dialogHeightView = layoutInflater.inflate(R.layout.main_profile_dialog_height, null)
        heightNumberPicker = dialogHeightView.findViewById(R.id.profileHeightNumberPicker)
        smokerAdapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_singlechoice, arrayOf(getString(R.string.button_yes), getString(R.string.button_no)))
        countriesAdapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_singlechoice, mainModel.countries)
    }

    private fun addRX(){
        val nameDisposable = profileBinding?.nameEt?.textChanges()
            //?.skipInitialValue()
            ?.subscribe {
                if (it.isNullOrEmpty()){
                    setErrorEnabled(true)
                    setNameError(getString(R.string.error_form_profile_name))
                } else {
                    setNameError(null)
                    setErrorEnabled(false)
                }
            }
        val nameFocusDisposable = profileBinding?.nameEt?.focusChanges()
            //?.skipInitialValue()
            ?.subscribe {
                /*if (!it && !nameIsHasError()){
                    profileBinding?.nameEt?.text?.toString()?.let { name ->
                        currentProfile.firstName = name
                        mainModel.patchProfile(currentProfile)
                    }
                }*/
            }

        val surnameDisposable = profileBinding?.surnameEt?.textChanges()
            //?.skipInitialValue()
            ?.subscribe {
                if (it.isNullOrEmpty()){
                    setErrorEnabled(true)
                    setSurnameError((getString(R.string.error_form_profile_surname)))
                } else {
                    setSurnameError(null)
                    setErrorEnabled(false)
                }
            }
        val surnameFocusDisposable = profileBinding?.surnameEt?.focusChanges()
            //?.skipInitialValue()
            ?.subscribe {
                /*if (!it && !surnameIsHasError()){
                    profileBinding?.surnameEt?.text?.toString()?.let { surname ->
                        currentProfile.lastName = surname
                        mainModel.patchProfile(currentProfile)
                    }
                }*/
            }
        allDisposable.addAll(nameDisposable, nameFocusDisposable, surnameDisposable, surnameFocusDisposable)
    }

    private fun initDatePicker(date: String){
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
                mainModel.patchProfile(currentProfile)
            }
        }
    }
    private fun initGendersDialog(item: Int){
        dialogGendersBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogGendersBuilder.setTitle(getString(R.string.profile_gender_title))
        dialogGendersBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ -> }
        dialogGendersBuilder.setSingleChoiceItems(gendersAdapter, item) { _, which ->
            profileBinding?.profileGenderTextView?.text = mainModel.genders[which].value
            currentProfile.gender = which + 1
            mainModel.patchProfile(currentProfile)
        }
    }
    private fun initHeightDialog(valuePicker: Int){
        dialogHeightBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogHeightBuilder.setTitle(getString(R.string.profile_height_title))
        dialogHeightBuilder.setMessage(R.string.profile_height_message)
        dialogHeightBuilder.setView(dialogHeightView)
        dialogHeightBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ ->
            currentProfile.height = heightNumberPicker.value.toFloat()
            mainModel.patchProfile(currentProfile)
        }
        dialogHeightBuilder.setNegativeButton(getString(R.string.button_cancel)){_, _ ->}
        dialogHeightBuilder.setOnDismissListener {
            val parent = dialogHeightView.parent as ViewGroup
            parent.removeView(dialogHeightView)
        }

        heightNumberPicker.minValue = 50
        heightNumberPicker.maxValue = 260

        heightNumberPicker.wrapSelectorWheel = true
        heightNumberPicker.value = if (valuePicker < 50) 70 else valuePicker
        heightNumberPicker.setOnValueChangedListener { _, _, newVal ->
            val newHeight = "$newVal cm"
            profileBinding?.profileHeightTextView?.text = newHeight
        }

        //(NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }.get(heightNumberPicker) as EditText).filters = emptyArray()
    }
    private fun initSmokerDialog(item: Int){
        dialogSmokerBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogSmokerBuilder.setTitle(getString(R.string.profile_smoker_title))
        dialogSmokerBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ ->}
        dialogSmokerBuilder.setSingleChoiceItems(smokerAdapter, item){_, which ->
            val smokeStr = if (which == 0) getString(R.string.button_yes) else getString(R.string.button_no)
            profileBinding?.profileSmokeTextView?.text = smokeStr
            currentProfile.smoker = which == 0
            mainModel.patchProfile(currentProfile)
        }
    }
    private fun initCountriesDialog(item: Int){
        dialogCountriesBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogCountriesBuilder.setTitle("")
        dialogCountriesBuilder.setPositiveButton(getString(R.string.button_ok)){_, _ ->}
        dialogCountriesBuilder.setSingleChoiceItems(countriesAdapter, item){_, which ->
            profileBinding?.profileCountriesTextView?.text = mainModel.countries[which].value
            currentProfile.country = which
            mainModel.patchProfile(currentProfile)
        }
    }

    private fun initListeners(){
        mainModel.profileManager.currentProfile.observe(viewLifecycleOwner, {
            it?.let {
                currentProfile = it

                profileBinding?.nameEt?.setText(it.firstName)
                profileBinding?.surnameEt?.setText(it.lastName)

                profileBinding?.profileBirthDateTextView?.text = it.birthDate
                initDatePicker(it.birthDate)

                profileBinding?.profileGenderTextView?.text = mainModel.genders[it.gender - 1].value
                initGendersDialog(it.gender - 1)

                val height = "${it.height} cm"
                profileBinding?.profileHeightTextView?.text = height
                initHeightDialog(it.height.toInt())

                val smokeStr = if (it.smoker) getString(R.string.button_yes) else getString(R.string.button_no)
                profileBinding?.profileSmokeTextView?.text = smokeStr
                val smokerItemChoices = if (it.smoker) 0 else 1
                initSmokerDialog(smokerItemChoices)

                profileBinding?.profileCountriesTextView?.text = mainModel.countries[it.country].value
                initCountriesDialog(it.country)
            }
        })

        mainModel.profileManager.currentAvailableData.observe(viewLifecycleOwner, {
            it?.let {
                profileBinding?.profileFirstTimeButton?.isEnabled = it.getProfileAvailable()
                createFirsTimeMsg(it)
            }
        })

        profileBinding?.nameEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                if (!nameIsHasError()){
                    profileBinding?.nameEt?.text?.toString()?.let { name ->
                        currentProfile.firstName = name
                        mainModel.patchProfile(currentProfile)
                    }
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        profileBinding?.surnameEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                if (!surnameIsHasError()){
                    profileBinding?.surnameEt?.text?.toString()?.let { surname ->
                        currentProfile.lastName = surname
                        mainModel.patchProfile(currentProfile)
                    }
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        profileBinding?.profileFirstTimeButton?.setOnClickListener {
            val bundle = bundleOf("isFirstTime" to true)
            findNavController().navigate(R.id.mainNavProfileToMedcard, bundle)
        }

        mainModel.profileManager.currentMeasurementSystem.observe(viewLifecycleOwner, {
            it?.let {
                currentMeasuringSystem = it
            }
        })
    }

    private fun setLayoutListeners(){
        profileBinding?.profileBirthDateConstraint?.setOnClickListener { datePicker.show(childFragmentManager, "") }
        profileBinding?.profileGenderConstraint?.setOnClickListener { dialogGendersBuilder.show() }
        profileBinding?.profileHeightConstraint?.setOnClickListener { dialogHeightBuilder.show() }
        profileBinding?.profileSmokerConstraint?.setOnClickListener { dialogSmokerBuilder.show() }
        profileBinding?.profileCountriesConstraint?.setOnClickListener { dialogCountriesBuilder.show() }
    }

    private fun convertDateStringToMs(date: String): Long{
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        if(date.isNotEmpty()){
            val splitDate = date.split(".")
            if(splitDate.size == 3){
                val day = splitDate[0].toInt()
                val month = splitDate[1].toInt() - 1
                val year = splitDate[2].toInt()
                calendar.set(year, month, day)
                return calendar.timeInMillis
            }
        }
        return calendar.timeInMillis
    }
    private fun convertDateLongToString(date: Long): String{
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayStr = if (day > 9) day.toString() else "0${day}"
        val month = (calendar.get(Calendar.MONTH) + 1)
        val mothStr = if (month > 9) month.toString() else "0${month}"
        val year = calendar.get(Calendar.YEAR).toString()
        return "${dayStr}.${mothStr}.${year}"
    }

    private fun setNameEmptyError(){
        val name = profileBinding?.nameEt?.text?.toString()
        if (name.isNullOrEmpty()){
            setErrorEnabled(true)
            setNameError(getString(R.string.error_form_profile_name))
        } else {
            setNameError(null)
            setErrorEnabled(false)
        }
    }
    private fun setErrorEnabled(enabled: Boolean){
        profileBinding?.nameInputText?.isErrorEnabled = enabled
        profileBinding?.surnameInputText?.isErrorEnabled = enabled
    }
    private fun setNameError(msg: String?){
        profileBinding?.nameInputText?.error = msg
    }
    private fun setSurnameError(msg: String?){
        profileBinding?.surnameInputText?.error = msg
    }
    private fun nameIsHasError(): Boolean = !profileBinding?.nameInputText?.error.isNullOrEmpty()
    private fun surnameIsHasError(): Boolean = !profileBinding?.surnameInputText?.error.isNullOrEmpty()

    private fun showFirstTimeDialog(firsTime: Boolean){
        if (firsTime){
            profileBinding?.profileFirstTimeButton?.visibility = View.VISIBLE
            profileBinding?.profileFirstTimeInfo?.visibility = View.VISIBLE
        } else {
            profileBinding?.profileFirstTimeButton?.visibility = View.GONE
            profileBinding?.profileFirstTimeInfo?.visibility = View.GONE
        }
    }
    private fun createFirsTimeMsg(data: AvailableData){
        if (!data.getProfileAvailable()){
            val msgBuilder = StringBuilder()
            msgBuilder.append(getString(R.string.profile_firs_time_msg))
            if (!data.firstNameAvailable) msgBuilder.append("${getString(R.string.name)}  ")
            if (!data.lastNameAvailable) msgBuilder.append("${getString(R.string.surname)}  ")
            if (!data.genderAvailable) msgBuilder.append("${getString(R.string.gender)}  ")
            if (!data.heightAvailable) msgBuilder.append("${getString(R.string.height)}  ")
            if (!data.countryAvailable) msgBuilder.append("${getString(R.string.countries)}  ")
            profileBinding?.profileFirstTimeInfo?.text = msgBuilder.toString()
            msgBuilder.clear()
        } else {
            profileBinding?.profileFirstTimeInfo?.text = ""
        }
    }

    override fun onDestroyView() {
        profileBinding = null
        super.onDestroyView()
    }
    override fun onDestroy() {
        if (!allDisposable.isDisposed) allDisposable.dispose()
        super.onDestroy()
    }
}