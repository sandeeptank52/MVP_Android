package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LabelWelcomeFragment3Binding

class WelcomeFragment3: BaseFragment(R.layout.label_welcome_fragment_3) {
    private var welcomeBinding: LabelWelcomeFragment3Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding = LabelWelcomeFragment3Binding.bind(view)
    }

    override fun onDestroyView() {
        welcomeBinding = null
        super.onDestroyView()
    }
}