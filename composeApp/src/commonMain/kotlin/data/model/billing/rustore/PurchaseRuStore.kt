package data.model.billing.rustore

data class PurchaseRuStore(
    val purchaseId: String,
    val invoiceId: String,
    val orderId: String? = null,
    val purchaseType: PurchaseTypeRuStore,
    val description: String,
    val purchaseTime: String? = null,
    val price: Int,
    val amountLabel: String,
    val currency: String,
    val developerPayload: String? = null,
    val sandbox: Boolean,
)