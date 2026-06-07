package data.model.billing.revcat

data class ProductDetails(
    val duration: TypePurchase?,
    val productId: String,
    val name: String,
    val formattedPrice: String,
    val amount: Float,
    val currencyCode: String,
)