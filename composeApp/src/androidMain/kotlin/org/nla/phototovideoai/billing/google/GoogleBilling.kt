package org.nla.phototovideoai.billing.google

import BUY_1000_TOKENS_GOOGLE
import BUY_2000_TOKENS_GOOGLE
import SUB_MONTH_GOOGLE
import SUB_YEAR_GOOGLE
import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryPurchasesAsync
import com.google.common.collect.ImmutableList
import data.model.billing.google.PurchaseGoogle
import data.model.billing.google.PurchaseGoogleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nla.phototovideoai.app.AndroidApp

class GoogleBilling {
    private val context = AndroidApp.INSTANCE
    private val consumableProducts = setOf(BUY_1000_TOKENS_GOOGLE, BUY_2000_TOKENS_GOOGLE)
    private var listener: (suspend (PurchaseGoogle) -> Unit)? = null

    private val _productDetailsSetFlow = MutableStateFlow(setOf<ProductDetails>())
    val productDetailsSetFlow: StateFlow<Set<ProductDetails>> = _productDetailsSetFlow.asStateFlow()

    private val _premiumFlow = MutableStateFlow(false)
    val premiumFlow = _premiumFlow.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            CoroutineScope(Dispatchers.IO).launch {
                purchases.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
        }
    }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    init {
        startConnection()
    }

    suspend fun buy(activity: Activity, productId: String) {
        val productDetails = _productDetailsSetFlow.first().find { it.productId == productId }
            ?: return

        launchBilling(
            activity = activity,
            productDetails = productDetails,
            offerToken = productDetails.subscriptionOfferDetails?.first()?.offerToken
        )
    }

    fun setOnConfirmPurchaseListener(block: suspend (PurchaseGoogle) -> Unit) {
        listener = block
    }

    private fun startConnection() {
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        CoroutineScope(Dispatchers.IO).launch {
                            queryProductDetails(
                                SUB_MONTH_GOOGLE,
                                BillingClient.ProductType.SUBS
                            )
                            queryProductDetails(
                                SUB_YEAR_GOOGLE,
                                BillingClient.ProductType.SUBS
                            )
                            queryProductDetails(
                                BUY_1000_TOKENS_GOOGLE,
                                BillingClient.ProductType.INAPP
                            )
                            queryProductDetails(
                                BUY_2000_TOKENS_GOOGLE,
                                BillingClient.ProductType.INAPP
                            )
                            queryPurchasesAsync(BillingClient.ProductType.SUBS)
                            queryPurchasesAsync(BillingClient.ProductType.INAPP)
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                }
            }
        )
    }

    private fun queryProductDetails(productId: String, productType: String) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productType)
                        .build()
                )
            ).build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                queryProductDetailsResult,
            ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetailsSetFlow.update { current ->
                    current.toMutableSet().apply {
                        addAll(queryProductDetailsResult.productDetailsList)
                    }
                }
            }
        }
    }

    private suspend fun queryPurchasesAsync(productType: String) {
        val params = QueryPurchasesParams.newBuilder().setProductType(productType)
        val purchasesResult = billingClient.queryPurchasesAsync(params.build())
        if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            purchasesResult.purchasesList.forEach { purchase -> handlePurchase(purchase) }
        }
    }

    private fun launchBilling(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String? = null,
    ) {
        val productDetailsParamsList = listOf(
            if (offerToken != null) {
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            } else {
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            }
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .setIsOfferPersonalized(false)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        purchase.products.forEach { productId ->
            if (isConsumable(productId)) {
                consumePurchase(purchase)
            } else {
                handleNonConsumablePurchase(purchase)
            }
        }
    }

    private suspend fun handleNonConsumablePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            val ackPurchaseResult =
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())

            if (ackPurchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                listener?.invoke(purchase.toMulti(PurchaseGoogleType.SUBS))
                checkPremium(purchase = purchase, sendEvent = true)
            }
        } else {
            checkPremium(purchase)
        }
    }

    private suspend fun consumePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val consumeResult = withContext(Dispatchers.IO) {
            billingClient.consumePurchase(consumeParams)
        }

        if (consumeResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            listener?.invoke(purchase.toMulti(PurchaseGoogleType.CONSUMABLE))
        }
    }

    private fun checkPremium(
        purchase: Purchase,
        sendEvent: Boolean = false,
    ) {
        purchase.products.forEach { product ->
            when (product) {
                SUB_MONTH_GOOGLE -> {
                    _premiumFlow.value = true
                    /*if (sendEvent) {
                        googleAnalyticsManager.sendEvent(
                            eventName = EVENT_PURCHASED_1_MONTH_GOOGLE
                        )
                    }*/
                }

                SUB_YEAR_GOOGLE -> {
                    _premiumFlow.value = true
                    /*if (sendEvent) {
                        googleAnalyticsManager.sendEvent(
                            eventName = EVENT_PURCHASED_12_MONTHS_GOOGLE
                        )
                    }*/
                }
            }
        }
    }

    private fun isConsumable(productId: String): Boolean {
        return consumableProducts.contains(productId)
    }
}