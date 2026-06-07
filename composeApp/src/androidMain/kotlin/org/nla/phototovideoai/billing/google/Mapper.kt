package org.nla.phototovideoai.billing.google

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import data.model.billing.google.ProductDetailsGoogle
import data.model.billing.google.PurchaseGoogle
import data.model.billing.google.PurchaseGoogleState
import data.model.billing.google.PurchaseGoogleType

fun ProductDetails.toMulti() = ProductDetailsGoogle(
    productType = productType,
    productId = productId,
    title = title,
    description = description,
    formattedPrice = getFormattedPrice(),
    fullPriceMicros = getFullPriceMicros(),
    priceCurrencyCode = getPriceCurrencyCode()
)

fun Int.toMulti() = when (this) {
    1 -> PurchaseGoogleState.PURCHASED
    2 -> PurchaseGoogleState.PENDING
    else -> PurchaseGoogleState.UNSPECIFIED
}

fun Purchase.toMulti(purchaseGoogleType: PurchaseGoogleType) = PurchaseGoogle(
    products = products,
    purchaseToken = purchaseToken,
    purchaseState = purchaseState.toMulti(),
    acknowledged = isAcknowledged,
    autoRenewing = isAutoRenewing,
    orderId = orderId,
    purchaseTime = purchaseTime,
    purchaseGoogleType = purchaseGoogleType
)

private fun ProductDetails.getFormattedPrice(): String? {
    // Для одноразовых покупок
    oneTimePurchaseOfferDetails?.let {
        return it.formattedPrice
    }

    // Для подписок
    subscriptionOfferDetails
        ?.firstOrNull()
        ?.pricingPhases
        ?.pricingPhaseList
        ?.firstOrNull()
        ?.let {
            return it.formattedPrice
        }

    // Если цена не найдена
    return null
}

private fun ProductDetails.getFullPriceMicros(): Long? {
    oneTimePurchaseOfferDetails?.let {
        return it.fullPriceMicros
    }

    subscriptionOfferDetails
        ?.firstOrNull()
        ?.pricingPhases
        ?.pricingPhaseList
        ?.firstOrNull()
        ?.let {
            return it.priceAmountMicros
        }

    return null
}

private fun ProductDetails.getPriceCurrencyCode(): String? {
    oneTimePurchaseOfferDetails?.let {
        return it.priceCurrencyCode
    }

    subscriptionOfferDetails
        ?.firstOrNull()
        ?.pricingPhases
        ?.pricingPhaseList
        ?.firstOrNull()
        ?.let {
            return it.priceCurrencyCode
        }

    return null
}