import android.app.Activity
import data.model.billing.google.ProductDetailsGoogle
import data.model.billing.google.PurchaseGoogle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.nla.phototovideoai.app.AndroidApp
import org.nla.phototovideoai.billing.google.GoogleBilling
import org.nla.phototovideoai.billing.google.toMulti

class AndroidGoogleBillingProvider(
    private val googleBilling: GoogleBilling,
) : GoogleBillingProvider {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    override val productDetailsSetFlow: StateFlow<Set<ProductDetailsGoogle>> =
        googleBilling.productDetailsSetFlow
            .map { set -> set.map { it.toMulti() }.toSet() }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = emptySet()
            )
    override val premiumFlow: StateFlow<Boolean> =
        googleBilling.premiumFlow
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = false
            )

    override suspend fun buy(activity: Any, productId: String) {
        if (activity is Activity) {
            googleBilling.buy(activity = activity, productId = productId)
        }
    }

    override fun setOnConfirmPurchaseListener(block: suspend (PurchaseGoogle) -> Unit) {
        googleBilling.setOnConfirmPurchaseListener(block)
    }
}

actual fun getGoogleBillingProvider(): GoogleBillingProvider? {
    return AndroidApp.INSTANCE.googleBilling?.let {
        AndroidGoogleBillingProvider(it)
    }
}