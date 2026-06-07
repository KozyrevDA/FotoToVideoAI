package ui.screens.trial

import Platform
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import getImagePicker
import getPlatform
import org.koin.compose.viewmodel.koinViewModel
import ui.navigation.AppNavigationActions
import ui.theme.EclipseBlack

@Composable
fun TrialGenerationScreen(
    navigationActions: AppNavigationActions,
    viewModel: TrialGenerationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val selectedPhoto by viewModel.selectedPhoto.collectAsState()
    val showPicker = remember { mutableStateOf(false) }
    val picker = getImagePicker()

    picker?.PickImageFromGallery(
        showPicker = showPicker,
        onPicked = { image -> viewModel.setPhoto(image) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Попробуйте бесплатно",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Загрузите фото и оживите его одной кнопкой",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        PhotoPickerBox(
            photo = selectedPhoto,
            onPickClick = {
                when (getPlatform().name) {
                    Platform.Name.ANDROID -> showPicker.value = true
                    Platform.Name.IOS -> showPicker.value = true
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (state) {
            is TrialGenerationState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Генерация видео...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            is TrialGenerationState.Success -> {
                Text(
                    text = "Видео успешно создано!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { navigationActions.navigateToListScreen(popUpTo = true) }
                ) {
                    Text(
                        text = "Перейти к приложению",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            is TrialGenerationState.Error -> {
                Text(
                    text = (state as TrialGenerationState.Error).message ?: "Ошибка генерации",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { navigationActions.navigateToListScreen(popUpTo = true) }
                ) {
                    Text(
                        text = "Продолжить",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            is TrialGenerationState.Idle -> {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = selectedPhoto != null,
                    onClick = { viewModel.generateTrial() }
                ) {
                    Text(
                        text = "Попробовать",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state !is TrialGenerationState.Loading && state !is TrialGenerationState.Success) {
            TextButton(
                onClick = { navigationActions.navigateToListScreen(popUpTo = true) }
            ) {
                Text(
                    text = "Пропустить",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun PhotoPickerBox(
    photo: ImageBitmap?,
    onPickClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 0.6f)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        EclipseBlack,
                        MaterialTheme.colorScheme.surfaceVariant,
                        EclipseBlack
                    )
                )
            )
            .clickable(onClick = onPickClick),
        contentAlignment = Alignment.Center
    ) {
        if (photo != null) {
            Image(
                bitmap = photo,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = "Нажмите, чтобы добавить фото",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
