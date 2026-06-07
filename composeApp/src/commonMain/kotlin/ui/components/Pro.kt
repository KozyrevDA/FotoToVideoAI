package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.ic_crown
import org.jetbrains.compose.resources.vectorResource
import ui.theme.MintDark
import ui.theme.MintGlow
import ui.theme.MintLight
import utils.extensions.tdp

@Composable
fun ProIcon(modifier: Modifier = Modifier.size(110.dp, 50.dp)) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(MintGlow, MintLight, MintDark),
                    center = Offset.Unspecified,
                    radius = 100F
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(18.dp),
                imageVector = vectorResource(Res.drawable.ic_crown),
                contentDescription = null
            )

            Text(
                text = "PRO",
                fontSize = 20.tdp,
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold
            )
        }
    }
}