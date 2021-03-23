package com.application.bmiobesity.view.mainActivity.home.analyze

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeAnalyzeFragmentBinding

class HomeAnalyzeFragment : Fragment(R.layout.main_home_analyze_fragment) {

    private var analyzeBinding: MainHomeAnalyzeFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyzeBinding = MainHomeAnalyzeFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        analyzeBinding = null
        super.onDestroyView()
    }
}