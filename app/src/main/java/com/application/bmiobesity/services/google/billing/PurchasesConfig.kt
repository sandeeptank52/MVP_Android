package com.application.bmiobesity.services.google.billing

import com.android.billingclient.api.Purchase

data class PurchasesConfig(
        var item: Purchase,
        var obfuscatedAccountId: String = "",
        var obfuscatedProfileId: String = "",
        var developerPayload: String = "",
        var isAcknowledged: Boolean = false,
        var isAutoRenewing: Boolean = false,
        var orderId: String = "",
        var packageName: String = "",
        var purchaseState: Int = 0,
        var purchaseTime: Long = 0L,
        var purchaseToken: String = "",
        var signature: String = "",
        var sku: String = ""
){
    fun setUp(item: Purchase){
        this.obfuscatedAccountId = item.accountIdentifiers?.obfuscatedAccountId ?: ""
        this.obfuscatedProfileId = item.accountIdentifiers?.obfuscatedProfileId ?: ""
        this.developerPayload = item.developerPayload
        this.isAcknowledged = item.isAcknowledged
        this.isAutoRenewing = item.isAutoRenewing
        this.orderId = item.orderId
        this.packageName = item.packageName
        this.purchaseState = item.purchaseState
        this.purchaseTime = item.purchaseTime
        this.purchaseToken = item.purchaseToken
        this.signature = item.signature
        this.sku = item.sku
    }
}