package com.application.bmiantiobesity.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import com.application.bmiantiobesity.AppComponent
import com.application.bmiantiobesity.BuildConfig
import com.application.bmiantiobesity.R
import com.application.bmiantiobesity.db.usersettings.ConfigToDisplay
import com.application.bmiantiobesity.retrofit.*
import com.application.bmiantiobesity.ui.settings.SettingsActivity
import com.application.bmiantiobesity.utilits.OnHorizontalSwipeListener
import com.application.bmiantiobesity.utilits.getCurrentLocale
import com.application.bmiantiobesity.utilits.getNowDateTime
import com.application.bmiantiobesity.utilits.getStringLocale
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

enum class TypeOfInformation{ BMI, OBESITY_LEVEL, IDEAL_WEIGHT, BASE_METABOLISM, CALORIES_TO_LOW_WEIGHT, WAIST_TO_HIP_PROPORTIONS, BIO_AGE, COMMON_RISK_LEVEL, PROGNOSTIC_AGE, FAT_PERCENT, BODY_TYPE, UNFILLED, UNFILLED_BUTTON, ERROR}

data class MainResult(val type:TypeOfInformation, val description:String,  val information:String, val color:String,  val date:String)

class MainFragment : Fragment(), PurchasesUpdatedListener {

    private lateinit var defaultColor: String
    private lateinit var locale: String

    companion object{
        const val SUBSCRIPTION = "test_sub" //"android.test.purchased"  //"android.test.item_unavailable"  //"android.test.canceled"
        const val PERIOD_OF_TEST = DateUtils.DAY_IN_MILLIS * 14
    }

    //private var disposableRisk: Disposable? = null
    private var disposableRiskInternet: Disposable? = null
    private var disposableRecommendations: Disposable? = null

    private lateinit var userConfigToDisplay: List<ConfigToDisplay>
    private lateinit var swipeListener: SwipeRefreshLayout.OnRefreshListener

    private val viewModel by viewModels<MainViewModel>() // lazy create

    private lateinit var billingClient: BillingClient
    private var isBillingClientOk = false
    private var isSupportedSubscription = false
    private var isActiveSubscription = false
    private var userResult: Result? = null
    private var userRecommendation: MutableList<CommonRecommendationsAdapter>? = null

    //private val job = SupervisorJob()
    //private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainFragment","OnCreate")

