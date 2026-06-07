package data.model.billing.google

data class ProductDetailsGoogle(
    val productType: String,
    val productId: String,
    val title: String,
    val description: String,
    val formattedPrice: String?,
    val fullPriceMicros: Long?,
    val priceCurrencyCode: String?,
)