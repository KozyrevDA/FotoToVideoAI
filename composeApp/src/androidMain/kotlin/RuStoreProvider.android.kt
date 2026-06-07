import data.model.billing.rustore.PaymentExceptionRuStore
import data.model.billing.rustore.ProductPurchaseParamsRuStore
import data.model.billing.rustore.ProductPurchaseResultRuStore
import data.model.billing.rustore.ProductRuStore
import data.model.billing.rustore.PurchaseRuStore
import org.nla.phototovideoai.app.AndroidApp
import org.nla.phototovideoai.billing.rustore.RuStoreBilling
import org.nla.phototovideoai.billing.rustore.toModel
import org.nla.phototovideoai.billing.rustore.toMulti
import org.nla.phototovideoai.billing.rustore.toOriginal
import org.nla.phototovideoai.utils.CheckInstalled

class AndroidRuStoreProvider(private val ruStoreBilling: RuStoreBilling) : RuStoreProvider {
    override suspend fun getProducts(): List<ProductRuStore> {
        return ruStoreBilling.getProducts().map { it.toMulti() }
    }

    override suspend fun isUserAuthorization(): Boolean? {
        return ruStoreBilling.isUserAuthorization()
    }

    override suspend fun isPurchaseAvailability(): Boolean? {
        return ruStoreBilling.isPurchaseAvailability()
    }

    override suspend fun purchase(params: ProductPurchaseParamsRuStore): Pair<ProductPurchaseResultRuStore?, PaymentExceptionRuStore?> {
        val result = ruStoreBilling.purchase(params.toOriginal())
        return Pair(result.first?.toModel(), result.second?.toModel())
    }

    override suspend fun getPurchases(): List<PurchaseRuStore> {
        return ruStoreBilling.getPurchases().map { it.toModel() }
    }

    override suspend fun isHaveSubPremium(): Boolean {
        return ruStoreBilling.isHaveSubPremium()
    }

    override fun isInstalledFromRuStore(): Boolean = CheckInstalled.isInstalledFromRuStore()
}

actual fun getRuStoreProvider(): RuStoreProvider? {
    return AndroidApp.INSTANCE.ruStoreBilling?.let {
        AndroidRuStoreProvider(it)
    }
}