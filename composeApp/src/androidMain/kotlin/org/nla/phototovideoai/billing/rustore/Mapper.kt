package org.nla.phototovideoai.billing.rustore

import data.model.billing.rustore.PaymentExceptionRuStore
import data.model.billing.rustore.ProductPurchaseParamsRuStore
import data.model.billing.rustore.ProductPurchaseResultRuStore
import data.model.billing.rustore.ProductRuStore
import data.model.billing.rustore.ProductTypeRuStore
import data.model.billing.rustore.PurchaseRuStore
import data.model.billing.rustore.PurchaseTypeRuStore
import ru.rustore.sdk.pay.model.DeveloperPayload
import ru.rustore.sdk.pay.model.Product
import ru.rustore.sdk.pay.model.ProductId
import ru.rustore.sdk.pay.model.ProductPurchaseParams
import ru.rustore.sdk.pay.model.ProductPurchaseResult
import ru.rustore.sdk.pay.model.ProductType
import ru.rustore.sdk.pay.model.Purchase
import ru.rustore.sdk.pay.model.PurchaseType
import ru.rustore.sdk.pay.model.Quantity
import ru.rustore.sdk.pay.model.RuStorePaymentException

fun Product.toMulti() = ProductRuStore(
    productId = productId.value,
    type = type.toMulti(),
    amountLabel = amountLabel.value,
    price = price?.value,
    currency = currency.value,
    imageUrl = imageUrl.value,
    title = title.value,
    description = description?.value
)

fun ProductType.toMulti() = when (this) {
    ProductType.NON_CONSUMABLE_PRODUCT -> ProductTypeRuStore.NON_CONSUMABLE
    ProductType.CONSUMABLE_PRODUCT -> ProductTypeRuStore.CONSUMABLE
    ProductType.SUBSCRIPTION -> ProductTypeRuStore.SUBSCRIPTION
}

fun PurchaseType.toMulti() = when (this) {
    PurchaseType.ONE_STEP -> PurchaseTypeRuStore.ONE_STEP
    PurchaseType.TWO_STEP -> PurchaseTypeRuStore.TWO_STEP
    PurchaseType.UNDEFINED -> PurchaseTypeRuStore.UNDEFINED
}

fun ProductPurchaseParamsRuStore.toOriginal() = ProductPurchaseParams(
    productId = ProductId(productId),
    quantity = quantity?.let { Quantity(it) },
    developerPayload = DeveloperPayload(developerPayload)
)

fun Purchase.toModel() = PurchaseRuStore(
    purchaseId = purchaseId.value,
    invoiceId = invoiceId.value,
    orderId = orderId?.value,
    purchaseType = purchaseType.toMulti(),
    description = description.value,
    purchaseTime = purchaseTime.toString(),
    price = price.value,
    amountLabel = amountLabel.value,
    currency = currency.value,
    developerPayload = developerPayload?.value,
    sandbox = sandbox
)

fun ProductPurchaseResult.toModel() = ProductPurchaseResultRuStore(
    orderId = orderId?.value,
    purchaseId = purchaseId.value,
    productId = productId.value,
    invoiceId = invoiceId.value,
    purchaseType = purchaseType.toMulti(),
    productType = productType.toMulti(),
    quantity = quantity.value,
    sandbox = sandbox
)

fun RuStorePaymentException.toModel() = when (this) {
    is RuStorePaymentException.ApplicationSchemeWasNotProvided -> PaymentExceptionRuStore.ApplicationSchemeWasNotProvided()
    is RuStorePaymentException.EmptyPaymentTokenException -> PaymentExceptionRuStore.EmptyPaymentTokenException()
    is RuStorePaymentException.InvalidCardBindingIdException -> PaymentExceptionRuStore.InvalidCardBindingIdException()
    is RuStorePaymentException.ProductPurchaseCancelled ->
        PaymentExceptionRuStore.ProductPurchaseCancelled(
            purchaseId?.value,
            purchaseType?.toMulti(),
            productType?.toMulti()
        )

    is RuStorePaymentException.ProductPurchaseException ->
        PaymentExceptionRuStore.ProductPurchaseException(
            orderId = orderId?.value,
            purchaseId = purchaseId?.value,
            productId = productId?.value,
            invoiceId = invoiceId?.value,
            quantity = quantity?.value,
            purchaseType = purchaseType?.toMulti(),
            sandbox = sandbox,
            productType = productType?.toMulti(),
            cause = cause
        )

    is RuStorePaymentException.RuStorePayClientAlreadyExist ->
        PaymentExceptionRuStore.RuStorePayClientAlreadyExist(message, cause)

    is RuStorePaymentException.RuStorePayClientNotCreated ->
        PaymentExceptionRuStore.RuStorePayClientNotCreated(message, cause)

    is RuStorePaymentException.RuStorePayInvalidActivePurchase ->
        PaymentExceptionRuStore.RuStorePayInvalidActivePurchase(message, cause)

    is RuStorePaymentException.RuStorePayInvalidConsoleAppId ->
        PaymentExceptionRuStore.RuStorePayInvalidConsoleAppId(message, cause)

    is RuStorePaymentException.RuStorePaySignatureException ->
        PaymentExceptionRuStore.RuStorePaySignatureException(message, cause)

    is RuStorePaymentException.RuStorePaymentCommonException ->
        PaymentExceptionRuStore.RuStorePaymentCommonException(message, cause)

    is RuStorePaymentException.RuStorePaymentNetworkException ->
        PaymentExceptionRuStore.RuStorePaymentNetworkException(
            code = code,
            id = id,
            message = message,
            cause = cause
        )
}