package ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.navOptions

class AppNavigationActions(private val navController: NavHostController) {
    fun back() {
        navController.popBackStack()
    }

    fun navigateToOnboardingScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.OnboardingDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        )
    }

    fun navigateToAuthScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.AuthDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        )
    }

    fun navigateToEditScreen(
        nameFilter: String,
        restore: Boolean = false,
        popUpTo: Boolean = false,
    ) {
        navController.navigate(
            route = AppNavDestinations.EditScreenDestinations(
                nameFilter = nameFilter,
                restore = restore
            ),
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                } else {
                    popUpTo(AppNavDestinations.ListScreenDestinations)
                }
            }
        )
    }

    fun navigateToListScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.ListScreenDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                } else {
                    popUpTo(AppNavDestinations.ListScreenDestinations)
                }
            }
        )
    }

    fun navigateToPaywallScreen(prevScreen: String, popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.PaywallScreenDestinations(prevScreen = prevScreen),
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        )
    }

    fun navigateToHistoryScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.HistoryScreenDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                } else {
                    popUpTo(AppNavDestinations.ListScreenDestinations)
                }
            }
        )
    }

    fun navigateToResultVideoScreen(
        idVideo: String,
        popUpTo: Boolean = false,
    ) {
        navController.navigate(
            route = AppNavDestinations.ResultVideoScreenDestinations(idVideo = idVideo),
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        )
    }

    fun navigateToSettingsScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.SettingsScreenDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                } else {
                    popUpTo(AppNavDestinations.ListScreenDestinations)
                }
            }
        )
    }

    fun navigateToRequestFuncScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.RequestFuncScreenDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        )
    }

    fun navigateToTokensScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.TokensScreenDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        )
    }

    fun navigateToLaunchScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.LaunchScreenDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        )
    }

    fun navigateToTrialGenerationScreen(popUpTo: Boolean = false) {
        navController.navigate(
            route = AppNavDestinations.TrialGenerationDestinations,
            navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                if (popUpTo) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        )
    }
}