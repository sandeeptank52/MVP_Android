package com.application.bmiobesity.view.labelActivity.disclaimer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.R
import androidx.lifecycle.lifecycleScope
import com.application.bmiobesity.databinding.LabelDisclaimerFragmentBinding
import com.application.bmiobesity.model.appSettings.AppSettingDataStore
import com.application.bmiobesity.view.loginActivity.LoginActivity
import com.application.bmiobesity.view.mainActivity.MainActivity
import com.application.bmiobesity.viewModels.LabelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DisclaimerFragment : Fragment(R.layout.label_disclaimer_fragment) {

    private var disclaimerBinding: LabelDisclaimerFragmentBinding? = null
    private val labelModel: LabelViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disclaimerBinding = LabelDisclaimerFragmentBinding.bind(view)

        disclaimerBinding?.disclaimerSwitch?.setOnCheckedChangeListener { _, isChecked ->
            labelModel.setBooleanParam(AppSettingDataStore.PrefKeys.SHOW_DISCLAIMER, !isChecked)
        }
        disclaimerBinding?.disclaimerButtonNext?.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                labelModel.initAppPreference()
                when {
                    labelModel.isFirstTime() -> {
                        //is firsttime
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    else -> {
                        //Theres no need to progress
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }


        }
    }

    override fun onDestroyView() {
        disclaimerBinding = null
        super.onDestroyView()
    }
}