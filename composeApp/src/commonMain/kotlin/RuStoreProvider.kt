import data.model.billing.rustore.PaymentExceptionRuStore
import data.model.billing.rustore.ProductPurchaseParamsRuStore
import data.model.billing.rustore.ProductPurchaseResultRuStore
import data.model.billing.rustore.ProductRuStore
import data.model.billing.rustore.PurchaseRuStore

const val MONTH_SUB = "FV_monthly_subscription"
const val YEAR_SUB = "FV_annual_subscription"
const val BUY_400_TOKENS = "400_Tokenov"
const val BUY_1600_TOKENS = "1600_Tokenov"
const val BUY_2600_TOKENS = "2600_Tokenov"
const val BUY_14000_TOKENS = "14000_Tokenov"
const val TOAST_ERROR = "Ошибка RuStore. Попробуйте отключить VPN"

interface RuStoreProvider {
    suspend fun getProducts(): List<ProductRuStore>
    suspend fun isUserAuthorization(): Boolean?
    suspend fun isPurchaseAvailability(): Boolean?
    suspend fun purchase(params: ProductPurchaseParamsRuStore): Pair<ProductPurchaseResultRuStore?, PaymentExceptionRuStore?>
    suspend fun getPurchases(): List<PurchaseRuStore>
    suspend fun isHaveSubPremium(): Boolean
    fun isInstalledFromRuStore(): Boolean
}

expect fun getRuStoreProvider(): RuStoreProvider?