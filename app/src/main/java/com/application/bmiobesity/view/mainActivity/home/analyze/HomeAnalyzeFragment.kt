package com.application.bmiobesity.view.mainActivity.home.analyze

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeAnalyzeFragmentV2Binding
import com.application.bmiobesity.model.retrofit.ResultDiseaseRisk
import com.application.bmiobesity.viewModels.MainViewModel

class HomeAnalyzeFragment : Fragment(R.layout.main_home_analyze_fragment_v2) {

    private var analyzeBinding: MainHomeAnalyzeFragmentV2Binding? = null
    private val mainModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyzeBinding = MainHomeAnalyzeFragmentV2Binding.bind(view)
        analyzeBinding!!.vm = mainModel
        analyzeBinding!!.lifecycleOwner = this
        initRecycler()
    }

    private fun initRecycler(){
        val analyzeAdapter = HomeAnalyzeAdapterRecycler{onClickRecyclerItem(it)}
        analyzeBinding?.analyzeRecyclerCard?.adapter = analyzeAdapter
        mainModel.resultRiskAnalyze.observe(viewLifecycleOwner, {
            it?.let {
                analyzeAdapter.submitList(it as MutableList<ResultDiseaseRisk>)
                analyzeAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun onClickRecyclerItem(item: ResultDiseaseRisk){
        if (!item.recomendation.isNullOrEmpty()){
            Toast.makeText(requireContext(), item.recomendation, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        analyzeBinding = null
        super.onDestroyView()
    }
}