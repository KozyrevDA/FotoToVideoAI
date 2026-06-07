package data.model.billing.rustore

data class ProductRuStore(
    val productId: String,
    val type: ProductTypeRuStore,
    val amountLabel: String,
    val price: Int? = null,
    val currency: String,
    val imageUrl: String,
    val title: String,
    val description: String? = null,
)