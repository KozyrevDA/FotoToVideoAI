package ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import ui.screens.auth.AuthScreen
import ui.screens.edit.normal.EditScreen
import ui.screens.history.HistoryScreen
import ui.screens.launch.LaunchScreen
import ui.screens.list.ListScreen
import ui.screens.onboarding.OnboardingScreen
import ui.screens.paywall.main.PaywallScreen
import ui.screens.paywall.tokens.TokensScreen
import ui.screens.request.RequestFuncScreen
import ui.screens.result.ResultVideoScreen
import ui.screens.settings.SettingsScreen
import ui.screens.trial.TrialGenerationScreen

private const val DURATION_TRANSITION = 150
private const val INIT_ALPHA = .9F
private const val TARGET_ALPHA = 1F

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigationActions: AppNavigationActions,
    startDestination: AppNavDestinations,
    snackbarHostState: SnackbarHostState,
    showBottomBar: MutableState<Boolean>,
    activity: Any?,
    onCurrentRoute: (AppNavDestinations) -> Unit,
    onAuthVK: () -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<AppNavDestinations.OnboardingDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            onCurrentRoute(AppNavDestinations.OnboardingDestinations)
            Surface(color = MaterialTheme.colorScheme.background) {
                OnboardingScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.AuthDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            onCurrentRoute(AppNavDestinations.AuthDestinations)
            Surface(color = MaterialTheme.colorScheme.background) {
                AuthScreen(
                    navigationActions = navigationActions,
                    onAuthVK = onAuthVK
                )
            }
        }

        composable<AppNavDestinations.EditScreenDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            val args = it.toRoute<AppNavDestinations.EditScreenDestinations>()
            onCurrentRoute(
                AppNavDestinations.EditScreenDestinations(
                    nameFilter = args.nameFilter,
                    restore = args.restore
                )
            )
            Surface(color = MaterialTheme.colorScheme.background) {
                EditScreen(
                    navigationActions = navigationActions,
                    nameFilter = args.nameFilter,
                    restore = args.restore,
                    activity = activity
                )
            }
        }

        composable<AppNavDestinations.ListScreenDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            onCurrentRoute(AppNavDestinations.ListScreenDestinations)
            Surface(color = MaterialTheme.colorScheme.background) {
                ListScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.PaywallScreenDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            val args = it.toRoute<AppNavDestinations.PaywallScreenDestinations>()
            onCurrentRoute(AppNavDestinations.PaywallScreenDestinations(prevScreen = args.prevScreen))
            Surface(color = MaterialTheme.colorScheme.background) {
                PaywallScreen(
                    prevScreen = args.prevScreen,
                    navigationActions = navigationActions,
                    activity = activity,
                )
            }
        }

        composable<AppNavDestinations.HistoryScreenDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            onCurrentRoute(AppNavDestinations.HistoryScreenDestinations)
            Surface(color = MaterialTheme.colorScheme.background) {
                HistoryScreen(
                    navigationActions = navigationActions,
                    showBottomBar = showBottomBar
                )
            }
        }

        composable<AppNavDestinations.ResultVideoScreenDestinations> {
            val args = it.toRoute<AppNavDestinations.ResultVideoScreenDestinations>()
            onCurrentRoute(AppNavDestinations.ResultVideoScreenDestinations(idVideo = args.idVideo))
            Surface(color = MaterialTheme.colorScheme.background) {
                ResultVideoScreen(
                    navigationActions = navigationActions,
                    idVideo = args.idVideo
                )
            }
        }

        composable<AppNavDestinations.SettingsScreenDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            onCurrentRoute(AppNavDestinations.SettingsScreenDestinations)
            Surface(color = MaterialTheme.colorScheme.background) {
                SettingsScreen(
                    navigationActions = navigationActions,
                    snackbarHostState = snackbarHostState,
                )
            }
        }

        dialog<AppNavDestinations.RequestFuncScreenDestinations>(
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            onCurrentRoute(AppNavDestinations.RequestFuncScreenDestinations)
            RequestFuncScreen(navigationActions = navigationActions)
        }

        composable<AppNavDestinations.TokensScreenDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            onCurrentRoute(AppNavDestinations.TokensScreenDestinations)
            Surface(color = MaterialTheme.colorScheme.background) {
                TokensScreen(
                    navigationActions = navigationActions,
                    activity = activity
                )
            }
        }

        composable<AppNavDestinations.TrialGenerationDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            onCurrentRoute(AppNavDestinations.TrialGenerationDestinations)
            Surface(color = MaterialTheme.colorScheme.background) {
                TrialGenerationScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.LaunchScreenDestinations>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(DURATION_TRANSITION),
                    initialAlpha = INIT_ALPHA
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(DURATION_TRANSITION),
                    targetAlpha = TARGET_ALPHA
                )
            }
        ) {
            onCurrentRoute(AppNavDestinations.LaunchScreenDestinations)
            Surface(color = MaterialTheme.colorScheme.background) {
                LaunchScreen()
            }
        }
    }
}