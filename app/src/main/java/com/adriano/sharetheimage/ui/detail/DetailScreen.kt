package com.adriano.sharetheimage.ui.detail

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adriano.sharetheimage.domain.detail.DetailViewModel
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.ui.navigation.LocalNavigationListener
import com.adriano.sharetheimage.ui.navigation.NavEvent
import com.adriano.sharetheimage.ui.shared.modifier.sharedBoundsWithTransitionScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    photoId: String,
    viewModel: DetailViewModel = DetailViewModel.create(photoId),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val onNavigate = LocalNavigationListener.current
    val photo = state.photo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = photo?.userName ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(NavEvent.Back) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (photo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) CircularProgressIndicator()
                else Text("Photo not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {

                DetailPhoto(photo)

                // Info Section
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = photo.description ?: photo.altDescription ?: "No description",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Photographer", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = photo.userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (!photo.userBio.isNullOrBlank()) {
                        Text(
                            text = photo.userBio,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (photo.tags.isNotEmpty()) {
                        Text(text = "Tags", style = MaterialTheme.typography.labelSmall)
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                                8.dp
                            )
                        ) {
                            photo.tags.forEach { tag ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text("#$tag") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailPhoto(
    photo: Photo,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio((photo.width.toFloat() / photo.height.toFloat()).coerceIn(0.5f, 1.5f))
            .sharedBoundsWithTransitionScope(key = photo.id)
            .clip(RectangleShape)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceAtLeast(1f)
                    if (scale == 1f) offset = Offset.Zero else offset += pan
                }
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.urlSmall)
                .crossfade(true)
                .build(),
            contentDescription = photo.description,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}
