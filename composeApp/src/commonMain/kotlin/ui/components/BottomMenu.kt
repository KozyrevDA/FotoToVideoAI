package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.history
import phototovideoai.composeapp.generated.resources.ic_menu_history
import phototovideoai.composeapp.generated.resources.ic_menu_home
import phototovideoai.composeapp.generated.resources.ic_menu_settings
import phototovideoai.composeapp.generated.resources.main
import phototovideoai.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.navigation.AppNavDestinations
import ui.navigation.AppNavigationActions
import ui.theme.GraphiteGray
import ui.theme.PhotoToVideoAiTheme
import ui.theme.White
import utils.events.Events
import utils.extensions.tdp

@Composable
fun BottomMenu(
    navigationActions: AppNavigationActions,
    currentRoute: MutableState<AppNavDestinations>,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    val shape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = .1F),
                    shape = shape
                )
                .border(
                    width = .9.dp,
                    brush = Brush.linearGradient(
                        0.0F to Color.White.copy(alpha = 0.1F),
                        0.28F to Color.Transparent,
                        0.4F to Color.Transparent,
                        0.7F to Color.Transparent,
                        0.8F to Color.Transparent,
                        1.0F to Color.White.copy(alpha = 0.1F),
                        start = Offset(0F, 0F),
                        end = Offset.Infinite,
                    ),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomMenuItem(
                    title = stringResource(Res.string.main),
                    icon = vectorResource(Res.drawable.ic_menu_home),
                    enabled = currentRoute.value == AppNavDestinations.ListScreenDestinations,
                    onClick = {
                        if (currentRoute.value != AppNavDestinations.ListScreenDestinations) {
                            navigationActions.navigateToListScreen()
                            Events.put(Events.NAV_TO_LIST_SCREEN)
                        }
                    }
                )
                /*BottomMenuItem(
                    icon = vectorResource(Res.drawable.ic_menu_like),
                    onClick = {}
                )
                BottomMenuItem(
                    icon = vectorResource(Res.drawable.ic_menu_gen),
                    onClick = {}
                )*/
                BottomMenuItem(
                    title = stringResource(Res.string.history),
                    icon = vectorResource(Res.drawable.ic_menu_history),
                    enabled = currentRoute.value == AppNavDestinations.HistoryScreenDestinations,
                    onClick = {
                        if (currentRoute.value != AppNavDestinations.HistoryScreenDestinations) {
                            navigationActions.navigateToHistoryScreen()
                            Events.put(Events.NAV_TO_HISTORY_SCREEN)
                        }
                    }
                )
                BottomMenuItem(
                    title = stringResource(Res.string.settings),
                    icon = vectorResource(Res.drawable.ic_menu_settings),
                    enabled = currentRoute.value == AppNavDestinations.SettingsScreenDestinations,
                    onClick = {
                        if (currentRoute.value != AppNavDestinations.SettingsScreenDestinations) {
                            navigationActions.navigateToSettingsScreen()
                            Events.put(Events.NAV_TO_SETTINGS_SCREEN)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = false,
    modifier: Modifier = Modifier
        .padding(vertical = 4.dp)
        .fillMaxHeight()
        .weight(1F, true),
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = icon,
            tint = if (enabled) White else GraphiteGray,
            contentDescription = null
        )

        Text(
            text = title,
            fontSize = 13.tdp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) {
                MaterialTheme.colorScheme.onBackground
            } else {
                GraphiteGray
            }
        )
    }
}

@Preview
@Composable
private fun BottomMenuPreview() {
    val navController = rememberNavController()
    val navigationActions = remember(navController) { AppNavigationActions(navController) }

    PhotoToVideoAiTheme {
        Surface {
            BottomMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                navigationActions = navigationActions,
                currentRoute = mutableStateOf(AppNavDestinations.ListScreenDestinations)
            )
        }
    }
}