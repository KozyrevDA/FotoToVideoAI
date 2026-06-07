package app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import data.prefs.IMAGE_PICK_PHOTO_1
import data.prefs.IMAGE_PICK_PHOTO_2
import data.prefs.SharedPrefs
import data.repository.AppRepository
import di.Koin.koinConfiguration
import getAppMetricaKMP
import getFirebaseKMP
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import okio.FileSystem
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ui.components.BottomMenu
import ui.navigation.AppNavDestinations
import ui.navigation.AppNavGraph
import ui.navigation.AppNavigationActions
import ui.theme.CosmicAbyss
import ui.theme.PhotoToVideoAiTheme
import ui.theme.ScarletRed
import utils.events.Events
import utils.tokens.TokensManager

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App(
    activity: Any? = null,
    onAuthVK: () -> Unit = {},
) {
    setSingletonImageLoaderFactory { context ->
        getAsyncImageLoader(context)
    }

    openApp()

    KoinContext(context = koinConfiguration()) {
        val appRepository: AppRepository = koinInject()
        val sharedPrefs: SharedPrefs = koinInject()
        val tokensManager: TokensManager = koinInject()
        val navController = rememberNavController()
        val navigationActions = remember(navController) { AppNavigationActions(navController) }
        val routeBottom: MutableState<AppNavDestinations> = remember {
            mutableStateOf(AppNavDestinations.LaunchScreenDestinations)
        }
        val currentRoute = remember { mutableStateOf(routeBottom.value) }
        val showBottomBar = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            navController.currentBackStackEntryFlow.first()
            withFrameNanos {}

            if (routeBottom.value is AppNavDestinations.LaunchScreenDestinations) {
                initRoute(
                    sharedPrefs = sharedPrefs,
                    appRepository = appRepository,
                    navigationActions = navigationActions
                )
            }
        }

        LaunchedEffect(currentRoute.value) {
            routeBottom.value = currentRoute.value

            showBottomBar.value = when (currentRoute.value) {
                AppNavDestinations.HistoryScreenDestinations,
                AppNavDestinations.ListScreenDestinations,
                AppNavDestinations.SettingsScreenDestinations,
                    -> true

                else -> false
            }
        }

        LaunchedEffect(Unit) {
            navController.currentBackStackEntryFlow.first()
            delay(200L)

            val imagePickPhoto1 = sharedPrefs.getPhoto(IMAGE_PICK_PHOTO_1).firstOrNull()
            val imagePickPhoto2 = sharedPrefs.getPhoto(IMAGE_PICK_PHOTO_2).firstOrNull()
            val lastPrompt = sharedPrefs.getLastPrompt()
            val uidLastFilter = sharedPrefs.getUidLastFilter()

            if (
                (imagePickPhoto1 != null || imagePickPhoto2 != null || lastPrompt != null) &&
                !uidLastFilter.isNullOrBlank()
            ) {
                navigationActions.navigateToEditScreen(
                    nameFilter = uidLastFilter,
                    restore = true
                )
                sharedPrefs.putUidLastFilter("")
            }
        }

        LaunchedEffect(tokensManager.logoutState.value) {
            if (tokensManager.logoutState.value) {
                if (sharedPrefs.isPastOnboarding()) {
                    navigationActions.navigateToAuthScreen(popUpTo = true)
                } else {
                    if (appRepository.getShowOnboarding()) {
                        navigationActions.navigateToOnboardingScreen(popUpTo = true)
                    } else {
                        sharedPrefs.setNotFirstOpenApp(true)
                        sharedPrefs.setPastOnboarding(true)
                        navigationActions.navigateToListScreen(popUpTo = true)
                    }
                }
                tokensManager.logoutStateOff()
            }
        }

        PhotoToVideoAiTheme {
            Surface(color = MaterialTheme.colorScheme.background) {
                AppContent(
                    showBottomBar = showBottomBar,
                    navController = navController,
                    navigationActions = navigationActions,
                    routeBottom = routeBottom,
                    currentRoute = currentRoute,
                    activity = activity,
                    onAuthVK = onAuthVK,
                )
            }
        }
    }
}

@Composable
private fun AppContent(
    showBottomBar: MutableState<Boolean>,
    navigationActions: AppNavigationActions,
    navController: NavHostController,
    routeBottom: MutableState<AppNavDestinations>,
    currentRoute: MutableState<AppNavDestinations>,
    activity: Any?,
    onAuthVK: () -> Unit,
) {
    val initialRoute = remember { routeBottom.value }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = CosmicAbyss,
                        actionColor = ScarletRed
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavGraph(
                modifier = Modifier.padding(
                    bottom = with(LocalDensity.current) {
                        paddingValues.calculateBottomPadding()
                    }
                ),
                navController = navController,
                navigationActions = navigationActions,
                startDestination = initialRoute,
                snackbarHostState = snackbarHostState,
                showBottomBar = showBottomBar,
                onCurrentRoute = { currentRoute.value = it },
                activity = activity,
                onAuthVK = onAuthVK,
            )

            if (showBottomBar.value) {
                BottomMenu(
                    navigationActions = navigationActions,
                    currentRoute = currentRoute,
                    modifier = Modifier
                        .padding(32.dp)
                        .align(Alignment.BottomCenter)
                        .height(64.dp)
                )
            }
        }
    }
}

@Composable
private fun openApp() {
    var openApp by remember { mutableStateOf(true) }
    if (openApp) {
        openApp = false
        getFirebaseKMP().initialize()
        getAppMetricaKMP()?.initialize()

        Events.put(Events.APP_OPEN)
    }
}

private suspend fun initRoute(
    sharedPrefs: SharedPrefs,
    appRepository: AppRepository,
    navigationActions: AppNavigationActions,
) {
    if (sharedPrefs.isNotFirstOpenApp()) {
        navigationActions.navigateToListScreen(popUpTo = true)
        if (appRepository.getShowStartPaywall()) {
            navigationActions.navigateToPaywallScreen(prevScreen = "экран_список_фильтров")
        }
    } else if (!sharedPrefs.isPastOnboarding()) {
        if (appRepository.getShowOnboarding()) {
            navigationActions.navigateToOnboardingScreen(popUpTo = true)
        } else {
            sharedPrefs.setNotFirstOpenApp(true)
            sharedPrefs.setPastOnboarding(true)
            navigationActions.navigateToListScreen(popUpTo = true)
            if (appRepository.getShowStartPaywall()) {
                navigationActions.navigateToPaywallScreen(prevScreen = "экран_список_фильтров")
            }
        }
    } else {
        navigationActions.navigateToAuthScreen(popUpTo = true)
    }
}

private fun getAsyncImageLoader(context: PlatformContext) = ImageLoader.Builder(context)
    .memoryCachePolicy(CachePolicy.ENABLED)
    .memoryCache {
        MemoryCache.Builder()
            .maxSizePercent(context, 0.3)
            .strongReferencesEnabled(true)
            .build()
    }
    .diskCachePolicy(CachePolicy.ENABLED)
    .networkCachePolicy(CachePolicy.ENABLED)
    .diskCache { newDiskCache() }
    .crossfade(0)
    .logger(DebugLogger())
    .build()

private fun newDiskCache(): DiskCache {
    return DiskCache.Builder().directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        .maxSizeBytes(1024L * 1024 * 1024) // 512MB
        .build()
}