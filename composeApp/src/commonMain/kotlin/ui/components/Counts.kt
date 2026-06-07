package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.coin_big
import ui.theme.GoldenAmber
import utils.extensions.ResponsiveText
import utils.extensions.tdp

@Composable
fun CoinCounter(
    count: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(30.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(
                color = GoldenAmber.copy(alpha = .3F),
                shape = shape
            )
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(40.dp)
                .padding(horizontal = 10.dp),
            painter = painterResource(Res.drawable.coin_big),
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        if (count != null) {
            ResponsiveText(
                modifier = Modifier
                    .padding(end = 10.dp),
                text = count.toString(),
                color = MaterialTheme.colorScheme.onBackground,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.tdp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}