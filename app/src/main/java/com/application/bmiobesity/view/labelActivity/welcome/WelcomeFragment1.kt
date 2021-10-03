package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LabelWelcomeFragment1Binding
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.viewModels.LabelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeFragment1: BaseFragment(R.layout.label_welcome_fragment_1) {
    private var welcomeBinding1: LabelWelcomeFragment1Binding? = null
    private val labelModel: LabelViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding1 = LabelWelcomeFragment1Binding.bind(view)
        lifecycleScope.launch(Dispatchers.IO) {
            labelModel.setBooleanParam(AppSettingDataStore.PrefKeys.SHOW_DISCLAIMER, false)
        }
        welcomeBinding1?.welcomeSwitchDisclaimer?.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                labelModel.setBooleanParam(AppSettingDataStore.PrefKeys.SHOW_DISCLAIMER, !isChecked)
            }
        }
    }

    override fun onDestroyView() {
        welcomeBinding1 = null
        super.onDestroyView()
    }
}