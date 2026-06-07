package ui.screens.launch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.avatar_launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun LaunchScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(.5F)
                .aspectRatio(1F),
            painter = painterResource(Res.drawable.avatar_launch),
            contentDescription = null
        )
    }
}