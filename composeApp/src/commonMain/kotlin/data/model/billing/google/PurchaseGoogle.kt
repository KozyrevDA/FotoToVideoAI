package data.model.billing.google;

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseGoogle(
    val products: List<String>,
    val purchaseToken: String,
    val purchaseState: PurchaseGoogleState,
    val acknowledged: Boolean,
    val autoRenewing: Boolean = false,
    val orderId: String? = null,
    val purchaseTime: Long? = null,
    val purchaseGoogleType: PurchaseGoogleType,
)

@Serializable
enum class PurchaseGoogleState {
    UNSPECIFIED,
    PURCHASED,
    PENDING;
}