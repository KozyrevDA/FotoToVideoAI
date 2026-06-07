package org.nla.phototovideoai.billing.rustore

import AndroidFirebaseKMP
import BUY_14000_TOKENS
import BUY_1600_TOKENS
import BUY_2600_TOKENS
import BUY_400_TOKENS
import MONTH_SUB
import YEAR_SUB
import android.os.Build
import org.nla.phototovideoai.app.AndroidApp
import ru.rustore.sdk.pay.RuStorePayClient
import ru.rustore.sdk.pay.model.PreferredPurchaseType
import ru.rustore.sdk.pay.model.Product
import ru.rustore.sdk.pay.model.ProductId
import ru.rustore.sdk.pay.model.ProductPurchaseParams
import ru.rustore.sdk.pay.model.ProductPurchaseResult
import ru.rustore.sdk.pay.model.Purchase
import ru.rustore.sdk.pay.model.PurchaseAvailabilityResult
import ru.rustore.sdk.pay.model.RuStorePaymentException
import ru.rustore.sdk.pay.model.SubscriptionPurchase
import ru.rustore.sdk.pay.model.UserAuthorizationStatus
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "RuStoreBilling"

class RuStoreBilling {
    private val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
    private val crashlytics by lazy { AndroidFirebaseKMP(AndroidApp.INSTANCE).crashlytics }

    suspend fun getProducts(): List<Product> {
        return suspendCoroutine { continuation ->
            RuStorePayClient.instance.getProductInteractor()
                .getProducts(
                    productsId = listOf(
                        ProductId(MONTH_SUB),
                        ProductId(YEAR_SUB),
                        ProductId(BUY_400_TOKENS),
                        ProductId(BUY_1600_TOKENS),
                        ProductId(BUY_2600_TOKENS),
                        ProductId(BUY_14000_TOKENS),
                    )
                )
                .addOnSuccessListener { products: List<Product> ->
                    continuation.resume(products)
                }
                .addOnFailureListener {
                    crashlytics.log("[$deviceName] $TAG, getProducts(), addOnFailureListener(), $it")
                    crashlytics.recordException(it)
                    continuation.resume(emptyList())
                }
        }
    }

    suspend fun isUserAuthorization(): Boolean? {
        return suspendCoroutine { continuation ->
            RuStorePayClient.instance.getUserInteractor().getUserAuthorizationStatus()
                .addOnSuccessListener { result ->
                    when (result) {
                        UserAuthorizationStatus.AUTHORIZED -> {
                            continuation.resume(true)
                        }

                        UserAuthorizationStatus.UNAUTHORIZED -> {
                            continuation.resume(false)
                        }
                    }
                }.addOnFailureListener {
                    crashlytics.log("[$deviceName] $TAG, isUserAuthorization(), addOnFailureListener(), $it")
                    crashlytics.recordException(it)
                    continuation.resume(null)
                }
        }
    }

    suspend fun isPurchaseAvailability(): Boolean? {
        return suspendCoroutine { continuation ->
            RuStorePayClient.instance.getPurchaseInteractor().getPurchaseAvailability()
                .addOnSuccessListener { result ->
                    when (result) {
                        is PurchaseAvailabilityResult.Available -> {
                            continuation.resume(true)
                        }

                        is PurchaseAvailabilityResult.Unavailable -> {
                            continuation.resume(false)
                        }
                    }
                }.addOnFailureListener { throwable ->
                    crashlytics.log("[$deviceName] $TAG, isPurchaseAvailability(), addOnFailureListener(), $throwable")
                    crashlytics.recordException(throwable)
                    continuation.resume(null)
                }
        }
    }

    suspend fun purchase(params: ProductPurchaseParams): Pair<ProductPurchaseResult?, RuStorePaymentException?> {
        return suspendCoroutine { continuation ->
            RuStorePayClient.instance.getPurchaseInteractor()
                .purchase(
                    params = params,
                    preferredPurchaseType = PreferredPurchaseType.ONE_STEP
                )
                .addOnSuccessListener { result ->
                    continuation.resume(Pair(result, null))
                }
                .addOnFailureListener { throwable: Throwable ->
                    crashlytics.log("[$deviceName] $TAG, purchase(), addOnFailureListener(), $throwable")
                    crashlytics.recordException(throwable)
                    continuation.resume(Pair(null, throwable as? RuStorePaymentException))
                }
        }
    }

    suspend fun getPurchases(): List<Purchase> {
        return suspendCoroutine { continuation ->
            RuStorePayClient.instance.getPurchaseInteractor().getPurchases()
                .addOnSuccessListener { purchases: List<Purchase> ->
                    continuation.resume(purchases)
                }
                .addOnFailureListener { throwable: Throwable ->
                    crashlytics.log("[$deviceName] $TAG, getPurchases(), addOnFailureListener(), $throwable")
                    crashlytics.recordException(throwable)
                    continuation.resume(emptyList())
                }
        }
    }

    suspend fun isHaveSubPremium(): Boolean {
        val purchases = getPurchases()
        return purchases.any { it is SubscriptionPurchase }
    }
}