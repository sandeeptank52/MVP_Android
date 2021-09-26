package com.application.bmiobesity.view.mainActivity.home.recommendation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.Purchase
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainHomeRecommendationFragmentBinding
import com.application.bmiobesity.databinding.MainHomeRecommendationFragmentV2Binding
import com.application.bmiobesity.model.retrofit.ResultCommonRecommendation
import com.application.bmiobesity.model.retrofit.ResultRecommendation
import com.application.bmiobesity.services.google.billing.GoogleBillingClient
import com.application.bmiobesity.services.google.billing.PurchasesConfig
import com.application.bmiobesity.viewModels.MainViewModel

class HomeRecommendationFragment : Fragment(R.layout.main_home_recommendation_fragment_v2) {

    private var recBinding: MainHomeRecommendationFragmentV2Binding? = null
    private val mainModel: MainViewModel by activityViewModels()
    private var isExpired: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recBinding = MainHomeRecommendationFragmentV2Binding.bind(view)
        initCommonRecycler()
        initPersonalRecycler()
        initListeners()
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

    private fun initListeners(){
        val purchaseLiveData = mainModel.billingClient.purchaseListLive
        val expireLiveData = mainModel.profileManager.trialPeriodExpired

        val billingObserver = Observer<List<PurchasesConfig>> { purchaseConfigs ->
            purchaseConfigs?.let { purchaseConfigList ->
                val purchasePersonal = purchaseConfigList.find { it.sku == "test_sub" && it.purchaseState == Purchase.PurchaseState.PURCHASED }
                if (purchasePersonal != null){
                    showSubsInfo(false)
                } else {
                    if (isExpired) showSubsInfo(true)
                    else showSubsInfo(false)
                }
            }
        }
        val expireTrialPeriodObserver = Observer<Boolean>{
            it?.let {
                isExpired = it
            }
        }

        purchaseLiveData.removeObserver(billingObserver)
        expireLiveData.removeObserver(expireTrialPeriodObserver)

        expireLiveData.observe(viewLifecycleOwner, expireTrialPeriodObserver)
        purchaseLiveData.observe(viewLifecycleOwner, billingObserver)

        recBinding?.recomPersonalStoreButton?.setOnClickListener {
            findNavController().navigate(R.id.mainNavToSubs)
        }
    }

    private fun showSubsInfo(show: Boolean){
        if (show){
            recBinding?.recomPersonalGroup?.visibility = View.GONE
            recBinding?.recomSubsInfo?.visibility = View.VISIBLE
        } else {
            recBinding?.recomSubsInfo?.visibility = View.GONE
            recBinding?.recomPersonalGroup?.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        recBinding = null
        super.onDestroyView()
    }
}