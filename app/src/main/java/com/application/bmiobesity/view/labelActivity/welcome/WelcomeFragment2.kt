package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.LabelWelcomeFragment2Binding

class WelcomeFragment2: BaseFragment(R.layout.label_welcome_fragment_2) {
    private var welcomeBinding2: LabelWelcomeFragment2Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding2 = LabelWelcomeFragment2Binding.bind(view)
    }

    override fun onDestroyView() {
        welcomeBinding2 = null
        super.onDestroyView()
    }
}