package utils.managers

import BUY_1000_TOKENS_GOOGLE
import BUY_14000_TOKENS
import BUY_1600_TOKENS
import BUY_2000_TOKENS_GOOGLE
import BUY_2600_TOKENS
import BUY_400_TOKENS
import GoogleBillingProvider
import Language
import MONTH_SUB
import Platform
import RuStoreProvider
import SUB_MONTH_GOOGLE
import SUB_YEAR_GOOGLE
import TOAST_ERROR
import YEAR_SUB
import com.msilimon.vkauthdonate.getAuthVkRepository
import data.model.billing.google.ProductDetailsGoogle
import data.model.billing.revcat.ProductDetails
import data.model.billing.revcat.TypePurchase
import data.model.billing.rustore.PaymentExceptionRuStore
import data.model.billing.rustore.ProductPurchaseParamsRuStore
import data.model.billing.rustore.ProductRuStore
import data.repository.AppRepository
import getLanguage
import getPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import showToast
import utils.billing.revcat.RevCat
import utils.events.Events

class SubscriptionManager(
    private val appRepository: AppRepository,
    private val ruStoreProvider: RuStoreProvider?,
    private val googleBillingProvider: GoogleBillingProvider?,
    private val revCat: RevCat,
) {
    private val authVkRepository = getAuthVkRepository()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _productsRuStore = MutableStateFlow<List<ProductRuStore>>(emptyList())
    val productsRuStore = _productsRuStore.asStateFlow()

    private val _productDetailsSetFlow = MutableStateFlow(setOf<ProductDetailsGoogle>())
    val productDetailsSetFlow = _productDetailsSetFlow.asStateFlow()

    private val _productDetailsRevCatListFlow = MutableStateFlow(listOf<ProductDetails>())
    val productDetailsRevCatListFlow = _productDetailsRevCatListFlow.asStateFlow()

    init {
        fun ruStoreUseCase() {
            coroutineScope.launch {
                ruStoreProvider?.getProducts()?.let {
                    _productsRuStore.value = it
                }
            }
        }

        when (isInstalledFromRuStore()) {
            true -> ruStoreUseCase()

            false, null -> {
                if (getPlatform().name == Platform.Name.ANDROID) {
                    if (getLanguage() == Language.RU) {
                        ruStoreUseCase()
                    } else {
                        googleBillingProvider?.setOnConfirmPurchaseListener { purchaseGoogle ->
                            purchaseGoogle.products.forEach { product ->
                                when (product) {
                                    //TODO добавить новые ивенты
                                    SUB_MONTH_GOOGLE -> Events.put(Events.PURCHASED_SUB_GOOGLE_MONTH)
                                    SUB_YEAR_GOOGLE -> Events.put(Events.PURCHASED_SUB_GOOGLE_YEAR)
                                    BUY_1000_TOKENS_GOOGLE -> Events.put(Events.PURCHASED_1000_TOKENS_GOOGLE)
                                    BUY_2000_TOKENS_GOOGLE -> Events.put(Events.PURCHASED_2000_TOKENS_GOOGLE)
                                }
                            }
                            appRepository.confirmPurchaseGoogle(purchaseGoogle)
                        }
                        coroutineScope.launch {
                            googleBillingProvider?.productDetailsSetFlow?.collect {
                                _productDetailsSetFlow.value = it
                            }
                        }
                    }
                } else {
                    revCat.setOnConfirmPurchaseListener { customerInfo ->
                        customerInfo.entitlements.all.forEach { (id, ent) ->
                            //TODO добавить новые ивенты
                            when (id) {
                                RevCat.SUBSCRIPTION_MONTH -> {
                                    if (ent.isActive) {
                                        Events.put(Events.PURCHASED_SUB_REVCAT_MONTH)
                                    }
                                }

                                RevCat.SUBSCRIPTION_YEAR -> {
                                    if (ent.isActive) {
                                        Events.put(Events.PURCHASED_SUB_REVCAT_YEAR)
                                    }
                                }

                                RevCat.BUY_1000_TOKENS -> {
                                    if (ent.isActive) {
                                        Events.put(Events.PURCHASED_1000_TOKENS_REVCAT)
                                    }
                                }

                                RevCat.BUY_2000_TOKENS -> {
                                    if (ent.isActive) {
                                        Events.put(Events.PURCHASED_2000_TOKENS_REVCAT)
                                    }
                                }
                            }
                        }
                        appRepository.confirmPurchaseRevCat(customerInfo)
                    }

                    coroutineScope.launch {
                        revCat.productDetailsListFlow.collect {
                            _productDetailsRevCatListFlow.value = it
                        }
                    }
                }
            }
        }
    }

    fun onClickSubDiscount(activity: Any?) {
        fun ruStoreUseCase() {
            coroutineScope.launch {
                Events.put(Events.SELECTED_SUB_RUSTORE_MONTH)

                val emailReg = appRepository.getUser()?.emailReg ?: return@launch
                val pair = ruStoreProvider?.purchase(
                    params = ProductPurchaseParamsRuStore(
                        productId = MONTH_SUB,
                        developerPayload = emailReg
                    )
                )
                val result = pair?.first
                val exception = pair?.second

                if (result != null) {
                    Events.put(Events.PURCHASED_SUB_RUSTORE_MONTH)
                } else if (exception != null) {
                    handlePaymentException(exception)
                }
            }
        }

        when (isInstalledFromRuStore()) {
            true -> ruStoreUseCase()

            false, null -> {
                if (getPlatform().name == Platform.Name.ANDROID) {
                    if (getLanguage() == Language.RU) {
                        ruStoreUseCase()
                    } else {
                        activity?.let {
                            coroutineScope.launch {
                                googleBillingProvider?.buy(
                                    activity = it,
                                    productId = SUB_MONTH_GOOGLE
                                )
                            }
                        }
                    }
                } else {
                    coroutineScope.launch {
                        revCat.purchase(duration = TypePurchase.ONE_MONTH)
                    }
                }
            }
        }
    }

    fun onClickSub(activity: Any?) {
        fun ruStoreUseCase() {
            coroutineScope.launch {
                Events.put(Events.SELECTED_SUB_RUSTORE_YEAR)

                val emailReg = appRepository.getUser()?.emailReg ?: return@launch
                val pair = ruStoreProvider?.purchase(
                    params = ProductPurchaseParamsRuStore(
                        productId = YEAR_SUB,
                        developerPayload = emailReg
                    )
                )
                val result = pair?.first
                val exception = pair?.second

                if (result != null) {
                    Events.put(Events.PURCHASED_SUB_RUSTORE_YEAR)
                } else if (exception != null) {
                    handlePaymentException(exception)
                }
            }
        }

        when (isInstalledFromRuStore()) {
            true -> ruStoreUseCase()

            false, null -> {
                if (getPlatform().name == Platform.Name.ANDROID) {
                    if (getLanguage() == Language.RU) {
                        ruStoreUseCase()
                    } else {
                        activity?.let {
                            coroutineScope.launch {
                                googleBillingProvider?.buy(
                                    activity = it,
                                    productId = SUB_YEAR_GOOGLE
                                )
                            }
                        }
                    }
                } else {
                    coroutineScope.launch {
                        revCat.purchase(duration = TypePurchase.ONE_YEAR)
                    }
                }
            }
        }
    }

    fun onClickBuyAnyTokens(activity: Any?, count: Int) {
        fun ruStoreUseCase() {
            coroutineScope.launch {
                when (count) {
                    400 -> Events.put(Events.SELECTED_400_TOKENS_RUSTORE)
                    1600 -> Events.put(Events.SELECTED_1600_TOKENS_RUSTORE)
                    2600 -> Events.put(Events.SELECTED_2600_TOKENS_RUSTORE)
                    5000 -> Events.put(Events.SELECTED_5000_TOKENS_RUSTORE)
                    8000 -> Events.put(Events.SELECTED_8000_TOKENS_RUSTORE)
                    14000 -> Events.put(Events.SELECTED_14000_TOKENS_RUSTORE)
                    20000 -> Events.put(Events.SELECTED_20000_TOKENS_RUSTORE)
                    26000 -> Events.put(Events.SELECTED_26000_TOKENS_RUSTORE)
                }
                val emailReg = appRepository.getUser()?.emailReg ?: return@launch
                val pair = ruStoreProvider?.purchase(
                    params = ProductPurchaseParamsRuStore(
                        productId = when (count) {
                            400 -> BUY_400_TOKENS
                            1600 -> BUY_1600_TOKENS
                            2600 -> BUY_2600_TOKENS
                            14000 -> BUY_14000_TOKENS
                            else -> return@launch
                        },
                        developerPayload = emailReg
                    )
                )
                val result = pair?.first
                val exception = pair?.second

                if (result != null) {
                    when (count) {
                        400 -> Events.put(Events.PURCHASED_400_TOKENS_RUSTORE)
                        1600 -> Events.put(Events.PURCHASED_1600_TOKENS_RUSTORE)
                        2600 -> Events.put(Events.PURCHASED_2600_TOKENS_RUSTORE)
                        5000 -> Events.put(Events.PURCHASED_5000_TOKENS_RUSTORE)
                        8000 -> Events.put(Events.PURCHASED_8000_TOKENS_RUSTORE)
                        14000 -> Events.put(Events.PURCHASED_14000_TOKENS_RUSTORE)
                        20000 -> Events.put(Events.PURCHASED_20000_TOKENS_RUSTORE)
                        26000 -> Events.put(Events.PURCHASED_26000_TOKENS_RUSTORE)
                    }
                } else if (exception != null) {
                    handlePaymentException(exception)
                }
            }
        }

        when (isInstalledFromRuStore()) {
            true -> ruStoreUseCase()

            false, null -> {
                if (getPlatform().name == Platform.Name.ANDROID) {
                    if (getLanguage() == Language.RU) {
                        ruStoreUseCase()
                    } else {
                        val productId = when (count) {
                            1000 -> BUY_1000_TOKENS_GOOGLE
                            2000 -> BUY_2000_TOKENS_GOOGLE
                            else -> null
                        }

                        if (activity != null && productId != null) {
                            coroutineScope.launch {
                                googleBillingProvider?.buy(
                                    activity = activity,
                                    productId = productId
                                )
                            }
                        }
                    }
                } else {
                    coroutineScope.launch {
                        revCat.purchase(duration = TypePurchase.BUY_2000_TOKENS) //TODO добавить новые токены
                    }
                }
            }
        }
    }

    suspend fun isHaveSubPremium(): Boolean {
        val isPremiumRuStore = ruStoreProvider?.isHaveSubPremium() == true
        val isPremiumGoogle = googleBillingProvider?.premiumFlow?.first() == true
        val isPremiumRevCat = revCat.premiumStateFlow.first()
        val isPremiumVkDonut = authVkRepository?.vkSubFlow?.first() == true

        return isPremiumRuStore || isPremiumGoogle || isPremiumRevCat || isPremiumVkDonut
    }

    fun isInstalledFromRuStore(): Boolean? = ruStoreProvider?.isInstalledFromRuStore()

    private suspend fun handlePaymentException(exception: PaymentExceptionRuStore) {
        when (exception) {
            is PaymentExceptionRuStore.RuStorePaymentNetworkException -> {
                withContext(Dispatchers.Main) {
                    showToast(message = TOAST_ERROR)
                }
            }

            else -> {}
        }
    }
}