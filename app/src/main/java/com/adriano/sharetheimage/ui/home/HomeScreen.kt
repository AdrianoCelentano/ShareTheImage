package com.adriano.sharetheimage.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.adriano.sharetheimage.R
import com.adriano.sharetheimage.domain.home.HomeViewModel
import com.adriano.sharetheimage.domain.home.error
import com.adriano.sharetheimage.domain.home.isEmpty
import com.adriano.sharetheimage.domain.home.isLoading
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.ui.shared.composables.PreviewImageLoaderProvider
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val query = viewModel.query.collectAsStateWithLifecycle().value
    val photos = viewModel.photos.collectAsLazyPagingItems()
    val isOffline = viewModel.isOffline.collectAsStateWithLifecycle().value

    HomeScreenContent(
        query = query,
        isOffline = isOffline,
        photos = photos,
        onQueryChange = viewModel::onQueryChange
    )
}

@Composable
fun HomeScreenContent(
    query: String,
    isOffline: Boolean,
    photos: LazyPagingItems<Photo>,
    onQueryChange: (String) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isOffline) OfflineBanner()

            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .semantics { testTagsAsResourceId = true }
                    .testTag("Search")
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text(stringResource(R.string.search)) }
            )

            PhotoList(photos)
        }
    }
}

@Composable
private fun PhotoList(
    photos: LazyPagingItems<Photo>
) {
    val isLoading = remember { derivedStateOf { photos.loadState.isLoading } }.value
    val isEmpty = remember { derivedStateOf { photos.isEmpty } }.value
    val photosListError = remember { derivedStateOf { photos.loadState.error } }.value

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
            if (photo != null) PhotoItem(photo)
            else PhotoItemPlaceHolder()
        }

        when {
            isEmpty -> noResultsItem()
            isLoading -> listLoadingItems()
            photosListError != null -> retryButtonItem(photos::refresh, photosListError)
        }
    }
}

//region previews

@Preview
@Composable
private fun HomeScreenOfflinePreview() {
    val photos = flowOf(
        PagingData.from(emptyList<Photo>())
    ).collectAsLazyPagingItems()

    HomeScreenContent(
        query = "",
        isOffline = true,
        photos = photos,
        onQueryChange = {}
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val photos = flowOf(
        PagingData.from(
            List(10) { index ->
                Photo(
                    id = "$index",
                    description = "Description $index",
                    urlRegular = "https://example.com/${index}_regular.jpg",
                    urlFull = "https://example.com/$index.jpg",
                    urlSmall = "https://example.com/${index}_small.jpg",
                    width = 100,
                    height = 100,
                    userName = "User $index",
                    userBio = "Bio",
                    altDescription = "Alt",
                    tags = listOf("tag1", "tag2"),
                    blurHash = "LEHV6n9F,;t79Fdi%1t7.A%1,;t7"
                )
            }
        )
    ).collectAsLazyPagingItems()

    PreviewImageLoaderProvider {
        HomeScreenContent(
            query = "Nature",
            isOffline = false,
            photos = photos,
            onQueryChange = {}
        )
    }
}

//endregion