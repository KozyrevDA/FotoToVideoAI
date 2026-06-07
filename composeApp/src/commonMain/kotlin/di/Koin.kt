package di

import data.local.Database
import data.network.SpaceXApi
import data.prefs.SharedPrefs
import data.repository.AppRepository
import getDatabaseDriverFactory
import getGoogleBillingProvider
import getRuStoreProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import ui.screens.auth.AuthViewModel
import ui.screens.edit.normal.EditViewModel
import ui.screens.history.HistoryViewModel
import ui.screens.list.ListViewModel
import ui.screens.onboarding.OnboardingViewModel
import ui.screens.paywall.main.PaywallViewModel
import ui.screens.paywall.tokens.TokensViewModel
import ui.screens.request.RequestFuncViewModel
import ui.screens.result.ResultVideoViewModel
import ui.screens.settings.SettingsViewModel
import ui.screens.trial.TrialGenerationViewModel
import utils.billing.revcat.RevCat
import utils.device.uid.DeviceUid
import utils.managers.SubscriptionManager
import utils.tokens.TokensManager

object Koin {
    fun koinConfiguration() = koinApplication {
        modules(appModule())
    }.koin

    private fun appModule() = module {
        single { SharedPrefs() }
        single { Database(getDatabaseDriverFactory()) }
        single { TokensManager(get()) }
        single { DeviceUid(get()) }
        single { SpaceXApi(get(), get()) }
        single { AppRepository(get(), get(), get()) }
        single {
            SubscriptionManager(
                appRepository = get(),
                ruStoreProvider = getRuStoreProvider(),
                googleBillingProvider = getGoogleBillingProvider(),
                revCat = RevCat()
            )
        }

        viewModel { (nameFilter: String, restore: Boolean) ->
            EditViewModel(
                appRepository = get(),
                sharedPrefs = get(),
                nameFilter = nameFilter,
                restore = restore,
                subscriptionManager = get()
            )
        }
        viewModel { OnboardingViewModel(get(), get()) }
        viewModel { AuthViewModel(get(), get()) }
        viewModel { ListViewModel(get(), get()) }
        viewModel { PaywallViewModel(get(), get(), get()) }
        viewModel { HistoryViewModel(get(), get(), get()) }
        viewModel { ResultVideoViewModel(get(), get(), get()) }
        viewModel { SettingsViewModel(get(), get(), get()) }
        viewModel { RequestFuncViewModel() }
        viewModel { TokensViewModel(get(), get(), get()) }
        viewModel { TrialGenerationViewModel(get(), get()) }
    }
}