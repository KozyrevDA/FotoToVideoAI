package utils.billing.revcat

import Platform
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Package
import data.model.billing.revcat.ProductDetails
import data.model.billing.revcat.TypePurchase
import getPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RevCat {
    private var listener: (suspend (CustomerInfo) -> Unit)? = null
    private val availablePackagesFlow = MutableStateFlow(listOf<Package>())

    private val _premiumStateFlow = MutableStateFlow(false)
    val premiumStateFlow = _premiumStateFlow.asStateFlow()

    private val _productDetailsListFlow = MutableStateFlow(listOf<ProductDetails>())
    val productDetailsListFlow = _productDetailsListFlow.asStateFlow()

    init {
        val apiKey = when (getPlatform().name) {
            Platform.Name.ANDROID -> null
            Platform.Name.IOS -> API_KEY_APPLE
        }

        if (apiKey != null) {
            Purchases.configure(PurchasesConfiguration(apiKey = apiKey))
            collectDeviceIdentifiers()
            Purchases.sharedInstance.syncPurchases(
                onSuccess = { customerInfo -> handlePurchase(customerInfo) },
                onError = { _premiumStateFlow.value = false }
            )

            getOfferings()
        }
    }

    suspend fun purchase(duration: TypePurchase) {
        val productId = when (duration) {
            TypePurchase.ONE_MONTH -> SUBSCRIPTION_MONTH
            TypePurchase.ONE_YEAR -> SUBSCRIPTION_YEAR
            TypePurchase.BUY_1000_TOKENS -> BUY_1000_TOKENS
            TypePurchase.BUY_2000_TOKENS -> BUY_2000_TOKENS
        }
        val packageToPurchase = availablePackagesFlow.first()
            .find { it.identifier == productId } ?: return

        Purchases.sharedInstance.purchase(
            packageToPurchase,
            onSuccess = { _, customerInfo ->
                handlePurchase(customerInfo = customerInfo)
            },
            onError = { _, _ -> }
        )
    }

    fun collectDeviceIdentifiers() {
        Purchases.sharedInstance.collectDeviceIdentifiers()
    }

    fun setOnConfirmPurchaseListener(block: suspend (CustomerInfo) -> Unit) {
        listener = block
    }

    private fun getOfferings() {
        Purchases.sharedInstance.getOfferings(
            onSuccess = { offerings ->
                offerings[OFFERINGS]?.availablePackages?.let { packages ->
                    availablePackagesFlow.value = packages
                    getProductDetailsList(packages)
                }
            },
            onError = { }
        )
    }

    private fun getProductDetailsList(availablePackagesList: List<Package>) {
        val resultList = mutableListOf<ProductDetails>()

        availablePackagesList.forEach { packageItem ->
            val duration = when (packageItem.identifier) {
                SUBSCRIPTION_MONTH -> TypePurchase.ONE_MONTH
                SUBSCRIPTION_YEAR -> TypePurchase.ONE_YEAR
                BUY_1000_TOKENS -> TypePurchase.BUY_1000_TOKENS
                BUY_2000_TOKENS -> TypePurchase.BUY_2000_TOKENS
                else -> null
            } ?: return@forEach

            val productDetails = ProductDetails(
                duration = duration,
                productId = packageItem.identifier,
                name = packageItem.storeProduct.title,
                formattedPrice = packageItem.storeProduct.price.formatted,
                amount = packageItem.storeProduct.price.amountMicros / 1_000_000F,
                currencyCode = packageItem.storeProduct.price.currencyCode
            )

            resultList.add(productDetails)
        }

        _productDetailsListFlow.value = resultList
    }

    private fun handlePurchase(customerInfo: CustomerInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            listener?.invoke(customerInfo)
        }

        val entitlements = customerInfo.entitlements.all

        _premiumStateFlow.value = when {
            entitlements.containsKey(SUBSCRIPTION_MONTH) -> {
                val isActive = entitlements[SUBSCRIPTION_MONTH]?.isActive == true
                isActive
            }

            entitlements.containsKey(SUBSCRIPTION_YEAR) -> {
                val isActive = entitlements[SUBSCRIPTION_YEAR]?.isActive == true
                isActive
            }

            else -> false
        }
    }

    companion object {
        private const val API_KEY_APPLE = "appl_CYEQDUTIMjoBZorjwuDBObhFdby"
        private const val OFFERINGS = "premium"
        const val SUBSCRIPTION_MONTH = "1_month_premium_ph"
        const val SUBSCRIPTION_YEAR = "1_year_premium_ph"
        const val BUY_1000_TOKENS = "ph_buy_1000_tokens"
        const val BUY_2000_TOKENS = "ph_buy_2000_tokens"
    }
}