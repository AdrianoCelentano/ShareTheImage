package com.adriano.sharetheimage.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adriano.sharetheimage.R
import com.adriano.sharetheimage.domain.home.LimitReachedError
import com.adriano.sharetheimage.domain.home.PhotoListUIStateError
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.ui.navigation.LocalNavigationListener
import com.adriano.sharetheimage.ui.navigation.NavEvent.DetailsNavEntry
import com.adriano.sharetheimage.ui.shared.modifier.sharedBoundsWithTransitionScope
import com.adriano.sharetheimage.ui.shared.modifier.shimmer
import com.wajahatiqbal.blurhash.BlurHashPainter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PhotoItem(photo: Photo) {
    val onNavigate = LocalNavigationListener.current
    Box(
        modifier = Modifier
            .semantics { testTagsAsResourceId = true }
            .testTag("PhotoItem")
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onNavigate(DetailsNavEntry(photo.id)) }
            .sharedBoundsWithTransitionScope(key = photo.id)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.urlSmall)
                .crossfade(true)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565) // less memory
                .build(),
            placeholder = BlurHashPainter(
                blurHash = photo.blurHash,
                width = photo.width,
                height = photo.height,
            ),
            contentDescription = photo.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(ListItemHeight.dp)
        )
    }
}

fun LazyListScope.retryButtonItem(refresh: () -> Unit, photosListError: PhotoListUIStateError?) {
    item {
        val errorMessage = when (photosListError) {
            LimitReachedError -> stringResource(R.string.photos_list_rate_limit_error)
            else -> stringResource(R.string.no_photos_found)
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(errorMessage)
            Spacer(Modifier.height(8.dp))
            Button(onClick = refresh) {
                Text(stringResource(R.string.retry))
                Icon(imageVector = Icons.Default.Replay, contentDescription = stringResource(R.string.refresh_content_description))
            }
        }
    }
}

fun LazyListScope.listLoadingItems() {
    items(4) {
        PhotoItemPlaceHolder()
    }
}

@Composable
fun PhotoItemPlaceHolder() {
    Box(modifier = Modifier.padding(4.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ListItemHeight.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
        )
    }
}

fun LazyListScope.noResultsItem(modifier: Modifier = Modifier) {
    item {
        Column(
            modifier = modifier.fillMaxSize()
                .semantics { testTagsAsResourceId = true }
                .testTag("NoResults"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .height(48.dp)
                    .aspectRatio(1f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.no_photos_found),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private const val ListItemHeight = 300