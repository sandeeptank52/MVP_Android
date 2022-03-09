package com.application.bmiantiobesity.ui.settings

import androidx.lifecycle.ViewModel
import com.application.bmiantiobesity.InTimeApplication
import com.application.bmiantiobesity.utilits.CryptoApi
import javax.inject.Inject

class SettingsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    companion object{
        const val NAME_OF_SETTINGS_FRAGMENT = "Name_of_settings_fragment"
        const val REQUIRE_PERMISSIONS_READ_REQUEST_CODE = 10
        const val FILE_IMAGE_REQUEST_CODE = 11
    }

    @Inject
    lateinit var cryptoApi: CryptoApi

    init {
        //Dagger2
        InTimeApplication.component?.injectToViewModel(this)
    }

}
