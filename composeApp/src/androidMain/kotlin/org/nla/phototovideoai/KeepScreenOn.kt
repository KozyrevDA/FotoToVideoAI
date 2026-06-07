package org.nla.phototovideoai

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

private const val FLAG_KEEP_SCREEN_ON = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

@Composable
fun KeepScreenOn() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        var window: Window? = null

        if (context is Activity) {
            window = context.window
            window.addFlags(FLAG_KEEP_SCREEN_ON)
        }

        onDispose {
            window?.clearFlags(FLAG_KEEP_SCREEN_ON)
        }
    }
}