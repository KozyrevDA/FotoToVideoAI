package data.model.billing.rustore

data class ProductPurchaseResultRuStore(
    val orderId: String? = null,
    val purchaseId: String,
    val productId: String,
    val invoiceId: String,
    val purchaseType: PurchaseTypeRuStore,
    val productType: ProductTypeRuStore,
    val quantity: Int,
    val sandbox: Boolean,
)