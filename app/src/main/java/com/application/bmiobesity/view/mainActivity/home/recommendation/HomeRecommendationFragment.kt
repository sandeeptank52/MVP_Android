package com.application.bmiobesity.view.mainActivity.home.recommendation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeRecommendationFragmentBinding

class HomeRecommendationFragment : Fragment(R.layout.main_home_recommendation_fragment) {

    private var recBinding: MainHomeRecommendationFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recBinding = MainHomeRecommendationFragmentBinding.bind(view)
    }

    override fun onDestroyView() {
        recBinding = null
        super.onDestroyView()
    }
}