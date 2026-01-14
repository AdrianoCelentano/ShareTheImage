package com.adriano.sharetheimage.ui.shared

import androidx.compose.runtime.Composable
import coil.compose.AsyncImage

@Composable
fun RemoteImage(
    model: Any?,
    contentDescription: String?
) {
    AsyncImage(model = model, contentDescription = contentDescription)
}