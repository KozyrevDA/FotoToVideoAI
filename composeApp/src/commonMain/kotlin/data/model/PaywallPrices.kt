package data.model

data class PaywallPrices(
    val monthPair: Pair<String, String>,
    val yearPair: Pair<String, String>,
    val buy1000Tokens: String? = null,
    val buy2000Tokens: String? = null,
)