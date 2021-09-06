package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LabelWelcomeFragment1Binding
import com.application.bmiobesity.databinding.LabelWelcomeFragment1V2Binding

class WelcomeFragment1: BaseFragment(R.layout.label_welcome_fragment_1_v2) {
    private var welcomeBinding1: LabelWelcomeFragment1V2Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding1 = LabelWelcomeFragment1V2Binding.bind(view)
    }

    override fun onDestroyView() {
        welcomeBinding1 = null
        super.onDestroyView()
    }
}