package com.application.bmiobesity.view.mainActivity.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainSettingFragmentBinding

class SettingFragment : Fragment(R.layout.main_setting_fragment) {

    private var settingBinding: MainSettingFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingBinding = MainSettingFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        settingBinding = null
        super.onDestroyView()
    }
}