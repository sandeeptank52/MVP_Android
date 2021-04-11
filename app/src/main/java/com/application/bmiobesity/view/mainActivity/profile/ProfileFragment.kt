package com.application.bmiobesity.view.mainActivity.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainProfileFragmentBinding
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.viewModels.MainViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.main_profile_fragment) {

    private var profileBinding: MainProfileFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding = MainProfileFragmentBinding.bind(view)

        profileBinding?.test22?.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                mainModel.appSetting.setBooleanParam(AppSettingDataStore.PrefKeys.FIRST_TIME, !isChecked)
            }
        }
    }

    override fun onDestroyView() {
        profileBinding = null
        super.onDestroyView()
    }
}