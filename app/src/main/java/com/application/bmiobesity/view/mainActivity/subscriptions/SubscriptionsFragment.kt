package com.application.bmiobesity.view.mainActivity.subscriptions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingFlowParams
import com.application.bmiobesity.InTimeApp
import com.application.bmiobesity.R
import com.application.bmiobesity.base.BaseFragment
import com.application.bmiobesity.databinding.MainSubsFragmentBinding
import com.application.bmiobesity.services.google.billing.GoogleBillingClient
import com.application.bmiobesity.services.google.billing.SkuDetailConfig

class SubscriptionsFragment : BaseFragment(R.layout.main_subs_fragment) {
    private var subsBinding: MainSubsFragmentBinding? = null
    private lateinit var googleBillingClient: GoogleBillingClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subsBinding = MainSubsFragmentBinding.bind(view)
        init()
        initRecycler()
    }

    private fun init(){
        googleBillingClient = GoogleBillingClient.getGoogleBilling(InTimeApp.APPLICATION)
    }

    private fun initRecycler(){
        val adapter = SubscriptionsAdapterRecycler(){ subsAction(it) }
        subsBinding?.mainSubsRecycler?.adapter = adapter
        googleBillingClient.skuDetailListLive.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it.values.toList() as MutableList<SkuDetailConfig>)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun subsAction(item: SkuDetailConfig){
        if (item.isPurchased){
            val playstoredeep = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s"
            val url = String.format(playstoredeep, item.sku, "com.application.bmiobesity")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } else {
            val param = BillingFlowParams.newBuilder().setSkuDetails(item.item).build()
            googleBillingClient.launchBillingFlow(requireActivity(), param)
        }
    }

    override fun onDestroyView() {
        subsBinding = null
        super.onDestroyView()
    }
}