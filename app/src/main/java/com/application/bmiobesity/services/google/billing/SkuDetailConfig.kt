package com.application.bmiobesity.services.google.billing

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.google.gson.Gson
import org.json.JSONObject

data class SkuDetailConfig(
        var item: SkuDetails,
        var sku: String = "",
        var title: String = "",
        var description: String = "",
        var type: String = "",
        var subscriptionPeriod: String = "",
        var freeTrialPeriod: String = "",
        var iconUrl: String = "",
        var price: String = "",
        var priceAmountMicros: Long = 0L,
        var priceCurrencyCode: String = "",
        var originalPrice: String = "",
        var originalPriceAmountMicros: Long = 0L,
        var introductoryPrice: String = "",
        var introductoryPriceAmountMicros: Long = 0L,
        var introductoryPriceCycles: Int = 0,
        var introductoryPricePeriod: String = "",
        var skuDetailsToken: String = "",

        var isPurchased: Boolean = false,
        var purchaseItem: Purchase? = null
){
    fun setUp(item: SkuDetails){
        this.sku = item.sku
        this.title = item.title
        this.description = item.description
        this.type = item.type
        this.subscriptionPeriod = item.subscriptionPeriod
        this.freeTrialPeriod = item.freeTrialPeriod
        this.iconUrl = item.iconUrl
        this.price = item.price
        this.priceAmountMicros = item.priceAmountMicros
        this.priceCurrencyCode = item.priceCurrencyCode
        this.originalPrice = item.originalPrice
        this.originalPriceAmountMicros = item.originalPriceAmountMicros
        this.introductoryPrice = item.introductoryPrice
        this.introductoryPriceAmountMicros = item.introductoryPriceAmountMicros
        this.introductoryPriceCycles = item.introductoryPriceCycles
        this.introductoryPricePeriod = item.introductoryPricePeriod

        val root = JSONObject(item.originalJson)
        this.skuDetailsToken = root.getString("skuDetailsToken")
    }
}