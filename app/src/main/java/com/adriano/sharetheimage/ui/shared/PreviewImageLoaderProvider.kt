package com.adriano.sharetheimage.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler

@OptIn(coil3.annotation.ExperimentalCoilApi::class)
@Composable
fun PreviewImageLoaderProvider(content: @Composable () -> Unit) {
    val previewHandler = AsyncImagePreviewHandler {
        android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.Red.toArgb())
        }.asImage()
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        content()
    }
}
