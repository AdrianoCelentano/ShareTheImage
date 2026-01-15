package com.adriano.sharetheimage.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState.Loading
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adriano.sharetheimage.R
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.ui.shared.modifier.sharedBoundsWithTransitionScope
import com.adriano.sharetheimage.ui.shared.modifier.shimmer

@Composable
fun HomeScreen(
    onPhotoClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val photos = viewModel.photos.collectAsLazyPagingItems()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text(stringResource(R.string.search_label)) }
            )

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = photos.itemCount,
                    key = photos.itemKey { it.id },
                    contentType = photos.itemContentType { "photo" }
                ) { index ->
                    val photo = photos[index]
                    if (photo != null) PhotoItem(photo, onPhotoClick)
                    else PlaceHolder()
                }

                when {
                    photos.loadState.isLoading -> listLoadingItems()
                    photos.loadState.hasError -> retryButtonItem(photos::refresh)
                }
            }
        }
    }
}

@Composable
fun PhotoItem(photo: Photo, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(photo.id) }
            .sharedBoundsWithTransitionScope(key = photo.id)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.urlSmall)
                .crossfade(true)
                .build(),
            contentDescription = photo.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(PhotoListItemHeight.dp)
        )
    }
}

private fun LazyListScope.retryButtonItem(refresh: () -> Unit) {
    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text("There was an error loading more photos")
            Spacer(Modifier.height(8.dp))
            Button(onClick = refresh) {
                Text(stringResource(R.string.retry))
                Icon(imageVector = Icons.Default.Replay, contentDescription = "Refresh")
            }
        }
    }
}

@Composable
fun PlaceHolder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PhotoListItemHeight.dp)
            .clip(RoundedCornerShape(8.dp))
            .shimmer()
    )
}

private fun LazyListScope.listLoadingItems() {
    items(4) {
        Box(modifier = Modifier.padding(4.dp)) {
            PlaceHolder()
        }
    }
}

private val CombinedLoadStates.isLoading: Boolean
    get() = refresh is Loading || append is Loading

private const val PhotoListItemHeight = 200
