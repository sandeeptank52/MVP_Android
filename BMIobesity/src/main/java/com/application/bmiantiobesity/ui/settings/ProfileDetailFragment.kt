package com.application.bmiantiobesity.ui.settings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.models.*
import com.application.bmiantiobesity.retrofit.*
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.ui.main.MainViewModel
import com.application.bmiantiobesity.utilits.getCurrentLocale
import com.application.bmiantiobesity.utilits.getStringLocale
import com.application.bmiantiobesity.utilits.roundTo
import com.bumptech.glide.Glide
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import io.reactivex.disposables.Disposable
import java.lang.Exception
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class ProfileDetailFragment : Fragment() {
    // Для первой загрузки
    private var finishLoadProgress: AtomicInteger = AtomicInteger(2)
    // Для нажатой кнопки сохранить
    private val finishSaveDataGood: AtomicInteger = AtomicInteger(2)
    private val finishSaveData: AtomicInteger = AtomicInteger(2)
    private val liveResultSaveData: MutableLiveData<Int> = MutableLiveData()

    //private lateinit var viewModel: MainViewModel
    private val viewModel by viewModels<MainViewModel>()

    private var profile: Profile? = null
    private var dashBoard: DashBoard? = null

    private var disposableProfile: Disposable? = null
    private var disposableSetProfile: Disposable? = null
    private var disposableDashBoard: Disposable? = null
    private var disposableSetDashBoard: Disposable? = null
    private var disposableGenders: Disposable? = null
    private var disposableCountries: Disposable? = null

    private var isDashBoardUpdate = false
    private var isProfileUpdate = false

    private lateinit var profileImageView: ImageView
    private lateinit var textName:TextInputEditText
    private lateinit var textLastName:TextInputEditText
    private lateinit var spinnerGender: SearchableSpinner
    private lateinit var spinnerCountry: SearchableSpinner
    private lateinit var spinnerHeight: SearchableSpinner
    private lateinit var spinnerWeight: SearchableSpinner
    private lateinit var textBirthDate:TextView
    private lateinit var textSwitch:TextView
    private lateinit var btSwitch: SwitchMaterial
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonSave: Button


    private lateinit var listGenders: Genders  //Genders(listOf(DataValues(1, "Male"),DataValues(2, "Female"), DataValues(3, "Undefined")))
    private lateinit var listCountries: Countries

    private fun setSaveButton() {
        buttonSave.isEnabled = !(textName.text.isNullOrBlank() || textBirthDate.text.isNullOrBlank() ||
                (spinnerGender.selectedItem == null) || (spinnerCountry.selectedItem == null) || (spinnerHeight.selectedItem == null) || (spinnerWeight.selectedItem == null))
    }

    /*private fun testListener() {
        Toast.makeText(this@ProfileDetailFragment.requireContext(), "Click!", Toast.LENGTH_SHORT).show()
        // Проверка всех полей и активация кнопки Save
        setSaveButton()
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_detail_fragment, container, false)

        // Настройка Toolbar
        //setToolbarTitle<MainActivity>(R.id.main_toolbar, getString(R.string.profile))
        progressBar = view.findViewById(R.id.profile_progressBar)
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) progressBar.progressTintList = ColorStateList.valueOf(
        //    ContextCompat.getColor(this.requireContext(), R.color.button_enabled_endColor))
        isProgress(true)


        val btNext = view.findViewById<Button>(R.id.profile_button_next)
        // Контроль первого запуска
        if (false) {//(SettingsActivity.firstLoad) { // Вернуть при подключении к Google Fit
            btNext.isEnabled = false
            btNext.setOnClickListener {
                val action = ProfileDetailFragmentDirections.actionProfileFragmentToConnectToFragment()
                findNavController().navigate(action)
            }
        } else {
            btNext.setText(R.string.button_back)
            btNext.setOnClickListener {
                // Возвращение к главной активити
                activity?.finish()
            }
        }


        //val textProfile = view.findViewById<TextView>(R.id.profile_main_textView)
        profileImageView = view.findViewById(R.id.profile_avatar_image)
        textName = view.findViewById(R.id.profile_input_name)
        textLastName = view.findViewById(R.id.profile_input_lastname)
        textBirthDate = view.findViewById(R.id.profile_input_birth_date)
        spinnerGender = view.findViewById(R.id.profile_input_gender)
        spinnerCountry = view.findViewById(R.id.profile_input_country)
        buttonSave = view.findViewById(R.id.profile_button_save)
        // Настройка роста
        spinnerHeight = view.findViewById(R.id.profile_input_growth)
        //setupGrowth()
        // Настройка weight
        spinnerWeight = view.findViewById(R.id.profile_input_weight)
        //setupWeight()

        // Пока ничего не изменилось сохранять нечего
        buttonSave.isEnabled = false //setSaveButton()

        val onItemSelectedListener = object :OnItemSelectedListener {
            override fun onNothingSelected() {
            }

            override fun onItemSelected(view: View?, position: Int, id: Long) {
               setSaveButton() //testListener()
            }
        }

        // Проверка всех полей и активация кнопки Save
        textName.setOnFocusChangeListener { _ , hasFocus ->  if (!hasFocus) setSaveButton() }
        textLastName.setOnFocusChangeListener { _ , hasFocus ->  if (!hasFocus) setSaveButton() }
        spinnerGender.setOnItemSelectedListener(onItemSelectedListener)
        spinnerCountry.setOnItemSelectedListener(onItemSelectedListener)
        spinnerWeight.setOnItemSelectedListener(onItemSelectedListener)
        spinnerHeight.setOnItemSelectedListener(onItemSelectedListener)

        // Настройка smoker
        textSwitch = view.findViewById(R.id.profile_smoker_content_input)
        btSwitch = view.findViewById(R.id.profile_smoker_switcher)
        btSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) textSwitch.setText(R.string.yes) else textSwitch.setText(R.string.no)

            // Проверка всех полей и активация кнопки Save
            setSaveButton()
        }
        //btSwitch.isChecked = false


        //DatePicker
        textBirthDate.setOnClickListener {
            //val picker = MaterialDatePicker.Builder.datePicker().build()
            //picker.show(parentFragmentManager, picker.toString())
            val dateText = textBirthDate.text.split('.')
            var yearDef = 1990
            var monthDef = 0
            var dayDef = 1
            if (dateText.count() == 3){
                dayDef = dateText[0].toInt()
                monthDef = dateText[1].toInt() - 1
                yearDef = dateText[2].toInt()
            }

            val calendar = Calendar.getInstance()

            SpinnerDatePickerDialogBuilder()
                .context(context)
                .callback { _, year, monthOfYear, dayOfMonth ->
                    //Toast.makeText(context, "$dayOfMonth-${monthOfYear+1}-$year",Toast.LENGTH_SHORT).show()
                    val tempStr = "$dayOfMonth.${monthOfYear+1}.$year"
                    textBirthDate.text = tempStr

                    /*val cal = Calendar.getInstance()
                    cal.set(year,monthOfYear,monthOfYear)
                    textBirthDate.setText(cal.timeInMillis.toString())*/
                }
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                //.customTitle("My custom title")
                .showDaySpinner(true)
                .defaultDate(yearDef, monthDef, dayDef)
                .maxDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .minDate(1900, 0, 1)
                .build()
                .show()

            // Проверка всех полей и активация кнопки Save
            setSaveButton()
        }


        // Запрос DashBoard (Обязательно первее всехб иначе будет null)
        if (MainViewModel.singleDashBoard != null){
            dashBoard = MainViewModel.singleDashBoard!!
            // Установка начальных значений
            // Запросы
            setupGenders()
            setupCountries()
            setupHeight()
            setupWeight()
            // Установка параметров
            setParametersDashBoard()
        } else disposableDashBoard = viewModel.getDashBoardFromInternet()
            .subscribe(
                { db ->
                    MainViewModel.singleDashBoard = db
                    dashBoard = db!!
                    // Запросы
                    setupGenders()
                    setupCountries()
                    setupHeight()
                    setupWeight()
                    // Установка параметров
                    setParametersDashBoard() },
                { error ->
                    updateTokenIfItNeed(
                        this.requireContext(),
                        viewModel,
                        error
                    )
                    showErrorIfNeed(
                        this.requireContext(),
                        error
                    )
                    //finishActivityIfTokenNotValid(this.requireActivity(), error)
                })


        // Запрос Profile
        if (MainViewModel.singleProfile != null) {
            profile = MainViewModel.singleProfile
            setParametersProfile()
        } else disposableProfile = viewModel.getProfileFromInternet()
                .subscribe(
                    { profile -> //textProfile.text = profile.toString()
                        this.profile = profile
                        setParametersProfile()
                        //profile?.let { viewModel.setProfile(profile)}
                    },
                    { error ->
                        updateTokenIfItNeed(
                            this.requireContext(),
                            viewModel,
                            error
                        )
                        showErrorIfNeed(
                            this.requireContext(),
                            error
                        )
                        //finishActivityIfTokenNotValid(this.requireActivity(), error)
                    })

        val buttonSave = view.findViewById<Button>(R.id.profile_button_save)
        buttonSave.setOnClickListener {

            //Toast.makeText(this.requireContext(), spinnerCountry.selectedItem.toString(), Toast.LENGTH_SHORT).show()
            isProgress(true)
            buttonSave.isEnabled = false

            finishSaveDataGood.set(2)
            finishSaveData.set(2)
            //liveResultSaveData.value = finishSaveData.get()  // = AtomicInteger(2) //.set(2)

            dashBoard?.let {
                try {
                    //val item = spinnerGender.selectedPosition
                    it.gender = listGenders.genders.find { gender -> gender.value == spinnerGender.selectedItem}?.id
                    if (it.gender == null) it.gender = 3
                    else if( it.gender!! < 0) it.gender = 3
                } catch (ex: Exception){
                    it.gender = 3
                }
                try {
                    it.country = listCountries.countries.find { country -> country.value == spinnerCountry.selectedItem}?.id
                    if (it.country == null) it.country = 1
                    else if( it.country!! < 0) it.country = 1
                } catch (ex: Exception){
                    it.country = 1
                }
                try {
                    it.weight = when (MainViewModel.measuringSystem) {
                        MeasuringSystem.SI -> spinnerWeight.selectedItem.toString().replace(',','.').toFloatOrNull()
                        MeasuringSystem.IMPERIAL -> converterLbToKg(
                            spinnerWeight.selectedItem.toString().replace(',', '.').toFloatOrNull()
                        )
                    }
                }catch (ex: Exception){
                    it.weight = when (MainViewModel.measuringSystem) {
                        MeasuringSystem.SI -> 70f
                        MeasuringSystem.IMPERIAL -> converterLbToKg(
                            70f
                        )
                    }
                }
                try {
                    it.height = when (MainViewModel.measuringSystem) {
                        MeasuringSystem.SI -> spinnerHeight.selectedItem.toString().replace(',','.').toFloatOrNull()
                        MeasuringSystem.IMPERIAL -> converterInToSm(
                            spinnerHeight.selectedItem.toString().replace(',', '.').toFloatOrNull()
                        )
                    }
                }catch (ex: Exception){
                    it.weight = when (MainViewModel.measuringSystem) {
                        MeasuringSystem.SI -> 170f
                        MeasuringSystem.IMPERIAL -> converterInToSm(170f)
                    }
                }

                it.birth_date = textBirthDate.text.toString()
                it.smoker = btSwitch.isChecked
                it.locale = getStringLocale(this.requireContext())

                // Отправка обновлённых значений
                viewModel.setDashBoardToInternet(it)

                // Получение результата о сохранении
                disposableSetDashBoard = viewModel.getDashBoardFromInternet()
                    .subscribe(
                        {db -> MainViewModel.singleDashBoard = db
                            isDashBoardUpdate = true
                            if (isProfileUpdate) btNext.isEnabled = true

                            finishSaveDataGood.decrementAndGet()
                            liveResultSaveData.value = finishSaveData.decrementAndGet()
                        },
                        { error ->
                            liveResultSaveData.value = finishSaveData.decrementAndGet()

                            updateTokenIfItNeed(this.requireContext(), viewModel, error)
                            showErrorIfNeed(this.requireContext(), error)
                        })
            }

            profile?.let{
                it.first_name = textName.text.toString()
                it.last_name = textLastName.text.toString()
                it.image = null // Заглушка

                disposableSetProfile = viewModel.setProfileToInternet(it)
                    .subscribe(
                        { profile -> MainViewModel.singleProfile = profile
                            isProfileUpdate = true
                            if (isDashBoardUpdate) btNext.isEnabled = true
                           //textProfile.text = profile.toString()

                            finishSaveDataGood.decrementAndGet()
                            liveResultSaveData.value = finishSaveData.decrementAndGet()
                        },
                        { error ->
                            liveResultSaveData.value = finishSaveData.decrementAndGet()
                            showErrorIfNeed(this.requireContext(), error) })
            }
        }

        // Результат дейтвий при нажатии кнопки сохранить
        liveResultSaveData.observe(this.viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it <= 0) {
                // Если вход в первый раз то выходим из экрана
                if (finishSaveDataGood.get() <= 0)
                    if (SettingsActivity.firstLoad) {
                        context?.getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE)?.edit { putBoolean(LoginViewModel.USER_FIRST_LOGIN, false) }
                        activity?.finish() // Редирект при первом сохранении
                    }

                isProgress(false)
                buttonSave.isEnabled = true
            }
        })

        // Выбор и загрузка фотографии
        profileImageView.setOnClickListener {
            //getImageDialog()

            /*if (isReadStoragePermissionGranted()) {
            getImageDialog()
            }*/
        }
        return view
    }

    private fun getImageDialog() {
        val intent = Intent(this.requireActivity(), FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.CONFIGS,
            Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .setShowVideos(false)
                //.enableImageCapture(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .build()
        )
        this.requireActivity()
            .startActivityForResult(intent, SettingsViewModel.FILE_IMAGE_REQUEST_CODE)
    }

    private fun isReadStoragePermissionGranted() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) true
            else {
                // Разрешения не предоставлены и соответственно их нужно запросить
                requestPermissions(listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray(),
                    SettingsViewModel.REQUIRE_PERMISSIONS_READ_REQUEST_CODE
                )
                false
            }
        }else{
            // Права получены при установке
            true
        }

    private fun isProgress(isProgress: Boolean) {
        progressBar.isIndeterminate = isProgress
        progressBar.isVisible = isProgress
    }

    // Запрос Countries
    private fun setupCountries() {
        if (MainViewModel.singleCountries != null) {
            listCountries = MainViewModel.singleCountries!!
            // Установка начальных значений
            setParametersCountries()
            finishLoadProgressBar()
        } else disposableCountries = viewModel.getCountries(
            BodyCountries(
                getStringLocale(this.requireContext())
            )
        )
            .subscribe(
                { countries ->
                    listCountries = countries!!
                    setParametersCountries()
                    finishLoadProgressBar()
                    //if (BuildConfig.DEBUG) Toast.makeText(this.requireContext(), countries.toString(), Toast.LENGTH_SHORT).show()
                },
                { error ->
                    finishLoadProgressBar()
                    showErrorIfNeed(
                        this.requireContext(),
                        error
                    )
                })
    }

    // Запрос Genders
    private fun setupGenders() {
        if (MainViewModel.singleGenders != null) {
            listGenders = MainViewModel.singleGenders!!
            // Установка начальных значений
            setParametersGenders()
            finishLoadProgressBar()
        } else disposableGenders = viewModel.getGenders(getCurrentLocale(this.requireContext()))
            .subscribe(
                { genders ->
                    listGenders = genders!!
                    setParametersGenders()
                    finishLoadProgressBar()
                    //if (BuildConfig.DEBUG) Toast.makeText(this.requireContext(), genders.toString(), Toast.LENGTH_SHORT).show()
                },
                { error ->
                    finishLoadProgressBar()
                    showErrorIfNeed(
                        this.requireContext(),
                        error
                    )
                })
    }

    private fun finishLoadProgressBar() {
        if (finishLoadProgress.decrementAndGet() == 0) {
            isProgress(false)
        }
    }

    // Настройка роста
    private fun setupHeight() {
        val listHeight = when (MainViewModel.measuringSystem) {
            MeasuringSystem.SI -> MutableList(250) { (it + 50).toString() }
            MeasuringSystem.IMPERIAL -> MutableList(250) { converterSmToIn(
                (it + 50).toFloat()
            )?.roundTo(2).toString() }
        }
        val heightAdapter = SimpleListAdapterForSearchSpinner(this.requireContext(), listHeight)

        //ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, listGrowth)
        //growthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //spinnerGrowth.adapter = growthAdapter
        //spinnerGrowth.setSelection(100)
        spinnerHeight.setAdapter(heightAdapter)
        //spinnerGrowth.setOnClickListener { spinnerWeight.hideEdit()
        //spinnerWeight.clearFocus()}
    }

    // Настройка weight
    private fun setupWeight() {
        val listWeight = when (MainViewModel.measuringSystem) {
            MeasuringSystem.SI -> MutableList(290) { (it + 10).toString() }
            MeasuringSystem.IMPERIAL -> MutableList(290) { converterKgToLb(
                (it + 10).toFloat()
            )?.roundTo(2).toString() }
        }

        val weightAdapter = SimpleListAdapterForSearchSpinner(this.requireContext(), listWeight)
        //ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, listWeight)
        //weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //spinnerWeight.adapter = weightAdapter
        //spinnerWeight.setSelection(70)
        spinnerWeight.setAdapter(weightAdapter)
        /*spinnerWeight.isFocusableInTouchMode = true
        spinnerWeight.setOnClickListener {
            spinnerGrowth.clearFocus()
        }
        spinnerWeight.setOnFocusChangeListener { v, hasFocus ->
            Toast.makeText(this.requireContext(), "Focus - $hasFocus", Toast.LENGTH_SHORT).show()
            if (!hasFocus) spinnerWeight.clearFocus()
        }*/

        /*spinnerWeight.setOnItemSelectedListener(object: OnItemSelectedListener {
            override fun onNothingSelected() {
                //Toast.makeText(this@ProfileDetailFragment.requireContext(), "Nothing Selected", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(view: View?, position: Int, id: Long) {
                Toast.makeText(this@ProfileDetailFragment.requireContext(), "Selected -  ${spinnerWeight.selectedItem}", Toast.LENGTH_SHORT).show()
                //spinnerWeight.clearFocus()
            }

        })*/
    }

    private fun setParametersProfile() {
        textName.text?.clear()
        textName.text?.insert(0, profile?.first_name.toString())
        textLastName.text?.clear()
        textLastName.text?.insert(0, profile?.last_name.toString())

        if (profile?.image != null)
            Glide.with(this.requireContext())
                .load(profile?.image)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(profileImageView)
    }

    // Настройка пола
    private fun setParametersGenders(){
        val listGender = MutableList(listGenders.genders.size) {listGenders.genders[it].value}
        val genderAdapter = SimpleListAdapterForSearchSpinner(this.requireContext(), listGender)
            //ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, listGender)
        //genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //spinnerGender.adapter = genderAdapter
        spinnerGender.setAdapter(genderAdapter)

        var position = dashBoard?.gender ?: 3
        //listSex.indexOf(listSex.find { position == it.id })
        if (position < 0) position = 3 // дополнительная проверка для защиты от вылетания
        spinnerGender.setSelectedItem(listGenders.genders.indexOf(listGenders.genders.find {  it.id == position }))
    }

    //Настройка Стран
    private  fun setParametersCountries(){
        val sortedCountries = listCountries.countries.sortedBy { it.value } // сортировка  стран
        val listCountry = MutableList(sortedCountries.size) {sortedCountries[it].value}
        val countryAdapter = SimpleListAdapterForSearchSpinnerWithLetter(this.requireContext(), listCountry)
            //ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, listCountry)
        //countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //spinnerCountry.adapter = countryAdapter
        spinnerCountry.setAdapter(countryAdapter)

        var position = dashBoard?.country ?: 1
        if (position <= 0) position = 1 // дополнительная проверка для защиты от вылетания
        spinnerCountry.setSelectedItem(sortedCountries.indexOf(sortedCountries.find { it.id  == position }))
    }

    private fun setParametersDashBoard() {

        textBirthDate.text = dashBoard?.birth_date ?: ""


        var position = dashBoard?.height?.roundToInt() ?: 170
        if (position != 0) position -= 50
        spinnerHeight.setSelectedItem(position) // null

        position = dashBoard?.weight?.roundToInt() ?: 70
        if (position != 0) position -= 10
        spinnerWeight.setSelectedItem(position)

        btSwitch.isChecked = dashBoard?.smoker ?: false
        if (btSwitch.isChecked) textSwitch.setText(R.string.yes) else textSwitch.setText(R.string.no)
    }

    override fun onDestroy() {
        disposableProfile?.let { if (!it.isDisposed) it.dispose()}
        disposableSetProfile?.let { if (!it.isDisposed) it.dispose()}
        disposableDashBoard?.let { if (!it.isDisposed) it.dispose()}
        disposableSetDashBoard?.let { if (!it.isDisposed) it.dispose()}
        disposableGenders?.let { if (!it.isDisposed) it.dispose()}
        disposableCountries?.let { if (!it.isDisposed) it.dispose()}
        super.onDestroy()
    }

}


