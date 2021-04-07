package com.application.bmiobesity.view.mainActivity.home.recommendation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeRecommendationFragmentBinding
import com.application.bmiobesity.model.retrofit.ResultCommonRecommendation
import com.application.bmiobesity.model.retrofit.ResultRecommendation
import com.application.bmiobesity.viewModels.MainViewModel

class HomeRecommendationFragment : Fragment(R.layout.main_home_recommendation_fragment) {

    private var recBinding: MainHomeRecommendationFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recBinding = MainHomeRecommendationFragmentBinding.bind(view)
        initCommonRecycler()
        initPersonalRecycler()
    }

    private fun initCommonRecycler(){
        val adapter = HomeRecomCommonAdapterRecycler { commonClickAction(it) }
        recBinding?.recommendationsCommonRecycler?.adapter = adapter
        mainModel.resultCommonRecommendations.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<ResultCommonRecommendation>)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initPersonalRecycler(){
        val adapter = HomeRecomPersonalAdapterRecycler {personalClickAction(it)}
        recBinding?.recommendationsPersonalRecycler?.adapter = adapter
        mainModel.resultPersonalRecommendations.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<ResultRecommendation>)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun commonClickAction(item: ResultCommonRecommendation){

    }

    private fun personalClickAction(item: ResultRecommendation){

    }

    override fun onDestroyView() {
        recBinding = null
        super.onDestroyView()
    }
}