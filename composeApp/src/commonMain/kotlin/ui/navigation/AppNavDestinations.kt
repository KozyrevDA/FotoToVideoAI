package ui.navigation

import kotlinx.serialization.Serializable

sealed class AppNavDestinations {
    @Serializable
    object OnboardingDestinations : AppNavDestinations()

    @Serializable
    object AuthDestinations : AppNavDestinations()

    @Serializable
    data class EditScreenDestinations(
        val nameFilter: String,
        val restore: Boolean,
    ) : AppNavDestinations()

    @Serializable
    object ListScreenDestinations : AppNavDestinations()

    @Serializable
    data class PaywallScreenDestinations(val prevScreen: String) : AppNavDestinations()

    @Serializable
    object HistoryScreenDestinations : AppNavDestinations()

    @Serializable
    data class ResultVideoScreenDestinations(val idVideo: String) : AppNavDestinations()

    @Serializable
    object SettingsScreenDestinations : AppNavDestinations()

    @Serializable
    object RequestFuncScreenDestinations : AppNavDestinations()

    @Serializable
    object TokensScreenDestinations : AppNavDestinations()

    @Serializable
    object LaunchScreenDestinations : AppNavDestinations()

    @Serializable
    object TrialGenerationDestinations : AppNavDestinations()
}