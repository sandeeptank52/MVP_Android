package com.application.bmiobesity.view.mainActivity.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainProfileFragmentBinding

class ProfileFragment : Fragment(R.layout.main_profile_fragment) {

    private var profileBinding: MainProfileFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding = MainProfileFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        profileBinding = null
        super.onDestroyView()
    }
}