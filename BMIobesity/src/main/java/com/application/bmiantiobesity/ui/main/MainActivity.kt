package com.application.bmiantiobesity.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.application.bmiantiobesity.*
import com.application.bmiantiobesity.retrofit.*
import com.application.bmiantiobesity.ui.googlefitapi.GoogleFitApiModel
import com.application.bmiantiobesity.ui.login.LoginViewModel
import com.application.bmiantiobesity.ui.login.LoginViewModel.Companion.USER_HAS_JUST_REGISTERED
import com.application.bmiantiobesity.ui.settings.SettingsActivity
import com.application.bmiantiobesity.ui.settings.SettingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

//Функция расширения для установки Title ToolBar
/*inline fun <reified T> Fragment.setToolbarTitle(id: Int, title:String) {
    if (requireActivity() is T) requireActivity().findViewById<Toolbar>(id).title = title
}*/

// Функция расшения для запуска Activity
inline fun <reified T> startSettingsActivity(context: Context, firstLogin: Boolean, nameOfFragment: String) {
    val mainActivity = Intent(context.applicationContext, T::class.java)
    mainActivity.putExtra(LoginViewModel.USER_FIRST_LOGIN, firstLogin)
    mainActivity.putExtra(SettingsViewModel.NAME_OF_SETTINGS_FRAGMENT, nameOfFragment)
    context.startActivity(mainActivity)
}

interface ChangeFragment{
    fun changeFragment(menuItem: MenuItem)
}

class MainActivity : AppCompatActivity(), ChangeFragment {

    companion object{
        lateinit var menu: Menu
    }

    private var disposableGetDashBoard: Disposable? = null
    private var disposableAutoSetDashBoard:Disposable? = null
    private var disposableGetDataFromEvent:Disposable? = null
    private var disposableGetFirstTimeResult:Disposable? = null
    private var disposableProfile: Disposable? = null

    private lateinit var navController:NavController

    private lateinit var bottomNavigationView : BottomNavigationView

    private val viewModel by viewModels<MainViewModel>()

