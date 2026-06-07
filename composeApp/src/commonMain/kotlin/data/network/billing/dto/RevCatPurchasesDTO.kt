package data.network.billing.dto

import kotlinx.serialization.Serializable

@Serializable
data class RevCatPurchaseDTO(
    val appUserId: String,
    val productId: String,
    val purchaseDate: Long?,  // ISO8601
    val store: String, // "app_store" / "play_store"
)