package com.application.bmiobesity.view.mainActivity.home.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.MainHomeReportFragmentBinding
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSetting
import com.application.bmiobesity.model.db.paramSettings.entities.MedCardParamSimpleValue
import com.application.bmiobesity.viewModels.MainViewModel

class HomeReportFragment : BaseFragment(R.layout.main_home_report_fragment) {

    private var binding: MainHomeReportFragmentBinding? = null
    private val mainModel: MainViewModel by activityViewModels()
    private lateinit var homeReportAdapter: HomeReportAdapterRecycler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainHomeReportFragmentBinding.bind(view)

        initAdapter()
        initListener()
    }

    private fun initAdapter() {
        homeReportAdapter = HomeReportAdapterRecycler(
            requireActivity(),
            mainModel.paramUnit,
            mainModel.medCardParamSetting,
        )
        binding?.reportRecycler?.adapter = homeReportAdapter
    }

    private fun initListener() {
        // Observe change of list of med card param settings
        mainModel.medCard.parametersLive.observe(viewLifecycleOwner, {
            it?.let {
                // Get list of simple values for each parameter
                refreshAdapter(it.values.toList())
            }
        })

        // Swipe down to refresh adapter
        binding?.reportRefreshLayout?.setOnRefreshListener {
            binding?.reportRefreshLayout?.isRefreshing = true
            mainModel.medCard.parametersLive.value?.values?.toList()?.let { refreshAdapter(it) }
            binding?.reportRefreshLayout?.isRefreshing = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(medCards: List<MedCardParamSetting>) {
        val simpleValues: MutableList<List<MedCardParamSimpleValue>> = mutableListOf()
        medCards.forEach {
            simpleValues.add(it.values.toList())
        }
        homeReportAdapter.submitList(simpleValues)
        homeReportAdapter.medCards = medCards
        homeReportAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}