        billingClient = BillingClient.newBuilder(this.requireContext()).setListener(this).enablePendingPurchases().build()
    }

    override fun onResume() {
        super.onResume()
        // Реализовать проверку покупок
        requireBillingSubscription()
        Log.d("MainFragment", "BillingClientOK - $isBillingClientOk")

        //Test Coroutine
        //lifecycleScope.launch { testCoroutine(10) }
        //Toast.makeText(this@MainFragment.requireContext(), "Timer finish - $result s", Toast.LENGTH_LONG).show()
    }


    /*private suspend fun testCoroutine(timeSecond:Long){
        withContext(Dispatchers.Default){
            delay(timeSecond * 1000)
        }
        //finishWork.onWorkFinished(timeSecond.toInt())
        Toast.makeText(this@MainFragment.requireContext(), "Timer finish - $timeSecond s", Toast.LENGTH_LONG).show()
    }*/

    private fun requireBillingSubscription(){
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    isBillingClientOk = true

                    // Проверка поддрежки подписки на устройстве
                    val response = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
                    isSupportedSubscription = response.responseCode == BillingClient.BillingResponseCode.OK

                    // Проверка подписки на устройстве
                    val purchasesResult: Purchase.PurchasesResult? = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                    val activeSubscription = purchasesResult?.purchasesList?.find { it.sku == SUBSCRIPTION }
                    activeSubscription?.let {
                        isActiveSubscription = it.purchaseState == Purchase.PurchaseState.PURCHASED
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                isBillingClientOk = false
            }
        })
    }

    // Покупки произведены
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty()){
            for (purchase in purchases){
                Log.d("Purchases", purchase.toString())

                if (purchase.sku == SUBSCRIPTION) {
                    // Проверка активации подписки
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        // Отображение для пользователя
                        isActiveSubscription = true
                        userResult?.let { createAllRecyclerView(it) }
                        userRecommendation?.let { MainViewModel.updateRecomendations.value = it }

                        // Активировать покупку если ещё не активировано
                        lifecycleScope.launch { acknowledgePurchase(purchase) }
                        //coroutineScope.launch { acknowledgePurchase(purchase) }
                    }
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED){
            Toast.makeText(this.requireContext(), getText(R.string.user_cancel_billing), Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Purchase", billingResult.debugMessage)
            Toast.makeText(this.requireContext(), getText(R.string.error_billing), Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
            val ackPurchasesResult = withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        locale = getStringLocale(this.requireContext()).substringBefore('-')

        val view = inflater.inflate(R.layout.main_fragment, container, false)

        val editButton = view.findViewById<AppCompatImageView>(R.id.edit_button)
        // Настройка Toolbar
        //setToolbarTitle<MainActivity>(R.id.main_toolbar, getString(R.string.main))
        @SuppressLint("ResourceType")
        defaultColor = this.resources.getString(R.color.text_disease)

        val viewPager = view.findViewById<ViewPager>(R.id.main_viewpager)
        val myFragmentPagerAdapter = MainFragmentPagerAdapter(this.requireContext(), this.childFragmentManager)
        //viewPager.adapter = null
        viewPager.adapter = myFragmentPagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        editButton.visibility = View.VISIBLE
                    }else -> {
                        editButton.visibility = View.GONE
                    }
                }
            }
        })

        val tabLayout = view.findViewById<TabLayout>(R.id.main_tab)
        tabLayout.setupWithViewPager(viewPager)
        editButton.setOnClickListener {
            viewModel.showEditDialog()
        }

        // Настройка адаптера для последующей оплаты
        val myDataAdapter = CommonRecommendationsSetAdapter.getInstance(object : CommonRecommendationsSetAdapter.Callback {
            override fun onItemClicked(item: CommonRecommendationsAdapter) {
                //Сюда придёт элемент, по которому кликнули. Можно дальше с ним работать

                //Snackbar.make(view, item.message_short ?: "", Snackbar.LENGTH_LONG).show()
                if (item.typeOfAdapter == TypeOfAdapter.BUTTON)
                    if (isBillingClientOk and isSupportedSubscription) queryBillings()
                if (item.typeOfAdapter == TypeOfAdapter.ERROR)
                    Toast.makeText(context, item.message_short, Toast.LENGTH_SHORT).show()
            }
        })
        MainViewModel.updateCommonRecommendationsAdapter.value = myDataAdapter

        //Настройка Swipe
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.main_swipe)
        swipeRefreshLayout.setColorSchemeResources(R.color.text_disease)

        //Обновление Swipe
        swipeListener = SwipeRefreshLayout.OnRefreshListener {
            //swipeRefreshLayout.isRefreshing = true
            swipeRefreshLayout.isRefreshing = false // отключение из-за skeleton
            //val skeletonScreen = skeletonBuilder.show()

            // Обнуление рекомендаций
            MainViewModel.updateRecomendations.value = mutableListOf(CommonRecommendationsAdapter(TypeOfAdapter.UNFILLED, null, null, "CLEAR"))

            MainViewModel.isSkeletonLoading.value = true

            // Получить рзультат
            disposableRiskInternet = viewModel.getResultFromInternet(locale) // getResult()
                //.delay(3, TimeUnit.SECONDS) // Test Skeleton
                .subscribe(
                    { result ->
                        //skeletonScreen.hide()
                        //для синхронизации и исключение одинаковых событий
                        result.common_recomendations?.add(0, CommonRecomendations(null, null, "FIRST_RESULT"))

                        MainViewModel.isSkeletonLoading.value = false
                        MainViewModel.updateSingleResult.value = result
                        //swipeRefreshLayout.isRefreshing = false
                        userResult = result
                        result?.let {  createAllRecyclerView(it) }
                        //if (BuildConfig.DEBUG) Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show()
                    },
                    { error ->
                        //skeletonScreen.hide()
                        MainViewModel.isSkeletonLoading.value = false

                        //swipeRefreshLayout.isRefreshing = false
                        updateTokenIfItNeed(this.requireContext(), viewModel, error)
                        showErrorIfNeed(this.requireContext(), error)

                        // For Test
                        if (BuildConfig.DEBUG) {
                            val result =
                                Result(
                                    listOf("25.5", "#000000"), listOf("no","#000000"), 80.0f, 2000, null, 0.8f, 35, listOf("medium","#000000"), 85, listOf("35%", "#000000"),"Body Type",
                                    listOf(
                                        DiseaseRisk(1, "#FF0000", "76f", "Hi risk of diabetes", "Low your weight"),
                                        DiseaseRisk(2, "#00FF00", "54f", "Medium risk of stroke", "Decrease your cigarettes counts"),
                                        DiseaseRisk(3, "#0000FF", "5f", "Medium risk of stroke werre wtrewtrert wertwrtwtr trwrtewrtwr", "Decrease your cigarettes counts t trwtrwwt twtrw"),
                                        DiseaseRisk(4, "#FF0000", "35.5 %", "Low risk of nothing", "Decrease your cigarettes counts"),
                                        DiseaseRisk(5, "000000", null, "Low risk of null", "Null")
                                    ),
                                    mutableListOf(
                                        CommonRecomendations(null, null, "FIRST_RESULT"),
                                        CommonRecomendations("Visit your cardiologyst.", "Go", null),
                                        CommonRecomendations("Visit your home.", "Go home", null)
                                    ),
                                    "")
                            //val result = Result(null, null, null, null, null, null, null, null, null, null, null, null)

                            MainViewModel.updateSingleResult.value = result
                            createAllRecyclerView(result)
                        }
                        //finishActivityIfTokenNotValid(this.requireActivity(), error)
                    })

            // Получение рекомендаций
            disposableRecommendations = viewModel.getRecomendationsFromInternet(locale)
                .subscribe(
                    {result ->
                        //для синхронизации и исключение одинаковых событий
                        result.add(0, CommonRecommendationsAdapter(TypeOfAdapter.DATA, null, null, "FIRST_RECOMMENDATION"))

                        //Test
                        //result.add(result.size, CommonRecommendationsAdapter(TypeOfAdapter.BUTTON, "Test", null, null))

                        if(MainViewModel.needToShowRecommendation) MainViewModel.updateRecomendations.value = result

                        userRecommendation = result},
                    {error ->
                        updateTokenIfItNeed(this.requireContext(), viewModel, error)
                        showErrorIfNeed(this.requireContext(), error)

                        if (BuildConfig.DEBUG) {
                            val result = mutableListOf(
                                CommonRecommendationsAdapter(TypeOfAdapter.DATA, null, null, "FIRST_RECOMMENDATION"),
                                CommonRecommendationsAdapter(TypeOfAdapter.DATA, null,"Test", null),
                                CommonRecommendationsAdapter(TypeOfAdapter.DATA,null, "Test good!", null)
                            )
                            MainViewModel.updateRecomendations.value = result
                            userRecommendation = result
                        }
                    })

        }

        swipeRefreshLayout.setOnRefreshListener(swipeListener)

        //
        MainViewModel.updateSwipeRefresh.observe(this.requireActivity(), Observer {
            if (it) swipeListener.onRefresh()
        })

        // Первый запуск загрузки данных c учётом полученных настроек
        viewModel.getUserSettings(this.requireActivity()).observe(this.requireActivity(), androidx.lifecycle.Observer {
            userConfigToDisplay = it
            MainViewModel.updateSwipeRefresh.value = true //swipeListener.onRefresh()
        })


        val onProfileClickListener = View.OnClickListener{
            startSettingsActivity<SettingsActivity>(this.requireActivity(), false, "ProfileDetailFragment")
        }

        val textUserName = view.findViewById<TextView>(R.id.main_userName_textView)
        // Аватар
        val avatarImage = view.findViewById<ImageView>(R.id.main_avatar_imageView)
        avatarImage.setOnClickListener (onProfileClickListener)
        textUserName.setOnClickListener (onProfileClickListener)


        // Обновление значений профиля (имя, далее картинка)
        MainViewModel.updateProfile.observe(this.requireActivity(), androidx.lifecycle.Observer {
            if (it.last_name.isNotEmpty()) textUserName.text = "${it.first_name} ${it.last_name}"
            else textUserName.text = it.first_name

            // Здесь загрузка картинки
            if (it.image != null)
                Glide.with(this.requireContext())
                    .load(it.image)
                    .placeholder(R.drawable.ic_avatar)
                    .circleCrop()
                    .into(avatarImage)
        })


        // Редактирование избранного
        /*val editSettings = view.findViewById<TextView>(R.id.main_edit_textview)
        editSettings.setOnClickListener {
            showSettingsDialog() // раскомитить
            //Toast.makeText(this.requireContext(), getText(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }*/

        // Добавления контролёра свайпа
        //val nestedScrollView = view.findViewById<ConstraintLayout>(R.id.main_fragment)
        //listenHorizontalSwipe(nestedScrollView)

        return  view
    }

    //Обработчик свайпа
    private fun listenHorizontalSwipe(view: View){
        view.setOnTouchListener(object : OnHorizontalSwipeListener(this@MainFragment.requireContext()){
            override fun onRightSwipe() {
                //Work good
                Log.d("Swipe-MF", "Swipe RIGHT!")
            }
            override fun onLeftSwipe() {
                //Work good
                /*try {
                    val mainActivity = this@MainFragment.activity as ChangeFragment
                    mainActivity.changeFragment( MainActivity.menu.findItem(R.id.main_menu_profile))
                }catch (ex: Exception){
                    Log.d("Swipe-MF", ex.message ?: "")
                }*/
                Log.d("Swipe-MF", "Swipe LEFT!")
            }
        })
    }



    private fun createAllRecyclerView(result: Result) {

        if (result.unfilled.isNullOrEmpty()) {
            if ((result.base_metabolism == null) and (result.bio_age == null) and (result.bmi.isNullOrEmpty()) and (result.calories_to_low_weight == null) and (result.common_risk_level.isNullOrEmpty()) and
                (result.ideal_weight == null) and (result.prognostic_age == null) and (result.obesity_level.isNullOrEmpty()) and (result.waist_to_hip_proportion == null))
            {
                fillRecyclerIfNotGoodResult(result)
            } else {
                // Все данные пришли с сервера
                val timeFirst = MainViewModel.singleTimeOfFirstResult ?: System.currentTimeMillis() - (DateUtils.DAY_IN_MILLIS * 13) // Время от бэкэнда
                //val timeFirst = System.currentTimeMillis() - (DateUtils.DAY_IN_MILLIS * 13) // Время от бэкэнда

                // Проверка оплаты или пробного периода
                if (isNeedPayOrPeriod(timeFirst))
                {
                    // не оплачено и пробный период истёк
                    MainViewModel.needToShowRecommendation = false
                    fillRecyclerPleasePayBill()
                } else {
                    MainViewModel.needToShowRecommendation = true
                    //updateViewPager(result)
                }

                updateViewPager(result)
            }
        } else {
            fillRecyclerIfNotGoodResult(result)
        }
    }

    // Проверка оплаты или пробного периода
    private fun isNeedPayOrPeriod(timeFirst: Long) =
    if ((System.currentTimeMillis() - PERIOD_OF_TEST) > timeFirst) { !isActiveSubscription } else { false }
    //Проверка наличия оплаченной подписки
    //private fun isAlreadyPay() = false

    private fun queryBillings() { //suspend
        val skuList = ArrayList<String>()
        skuList.add(SUBSCRIPTION)

        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)

        /*val skuDetailsResult = withContext(Dispatchers.IO){
            billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetails ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetails.isNullOrEmpty()) {
                    val flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails.first()).build()
                    billingClient.launchBillingFlow(this@MainFragment.requireActivity(), flowParams)
                }
            }
        }*/

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetails ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetails.isNullOrEmpty()) {
                val flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails.first()).build()
                billingClient.launchBillingFlow(this.requireActivity(), flowParams)
            }
        }

    }

    private fun fillRecyclerPleasePayBill() {

        val data = mutableListOf<CommonRecommendationsAdapter>()
        if (isBillingClientOk) {
            if (isSupportedSubscription) {
                data.add(CommonRecommendationsAdapter(TypeOfAdapter.ERROR,getString(R.string.pay_period_false), getString(
                    R.string.please_pay), null))
                data.add(CommonRecommendationsAdapter(TypeOfAdapter.BUTTON,getString(R.string.pay_button),"", null))
            } else
                data.add(CommonRecommendationsAdapter(TypeOfAdapter.ERROR,getString(R.string.subscriptions_not_supported), "", null))
        }
        else{
            data.add(CommonRecommendationsAdapter(TypeOfAdapter.ERROR,getString(R.string.error_billing),"", null))
        }

        /*val myDataAdapter = MainRiskSetAdapter(data, object : MainRiskSetAdapter.Callback {
            override fun onItemClicked(item: MainResult) {
                //Сюда придёт элемент, по которому кликнули. Можно дальше с ним работать

                // Оплата
                //coroutineScope.launch { queryBillings() }

                //Work Bill
                if (isBillingClientOk and isSupportedSubscription) queryBillings()


                //queryBillings() // for test

                //Snackbar.make(view, item.description, Snackbar.LENGTH_LONG).show()
                //Toast.makeText(context, item.description, Toast.LENGTH_SHORT).show()
            }
        })*/
        /*val myDataAdapter = CommonRecommendationsSetAdapter.getInstance(object : CommonRecommendationsSetAdapter.Callback {
            override fun onItemClicked(item: CommonRecommendationsAdapter) {
                //Сюда придёт элемент, по которому кликнули. Можно дальше с ним работать

                //Snackbar.make(view, item.message_short ?: "", Snackbar.LENGTH_LONG).show()
                if (item.typeOfAdapter == TypeOfAdapter.BUTTON)
                    if (isBillingClientOk and isSupportedSubscription) queryBillings()
                if (item.typeOfAdapter == TypeOfAdapter.ERROR)
                    Toast.makeText(context, item.message_short, Toast.LENGTH_SHORT).show()
            }
        })*/

        // Очистка данных
        //MainViewModel.updateSingleResult.value = Result(null, null, null, null, null, null, null, null, null, null, null, null, null, null)

        //MainViewModel.updateCommonRecommendationsAdapter.value = myDataAdapter

        data.add(0, CommonRecommendationsAdapter(TypeOfAdapter.DATA, null, null, "FIRST_RECOMMENDATION"))
        MainViewModel.updateRecomendations.value =  data //mutableListOf(CommonRecommendationsAdapter(TypeOfAdapter.UNFILLED, null, null, "CLEAR"))

        //MainViewModel.updateDataAdapter.value = myDataAdapter
    }

    private fun isFillAllData(): Boolean {
        val dashboard = MainViewModel.singleDashBoard ?: return false
        return !((dashboard.smoker == null) or (dashboard.gender == null) or (dashboard.country == null) or (dashboard.birth_date == null) or (dashboard.blood_pressure_dia == null) or (dashboard.blood_pressure_sys == null)
                or (dashboard.height == null) or (dashboard.heart_rate_alone == null) or (dashboard.cholesterol == null) or (dashboard.glucose == null) or (dashboard.hip == null)  or (dashboard.waist == null)
                or (dashboard.weight == null) or (dashboard.wrist == null) or (dashboard.locale == null))
    }

    private fun fillRecyclerIfNotGoodResult(result: Result) {

        val data = mutableListOf<MainResult>()

        // Проверить заполнение данных и вывести сообщение об ожидании если заполнено или что нужно заполнить как сейчас
        if (isFillAllData()) {
            data.add(MainResult(TypeOfInformation.UNFILLED, getString(R.string.unfilled_finish), getString(R.string.unfilled_finish_msg), defaultColor, ""))
            // Виталий попросил добавить
            data.add(MainResult(TypeOfInformation.UNFILLED_BUTTON, getString(R.string.unfilled_button), "", defaultColor, ""))
        }
        else {
            result.unfilled?.let {  data.add(MainResult(TypeOfInformation.UNFILLED, getString(R.string.unfilled), it, defaultColor, "")) }
            data.add(MainResult(TypeOfInformation.UNFILLED_BUTTON, getString(R.string.unfilled_button), "", defaultColor, ""))
        }

        val myDataAdapter = MainRiskSetAdapter(data, object : MainRiskSetAdapter.Callback {
            override fun onItemClicked(item: MainResult) {
                //Сюда придёт элемент, по которому кликнули. Можно дальше с ним работать

                // Переход на фрагмент с данными
                val action = MainFragmentDirections.actionMainFragmentToDataFragment()
                findNavController().navigate(action)

                //Snackbar.make(view, item.information, Snackbar.LENGTH_LONG).show()
                //Toast.makeText(context, item.description, Toast.LENGTH_SHORT).show()
            }
        })

        MainViewModel.updateDataAdapter.value = myDataAdapter
    }

    private fun updateViewPager(result: Result) {

        val dateStr = getNowDateTime(this.requireContext())

        val data = mutableListOf<MainResult>()
        try {
            if (!result.common_risk_level.isNullOrEmpty() && (userConfigToDisplay.find { it.type == TypeOfInformation.COMMON_RISK_LEVEL }?.value != false))
                data.add(MainResult(TypeOfInformation.COMMON_RISK_LEVEL, getString(R.string.common_risk_level), result.common_risk_level?.first() ?: "", result.common_risk_level?.get(1) ?: defaultColor, dateStr))
        } catch (ex:Exception){}
        try {
            if (!result.bmi.isNullOrEmpty() && (userConfigToDisplay.find { it.type == TypeOfInformation.BMI }?.value != false))
                data.add(MainResult(TypeOfInformation.BMI, getString(R.string.bmi), result.bmi?.first().toString(), result.bmi?.get(1) ?: defaultColor, dateStr))
        } catch (ex:Exception){}
        try{
            if (!result.obesity_level.isNullOrEmpty() && (userConfigToDisplay.find { it.type == TypeOfInformation.OBESITY_LEVEL }?.value != false))
                data.add(MainResult(TypeOfInformation.OBESITY_LEVEL, getString(R.string.obesity_level), result.obesity_level?.first() ?: "", result.obesity_level?.get(1) ?: defaultColor, dateStr))
        } catch (ex:Exception){}
        try {
            if ((result.ideal_weight != null) && (userConfigToDisplay.find { it.type == TypeOfInformation.IDEAL_WEIGHT }?.value != false))
                data.add(MainResult(TypeOfInformation.IDEAL_WEIGHT, getString(R.string.ideal_weight), result.ideal_weight!!.toString(), defaultColor, dateStr))
        }catch (ex:Exception){}
        try {
            if ((result.base_metabolism != null) && (userConfigToDisplay.find { it.type == TypeOfInformation.BASE_METABOLISM }?.value != false))
                data.add(MainResult(TypeOfInformation.BASE_METABOLISM, getString(R.string.base_metabolism), result.base_metabolism!!.toString(), defaultColor, dateStr))
        }catch (ex:Exception){}
        try {
            if ((result.calories_to_low_weight != null) && (userConfigToDisplay.find { it.type == TypeOfInformation.CALORIES_TO_LOW_WEIGHT }?.value != false))
                data.add(MainResult(TypeOfInformation.CALORIES_TO_LOW_WEIGHT, getString(R.string.calories_to_low_weight), result.calories_to_low_weight!!.toString(), defaultColor, dateStr))
        }catch (ex:Exception){}
        try {
            if ((result.waist_to_hip_proportion != null) && (userConfigToDisplay.find { it.type == TypeOfInformation.WAIST_TO_HIP_PROPORTIONS }?.value != false))
                data.add(MainResult(TypeOfInformation.WAIST_TO_HIP_PROPORTIONS, getString(R.string.waist_to_hip_proportion), result.waist_to_hip_proportion!!.toString(), defaultColor, dateStr))
        }catch (ex:Exception){}
        try {
            if ((result.bio_age != null) && (userConfigToDisplay.find { it.type == TypeOfInformation.BIO_AGE }?.value != false))
                data.add(MainResult(TypeOfInformation.BIO_AGE, getString(R.string.bio_age), result.bio_age!!.toString(), defaultColor, dateStr))
        }catch (ex:Exception){}
        try {
            if ((result.prognostic_age != null) && (userConfigToDisplay.find { it.type == TypeOfInformation.PROGNOSTIC_AGE }?.value != false))
                data.add(MainResult(TypeOfInformation.PROGNOSTIC_AGE, getString(R.string.progrostic_age), result.prognostic_age!!.toString(), defaultColor, dateStr))
        }catch (ex:Exception){}
        try {
            if (!result.fat_percent.isNullOrEmpty() && (userConfigToDisplay.find { it.type == TypeOfInformation.FAT_PERCENT }?.value != false))
                if (!result.fat_percent?.get(0).isNullOrEmpty())
                    data.add(MainResult(TypeOfInformation.FAT_PERCENT, getString(R.string.fat_percent), result.fat_percent?.get(0) ?: "", result.fat_percent?.get(1) ?: defaultColor, dateStr))
        } catch (ex:Exception){}
        try {
            if (!result.body_type.isNullOrEmpty() && (userConfigToDisplay.find { it.type == TypeOfInformation.BODY_TYPE }?.value != false))
                data.add(MainResult(TypeOfInformation.BODY_TYPE, getString(R.string.body_type), result.body_type!!.toString(), defaultColor, dateStr))
        }catch (ex:Exception){}


        val myDataAdapter = MainRiskSetAdapter(data, object : MainRiskSetAdapter.Callback {
            override fun onItemClicked(item: MainResult) {
                //Сюда придёт элемент, по которому кликнули. Можно дальше с ним работать

                //Snackbar.make(view, item.information, Snackbar.LENGTH_LONG).show()
                //Toast.makeText(context, item.description, Toast.LENGTH_SHORT).show()
                //dbAction.delete(item)
            }
        })

        MainViewModel.updateDataAdapter.value = myDataAdapter
    }


    override fun onDestroy() {
        //disposableProfile?.let { if (!it.isDisposed) it.dispose()}
        //disposableRisk?.let { if (!it.isDisposed) it.dispose()}
        disposableRiskInternet?.let { if (!it.isDisposed) it.dispose()}

        billingClient.endConnection()
        super.onDestroy()
    }


    class MainFragmentPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int) = when (position){
            0 -> MainFragmentMainRisk.newInstance()
            1 -> MainFragmentDiseaseRisk.newInstance()
            2 -> MainFragmentCommonRecommendations.newInstance()
            else -> MainFragmentMainRisk.newInstance()
        }


        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> context.getString(R.string.favorites)
                1 -> context.getString(R.string.diseases)
                2 -> context.getString(R.string.common_recommendations)
                else -> context.getString(R.string.main)
            }
        }
    }

}

