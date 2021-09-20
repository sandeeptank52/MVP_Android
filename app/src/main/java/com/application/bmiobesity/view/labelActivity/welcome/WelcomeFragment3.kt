package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LabelWelcomeFragment3Binding
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.viewModels.LabelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeFragment3 : Fragment(R.layout.label_welcome_fragment_3) {

    private var welcomeBinding3: LabelWelcomeFragment3Binding? = null
    private val labelModel: LabelViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding3 = LabelWelcomeFragment3Binding.bind(view)

        lifecycleScope.launch(Dispatchers.IO) {
            labelModel.setBooleanParam(AppSettingDataStore.PrefKeys.SHOW_DISCLAIMER, false)
        }
        welcomeBinding3?.welcome3SwitchDisclaimer?.setOnCheckedChangeListener { _, isChecked ->
            labelModel.setBooleanParam(AppSettingDataStore.PrefKeys.SHOW_DISCLAIMER, !isChecked)
        }
    }

    override fun onDestroyView() {
        welcomeBinding3 = null
        super.onDestroyView()
    }
}