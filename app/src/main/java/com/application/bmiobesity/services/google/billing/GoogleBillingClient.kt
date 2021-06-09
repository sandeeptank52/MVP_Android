package com.application.bmiobesity.services.google.billing

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.android.billingclient.api.*
import com.google.gson.Gson

class GoogleBillingClient private constructor(private val app: Application): LifecycleObserver,
                                                                            PurchasesUpdatedListener,
                                                                            BillingClientStateListener,
                                                                            SkuDetailsResponseListener{
    private lateinit var billingClient: BillingClient
    private val LIST_OF_SKUS = listOf("test_sub")

    private var mSkuDetailList: MutableMap<String, SkuDetailConfig> = mutableMapOf()
    private val mSkuDetailListLive = MutableLiveData<Map<String, SkuDetailConfig>>()
    val skuDetailListLive: LiveData<Map<String, SkuDetailConfig>> = mSkuDetailListLive

    private var mPurchaseList: MutableList<PurchasesConfig> = mutableListOf()
    private val mPurchaseListLive = MutableLiveData<List<PurchasesConfig>>()
    val purchaseListLive: LiveData<List<PurchasesConfig>> = mPurchaseListLive

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create(){
        billingClient = BillingClient.newBuilder(app.applicationContext)
                .setListener(this)
                .enablePendingPurchases()
                .build()

        if (!billingClient.isReady){
            billingClient.startConnection(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy(){
        if (billingClient.isReady){
            billingClient.endConnection()
        }
    }

    private fun querySkuDetails(){
        val params = SkuDetailsParams.newBuilder()
                .setSkusList(LIST_OF_SKUS)
                .setType(BillingClient.SkuType.SUBS)
                .build()
        params.let {
            billingClient.querySkuDetailsAsync(params, this)
        }
    }
    private fun queryPurchases(){
        val result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        result.purchasesList?.let {
            for (purchase in it){
                val p = PurchasesConfig(purchase)
                p.setUp(purchase)
                mPurchaseList.add(p)
                updateSkuConfig(purchase)
            }
            mPurchaseListLive.postValue(mPurchaseList)
        }
    }
    private fun acknowledgePurchase(p: Purchase){
        if (!p.isAcknowledged){
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(p.purchaseToken)
            billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {

            }
        }
    }
    private fun updateSkuConfig(p: Purchase){
        if (p.purchaseState == Purchase.PurchaseState.PURCHASED){
            val skuDetail = mSkuDetailList[p.sku]
            skuDetail?.let {
                it.isPurchased = true
                it.purchaseItem = p
                mSkuDetailList[p.sku] = it
                mSkuDetailListLive.postValue(mSkuDetailList)
            }
        }
    }

    fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int{
        //val sku = params.sku
        //val oldSku = params.oldSku
        if (!billingClient.isReady) {
            Log.e(TAG, "launchBillingFlow: BillingClient is not ready")
        }
        val billingResult = billingClient.launchBillingFlow(activity, params)
        val responseCode = billingResult.responseCode
        //val debugMessage = billingResult.debugMessage
        return responseCode
    }

    // PurchasesUpdatedListener
    override fun onPurchasesUpdated(billingResult: BillingResult, purchasesList: MutableList<Purchase>?) {
        val responseCode = billingResult.responseCode
        //val debugMessage = billingResult.debugMessage
        when (responseCode){
            BillingClient.BillingResponseCode.OK -> {

                if (!purchasesList.isNullOrEmpty()){

                    for (purchase in purchasesList){
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                            acknowledgePurchase(purchase)
                            val p = PurchasesConfig(purchase)
                            p.setUp(purchase)
                            mPurchaseList.add(p)
                            updateSkuConfig(purchase)
                        }
                    }
                    mPurchaseListLive.postValue(mPurchaseList)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {}
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {}
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {}
        }
    }

    // BillingClientStateListener
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        //val debugMessage = billingResult.debugMessage
        if (responseCode == BillingClient.BillingResponseCode.OK){
            querySkuDetails()
            //queryPurchases()
        }
    }
    override fun onBillingServiceDisconnected() {

    }

    // SkuDetailsResponseListener
    override fun onSkuDetailsResponse(billingResult: BillingResult, skuDetails: MutableList<SkuDetails>?) {
        val responseCode = billingResult.responseCode
        //val debugMessage = billingResult.debugMessage

        when(responseCode){
            BillingClient.BillingResponseCode.OK -> {
                //val expectedSkuDetailsCount = LIST_OF_SKUS.size
                if (skuDetails == null){
                    mSkuDetailListLive.postValue(emptyMap())
                } else {
                    val itemsMap = HashMap<String, SkuDetailConfig>().apply {
                        for (details in skuDetails){
                            val skuConfig = SkuDetailConfig(details)
                            skuConfig.setUp(details)
                            put(details.sku, skuConfig)
                        }
                    }
                    this.mSkuDetailList = itemsMap
                    this.mSkuDetailListLive.postValue(itemsMap)
                    queryPurchases()
                }
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
            BillingClient.BillingResponseCode.ERROR -> {
                queryPurchases()
            }
            BillingClient.BillingResponseCode.USER_CANCELED,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                queryPurchases()
            }
        }
    }

    companion object{
        private const val TAG = "InTimeBilling"

        @Volatile
        private var INSTANCE: GoogleBillingClient? = null

        fun getGoogleBilling(app: Application): GoogleBillingClient{
            return INSTANCE ?: synchronized(this){
                val instance = GoogleBillingClient(app)
                INSTANCE = instance
                instance
            }
        }
    }
}