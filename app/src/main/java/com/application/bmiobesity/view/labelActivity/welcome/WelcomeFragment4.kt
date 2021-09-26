package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LabelWelcomeFragment4Binding

class WelcomeFragment4: Fragment(R.layout.label_welcome_fragment_4) {
    private var welcomeBinding4: LabelWelcomeFragment4Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding4 = LabelWelcomeFragment4Binding.bind(view)
    }

    override fun onDestroyView() {
        welcomeBinding4 = null
        super.onDestroyView()
    }
}