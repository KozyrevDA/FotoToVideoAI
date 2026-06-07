import data.model.billing.google.ProductDetailsGoogle
import data.model.billing.google.PurchaseGoogle
import kotlinx.coroutines.flow.StateFlow

const val SUB_MONTH_GOOGLE = "av_monthly_subscription"
const val SUB_YEAR_GOOGLE = "av_annual_subscription"
const val BUY_1000_TOKENS_GOOGLE = "1000_tokenov"
const val BUY_2000_TOKENS_GOOGLE = "2000_tokenov"

interface GoogleBillingProvider {
    val productDetailsSetFlow: StateFlow<Set<ProductDetailsGoogle>>
    val premiumFlow: StateFlow<Boolean>

    suspend fun buy(activity: Any, productId: String)
    fun setOnConfirmPurchaseListener(block: suspend (PurchaseGoogle) -> Unit)
}

expect fun getGoogleBillingProvider(): GoogleBillingProvider?