package ui.screens.onboarding.pages

import VideoPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ui.theme.MintGlow
import utils.extensions.tdp

@Composable
fun VideoPage4(
    onbBackImage: Painter,
    onbImage: Painter,
    title: String,
    desc1: String,
    pageNumber: Int,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val desc1 = when (pageNumber) {
        3 -> {
            buildAnnotatedString {
                val words = desc1.split(" ")
                val firstWords = words.take(5).joinToString(" ")
                val rest = words.drop(5).joinToString(" ")

                withStyle(
                    style = SpanStyle(color = MintGlow)
                ) {
                    append(firstWords)
                }
                append(" ")
                append(rest)
            }
        }

        4 -> {
            buildAnnotatedString {
                val words = desc1.split(" ")
                val firstWords = words.take(3).joinToString(" ")
                val rest = words.drop(3).joinToString(" ")

                withStyle(
                    style = SpanStyle(color = MintGlow)
                ) {
                    append(firstWords)
                }
                append(" ")
                append(rest)
            }
        }

        else -> buildAnnotatedString { append(desc1) }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = onbBackImage,
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopCenter,
            contentDescription = null
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .heightIn(max = 280.dp),
                contentAlignment = Alignment.Center
            ) {
                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1F),
                    templateName = "public_mp4/Waving_a_hand.mp4",
                    isPlayingGlobal = true,
                    onReadyVideo = {}
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(.9F),
                text = title,
                fontSize = 30.tdp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 34.tdp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.fillMaxWidth(.9F),
                text = desc1,
                fontSize = 14.tdp,
                lineHeight = 15.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}