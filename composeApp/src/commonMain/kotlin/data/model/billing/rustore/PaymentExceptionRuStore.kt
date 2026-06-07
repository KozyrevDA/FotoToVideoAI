package data.model.billing.rustore

sealed class PaymentExceptionRuStore(
    message: String,
    cause: Throwable? = null,
) : Throwable(message, cause) {

    class ApplicationSchemeWasNotProvided : PaymentExceptionRuStore(
        message = "Application scheme was not provided"
    )

    class EmptyPaymentTokenException : PaymentExceptionRuStore(
        message = "Payment token is empty"
    )

    class InvalidCardBindingIdException : PaymentExceptionRuStore(
        message = "Invalid card binding ID"
    )

    class ProductPurchaseCancelled(
        val purchaseId: String? = null,
        val purchaseType: PurchaseTypeRuStore? = null,
        val productType: ProductTypeRuStore? = null,
    ) : PaymentExceptionRuStore(
        message = "Product purchase was cancelled"
    )

    class ProductPurchaseException(
        val orderId: String? = null,
        val purchaseId: String? = null,
        val productId: String? = null,
        val invoiceId: String? = null,
        val quantity: Int? = null,
        val purchaseType: PurchaseTypeRuStore? = null,
        val sandbox: Boolean? = null,
        val productType: ProductTypeRuStore? = null,
        override val cause: Throwable,
    ) : PaymentExceptionRuStore(
        message = "Product purchase exception occurred",
        cause = cause
    )

    class RuStorePayClientAlreadyExist(
        override val message: String,
        override val cause: Throwable? = null,
    ) : PaymentExceptionRuStore(message, cause)

    class RuStorePayClientNotCreated(
        override val message: String,
        override val cause: Throwable? = null,
    ) : PaymentExceptionRuStore(message, cause)

    class RuStorePayInvalidActivePurchase(
        override val message: String = "Invalid active purchase",
        override val cause: Throwable? = null,
    ) : PaymentExceptionRuStore(message, cause)

    class RuStorePayInvalidConsoleAppId(
        override val message: String = "Invalid console app ID",
        override val cause: Throwable? = null,
    ) : PaymentExceptionRuStore(message, cause)

    class RuStorePaySignatureException(
        override val message: String = "Invalid signature",
        override val cause: Throwable? = null,
    ) : PaymentExceptionRuStore(message, cause)

    class RuStorePaymentCommonException(
        override val message: String,
        override val cause: Throwable? = null,
    ) : PaymentExceptionRuStore(message, cause)

    class RuStorePaymentNetworkException(
        val code: String? = null,
        val id: String,
        override val message: String,
        override val cause: Throwable? = null,
    ) : PaymentExceptionRuStore(message, cause)
}