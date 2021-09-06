package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LabelWelcomeFragment3Binding
import com.application.bmiobesity.databinding.LabelWelcomeFragment3V2Binding

class WelcomeFragment3: BaseFragment(R.layout.label_welcome_fragment_3_v2) {
    private var welcomeBinding3: LabelWelcomeFragment3V2Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding3 = LabelWelcomeFragment3V2Binding.bind(view)
    }

    override fun onDestroyView() {
        welcomeBinding3 = null
        super.onDestroyView()
    }
}