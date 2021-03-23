package com.application.bmiobesity.view.mainActivity.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeFragmentBinding

class HomeFragment : Fragment(R.layout.main_home_fragment) {

    private var homeBinding: MainHomeFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeBinding = MainHomeFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        homeBinding = null
        super.onDestroyView()
    }
}