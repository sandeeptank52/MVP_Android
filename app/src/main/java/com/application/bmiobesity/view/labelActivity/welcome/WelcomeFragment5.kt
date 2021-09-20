package com.application.bmiobesity.view.labelActivity.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.LabelWelcomeFragment5Binding

class WelcomeFragment5: Fragment(R.layout.label_welcome_fragment_5) {
    private var welcomeBinding5: LabelWelcomeFragment5Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeBinding5 = LabelWelcomeFragment5Binding.bind(view)
    }

    override fun onDestroyView() {
        welcomeBinding5 = null
        super.onDestroyView()
    }
}