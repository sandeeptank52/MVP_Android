package com.application.bmiobesity.view.mainActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Base64
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.billingclient.api.Purchase
import com.application.bmiobesity.R
import com.application.bmiobesity.common.EventObserver
import com.application.bmiobesity.common.eventManagerMain.EventManagerMain
import com.application.bmiobesity.common.eventManagerMain.MainActivityEvent
import com.application.bmiobesity.databinding.MainActivityV2Binding
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.services.google.signIn.GoogleSignInService
import com.application.bmiobesity.utils.getDateStrFromMS
import com.application.bmiobesity.view.loginActivity.LoginActivity
import com.application.bmiobesity.viewModels.MainViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: MainActivityV2Binding
    private val mainModel: MainViewModel by viewModels()
    private val eventManager: MainActivityEvent = EventManagerMain.getEventManager()
    private lateinit var navController: NavController
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var mGoogleSignInService: GoogleSignInService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = MainActivityV2Binding.inflate(layoutInflater)
        setTheme(R.style.Theme_DiseaseTrackerProductionCustom)
        setContentView(mainBinding.root)
         eventManager.getPreloadSuccessEvent().observe(this, EventObserver{
            if (it) mainBinding.mainFrameLayoutWaiting.visibility = View.GONE
        })

        eventManager.getPreloadSuccessEvent().observe(this, EventObserver{
            if (it) mainBinding.mainFrameLayoutWaiting.visibility = View.GONE
        })

        lifecycle.addObserver(mainModel.billingClient)

        init()


        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted ->
            if (isGranted){
                CropImage.activity()
                    .setAspectRatio(1,1)
                    .setRequestedSize(600, 600)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null){

            val result = CropImage.getActivityResult(data)
            val imageURI = result.uri

            val imageBitmap = BitmapFactory.decodeFile(imageURI.path)
            val os = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            val b64 = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT)
            val jpegBase64 = "data:image/jpeg;base64,${b64}"

            mainModel.patchAvatar(jpegBase64)
        }
    }

    private fun init(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        lifecycleScope.launch(Dispatchers.IO) {
            val firstTime = mainModel.isFirstTimeAsync().await()
            withContext(Dispatchers.Main){
                if (firstTime) {
                    mainBinding.mainBottomNavigationView.visibility = View.GONE
                    mainBinding.mainImageViewAvatarIcon.visibility = View.VISIBLE
                    mainBinding.mainEnterText.visibility = View.VISIBLE
                    mainBinding.mainForMoreAccurrate.visibility = View.VISIBLE
                    mainBinding.mainImageViewAvatarIconCenter.visibility = View.GONE
                    mainBinding.bottomNav.visibility = View.GONE
                    addListeners()
                } else {
                    IndicatorUiUpdate(R.id.mainHomeNav)
                    mainBinding.mainBottomNavigationView.visibility = View.VISIBLE
                    mainBinding.mainImageViewAvatarIcon.visibility = View.GONE
                    mainBinding.mainEnterText.visibility = View.GONE
                    mainBinding.mainForMoreAccurrate.visibility = View.GONE
                    mainBinding.mainImageViewAvatarIconCenter.visibility = View.VISIBLE
                    mainBinding.bottomNav.visibility = View.VISIBLE
                    initMainBottomNav()
                    initMainMenu()
                    addListeners()
                }
            }
        }
    }




    private fun initMainMenu(){
        mainBinding.mainMenu.setOnClickListener {
            val menu = PopupMenu(applicationContext, it)
            menu.menuInflater.inflate(R.menu.main_app_menu, menu.menu)
            menu.setOnMenuItemClickListener{ menuItem ->
                when (menuItem.itemId){
                    R.id.mainAppMenuSubs -> {subsMenuAction()}
                    R.id.mainAppMenuSetting -> {settingMenuAction()}
                    R.id.mainAppMenuLogOut -> {logOutMenuAction()}
                }
                return@setOnMenuItemClickListener true
            }
            menu.show()
        }
    }
    private fun initMainBottomNav(){
        findViewById<BottomNavigationView>(R.id.mainBottomNavigationView).setupWithNavController(navController)

    }

    private fun subsMenuAction(){
        navController.navigate(R.id.mainNavToSubs)
    }
    private fun settingMenuAction(){
        navController.navigate(R.id.mainNavToSetting)
    }
    private fun logOutMenuAction(){
        lifecycleScope.launch(Dispatchers.IO) {
            mainModel.appSetting.setStringParam(AppSettingDataStore.PrefKeys.USER_PASS, "")
            withContext(Dispatchers.Main){
                mGoogleSignInService.mGoogleSignInClient.signOut()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setAvatar(url: String){
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.avatar_icon)
            .circleCrop()
            .into(mainBinding.mainImageViewAvatarIcon)
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.avatar_icon)
            .circleCrop()
            .into(mainBinding.mainImageViewAvatarIconCenter)
    }
    private fun setSubscriptionInfo(time: Long){
        val currentDate = Date().time
        val expireDate = time + (14 * DateUtils.DAY_IN_MILLIS)

        val expireInfo = getString(R.string.main_subs_info, getDateStrFromMS(expireDate))
        val expired = getString(R.string.main_subs_expire)

        if (currentDate < expireDate){
            mainBinding.mainTextViewSubsInfo.text = expireInfo
            mainBinding.mainTextViewSubsInfo.setTextColor(resources.getColor(R.color.transparent, null))
            mainModel.profileManager.trialPeriodExpired.postValue(false)
        } else {
            mainBinding.mainTextViewSubsInfo.text = expired
            mainBinding.mainTextViewSubsInfo.setTextColor(resources.getColor(R.color.transparent, null))
            mainModel.profileManager.trialPeriodExpired.postValue(true)
        }
    }

    private fun addListeners(){
        /*eventManager.getPreloadSuccessEvent().observe(this, EventObserver{
            if (it) mainBinding.mainFrameLayoutWaiting.visibility = View.GONE
        })*/
        mainBinding.home.setOnClickListener {
            IndicatorUiUpdate(R.id.mainHomeNav)
        }
        mainBinding.medsCard.setOnClickListener {
            IndicatorUiUpdate(R.id.mainMedcardNav)
        }
        mainBinding.profile.setOnClickListener {
            IndicatorUiUpdate(R.id.mainProfileNav)
        }
        mainBinding.mainImageViewAvatarIcon.setOnClickListener {

            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    CropImage.activity()
                        .setAspectRatio(1,1)
                        .setRequestedSize(600, 600)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(this)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }

        mainModel.profileManager.currentProfile.observe(this, {
            it?.let {
                mainBinding.mainTextViewFirstName.text = it.firstName
                mainBinding.mainTextViewLastName.text = it.lastName
                if (it.imageURI.isNotEmpty()){
                    setAvatar(it.imageURI)
                }
                setSubscriptionInfo(it.firsTimeStamp * 1000)
            }
        })

        mainModel.billingClient.purchaseListLive.observe(this, { purchaseConfigs ->
            purchaseConfigs?.let { purchaseConfigList ->
                val purchasePersonal = purchaseConfigList.find { it.sku == "test_sub" && it.purchaseState == Purchase.PurchaseState.PURCHASED }
                if (purchasePersonal != null){
                    mainBinding.mainTextViewSubsInfo.visibility = View.GONE
                } else {
                    mainBinding.mainTextViewSubsInfo.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun IndicatorUiUpdate(pos:Int){
        if (pos==R.id.mainHomeNav){
            navController.navigate(R.id.mainHomeNav)
            mainBinding.homeIndicator.visibility = View.VISIBLE
            mainBinding.profileIndicator.visibility = View.GONE
            mainBinding.medsCardIndicator.visibility = View.GONE
        }else if (pos==R.id.mainMedcardNav){
            navController.navigate(R.id.mainMedcardNav)
            mainBinding.homeIndicator.visibility = View.GONE
            mainBinding.profileIndicator.visibility = View.GONE
            mainBinding.medsCardIndicator.visibility = View.VISIBLE
        }
        else{
            navController.navigate(R.id.mainProfileNav)
            mainBinding.homeIndicator.visibility = View.GONE
            mainBinding.profileIndicator.visibility = View.VISIBLE
            mainBinding.medsCardIndicator.visibility = View.GONE
        }
    }



}