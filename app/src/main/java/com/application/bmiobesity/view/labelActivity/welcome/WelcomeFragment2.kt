package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LabelWelcomeFragment2Binding

class WelcomeFragment2: BaseFragment(R.layout.label_welcome_fragment_2) {
    private var welcomeBinding: LabelWelcomeFragment2Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding = LabelWelcomeFragment2Binding.bind(view)
    }

    override fun onDestroyView() {
        welcomeBinding = null
        super.onDestroyView()
    }
}