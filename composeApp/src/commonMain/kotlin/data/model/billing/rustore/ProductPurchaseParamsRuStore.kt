package data.model.billing.rustore

data class ProductPurchaseParamsRuStore(
    val productId: String,
    val quantity: Int? = null,
    val developerPayload: String,
)