    /*fun test() = GlobalScope.async(Dispatchers.Default) {
        threadTest()
    }

    suspend fun threadTest() {
        for (i in 0..10) {
            GoogleFitApiModel.publishDashBoard.onNext(i)
            Thread.sleep(3000)
        }
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        RxJavaPlugins.setErrorHandler { }

        //val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //test()

        // Получение данных из Login Activity
        val extras = intent.extras
        MainViewModel.userToken =
            ResultToken(
                extras?.getString(LoginViewModel.ACCESS_TOKEN, "").toString(),
                extras?.getString(LoginViewModel.REFRESH_TOKEN, "").toString()
            )

        // Загрузка профиля из интернета
        disposableProfile = viewModel.getProfileFromInternet()
            .subscribe(
                { profile -> MainViewModel.singleProfile = profile },
                { error ->
                    updateTokenIfItNeed(this, viewModel, error)
                    showErrorIfNeed(this, error)
                    //finishActivityIfTokenNotValid(this.requireActivity(), error)
                }
            )

        // Подписка на получение даты первого результата.
        disposableGetFirstTimeResult = viewModel.getTimeOfFirstResultFromInternet()
            .subscribe(
                { MainViewModel.singleTimeOfFirstResult =
                    try { it.timestamp?.substringBefore('.')?.toLong() }
                    catch (ex:Exception){ null }

                    Log.d("Time - ", it.toString() )},
                {error-> Log.d("Error", error.message ?: "")
                    updateTokenIfItNeed(this, viewModel, error)
                    Toast.makeText(this, parserError(error).toString(), Toast.LENGTH_SHORT).show()})

        // Контроль подключения к Google Fit
        //val fitnessViewModel = ViewModelProvider(this).get(GoogleFitApiModel::class.java)
        val fitnessViewModel by viewModels<GoogleFitApiModel>()

        // Отслеживание  подключений к Fit пользователем поклику на кнопку
        MainViewModel.accessToGoogleFitApi.observe(this, Observer {
            if (it) fitnessViewModel.startGoogleFitnessApi(this)
        })

        //Once we passed the login screen reset the variable to no be redirected to login after disclaimer
        getSharedPreferences(LoginViewModel.USER_LOGIN_SETTINGS, Context.MODE_PRIVATE).run {
            this.edit().putBoolean(USER_HAS_JUST_REGISTERED, false).apply()
        }
        // Отслеживание  подключений к Fit по настройкам
        val sharedPreferences = getSharedPreferences(GoogleFitApiModel.GOOGLE_API_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences?.let {
            val isAccessToGoogleFitApi = it.getBoolean(GoogleFitApiModel.ACCESS_TO_API, false)
            if (isAccessToGoogleFitApi) {
                // Дополнительная проверка, что пользователь вручную не запретил доступ
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {

                    fitnessViewModel.startGoogleFitnessApi(this)
                    //Toast.makeText(this, "True",Toast.LENGTH_SHORT).show()
                } else {
                    // Если пользователь вручную отключил права приложения
                    Toast.makeText(this, getString(R.string.need_to_connect_to_fit), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Контроль первого запуска приложения
        val firstLogin = extras?.getBoolean(LoginViewModel.USER_FIRST_LOGIN, false)
        firstLogin?.let {
            if (it) {
                // Запуск начального экрана
                startSettingsActivity<SettingsActivity>(
                    this,
                    true,
                    "ProfileDetailFragment"
                )
            }
            //else Toast.makeText(this, "Don`t first Load!", Toast.LENGTH_SHORT).show()
            //For test
            //startSettingsActivity<SettingsActivity>(this, false)
        }

        //Загрузка данных с сервера и готовность к их обработке
        disposableGetDashBoard = viewModel.getDashBoardFromInternet()
            .subscribe(
                { dashboard -> //GoogleFitApiModel.liveDashBoard.value = dashboard
                    MainViewModel.singleDashBoard = dashboard },
                { error -> Log.d("Error", error.message ?: "")
                    updateTokenIfItNeed(this, viewModel, error)
                    Toast.makeText(this, parserError(error).toString(), Toast.LENGTH_SHORT).show()
                    //finishActivityIfTokenNotValid(this, error)
                })

        // Главная подписка на отправку обновлений DashBoard
        mainUpdateDashBoard(viewModel)

        // Подписка на получение свежих данных с Google Fit и их отправку на сервер
        disposableGetDataFromEvent = MainViewModel.publishNewValueFromEvent
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {next ->
                    //Проверка необходимости обновления (работает)
                    //Toast.makeText(this, next.toString(), Toast.LENGTH_SHORT).show()
                    //При загрузке с сервера не обновлять
                    if (MainViewModel.singleDashBoard == null)
                        MainViewModel.singleDashBoard =
                            DashBoard(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                            )

                    //Отправлять только если значения поменялись
                    else //if (GoogleFitApiModel.singleDashBoard != next)
                    {
                        MainViewModel.singleDashBoard!!.updateField(next)

                        //Отправка данных на сервер (всё работает) (только лишнее срабатывание при первром обновлении(загрузке с сервера))
                        viewModel.setDashBoardToInternet(MainViewModel.singleDashBoard!!)
                        Log.d("UPDATE DASHBOARD -", "MA")
                    }},
                {error -> Log.d("Error MA-", error.message ?: "")
                    Toast.makeText(this, parserError(error).toString(), Toast.LENGTH_SHORT).show()})


        //Отслеживание изменений фрагментов
        //val view = findViewById<ConstraintLayout>(R.id.container_main_activity)
        navController = findNavController(R.id.nav_main_host_fragment)
        //viewModel.navController = navController

        // Настройка Toolbar на BackPressed
        //val appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        //findViewById<Toolbar>(R.id.main_toolbar).setupWithNavController(navController, appBarConfiguration)

        // Настройка BottomNavigation
        findViewById<BottomNavigationView>(R.id.main_menu_bottom).setupWithNavController(navController)

        //Обработка нажатий на нижнее меню
        bottomNavigationView = findViewById(R.id.main_menu_bottom)
        bottomNavigationView.setOnNavigationItemSelectedListener (onNavigationItemSelectedListener)
        menu = bottomNavigationView.menu


        // Обработчик изменений контроллера
        /*navController.addOnDestinationChangedListener { _ , destination, _ ->
            when(destination.id){
                R.id.mainFragment -> {
                    //bottomNavigationView.selectedItemId = R.id.main_menu_data
                    //Toast.makeText(this, "DataFragment", Toast.LENGTH_SHORT).show()
                    //Snackbar.make(view, "Главный фрагмент", Snackbar.LENGTH_SHORT).show()
                }
                R.id.profileMainFragment -> {}
                /*R.id.dataFragment -> {}
                R.id.doctorFragment ->{}*/
                else -> { //Snackbar.make(view, "Неизвестный фрагмент", Snackbar.LENGTH_SHORT).show()
                    }
            }
        }*/

    }

    private fun mainUpdateDashBoard(viewModel: MainViewModel) {
        disposableAutoSetDashBoard = viewModel.getSubcriberToSetDashBoard()
            .doOnError {
                updateTokenIfItNeed(this, viewModel, it)
                showErrorIfNeed(this, it)
                mainUpdateDashBoard(viewModel) // Переподписка в случае ошибки
            }
            .subscribe({ next -> viewModel.setChangeDashBoard(next)
                    //if (BuildConfig.DEBUG) Toast.makeText(this, next.toString(), Toast.LENGTH_SHORT).show()
                },
                { error ->
                    //updateTokenIfItNeed(this, viewModel, error)
                    showErrorIfNeed(this, error)
                    //finishActivityIfTokenNotValid(this, error)
                })
    }

    // Обработчик событий нажатий на нижнее меню
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.main_menu_main -> {
                // Необходимо для 3 и более
                navController.popBackStack(R.id.mainFragment, false) //R.id.mainFragmentOld
                return@OnNavigationItemSelectedListener true
            }
            R.id.main_menu_profile -> {
                navController.navigate(R.id.profileMainFragment)
                return@OnNavigationItemSelectedListener true }
            /*R.id.main_menu_data -> { navController.navigate(R.id.dataFragment)
                return@setOnNavigationItemSelectedListener true }
            R.id.main_menu_doctor -> { navController.navigate(R.id.doctorFragment)
                return@setOnNavigationItemSelectedListener true }*/
            else -> return@OnNavigationItemSelectedListener false
        }
    }

    override fun changeFragment(menuItem: MenuItem) {
        onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)
        bottomNavigationView.selectedItemId = menuItem.itemId
        //Toast.makeText(this, menuItem.title, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.mainFragment) //R.id.mainFragmentOld
            // MainFragment
            if (MainViewModel.isUpdateDashboard) Toast.makeText(this, getString(R.string.load_data_to_server),Toast.LENGTH_SHORT).show()
            else finishAffinity()
        else{
            // ProfileFragment
            if (navController.currentDestination?.id == R.id.profileMainFragment) {
                bottomNavigationView.selectedItemId =
                    R.id.main_menu_main // lastSelectedItem
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        disposableGetDashBoard?.let { if (!it.isDisposed) it.dispose()}
        disposableAutoSetDashBoard?.let { if (!it.isDisposed) it.dispose()}
        disposableGetDataFromEvent?.let { if (!it.isDisposed) it.dispose()}
        disposableGetFirstTimeResult?.let { if (!it.isDisposed) it.dispose()}
        disposableProfile?.let { if (!it.isDisposed) it.dispose()}

        super.onDestroy()
    }
}