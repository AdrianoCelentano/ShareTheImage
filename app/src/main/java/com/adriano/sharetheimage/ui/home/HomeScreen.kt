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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val photos = viewModel.photos.collectAsLazyPagingItems()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isOffline) OfflineBanner()

            TextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text(stringResource(R.string.search_label)) }
            )

            PhotoList(photos)
        }
    }
}

@Composable
private fun PhotoList(
    photos: LazyPagingItems<Photo>
) {
    val isLoading by remember { derivedStateOf { photos.loadState.isLoading } }
    val isEmpty by remember { derivedStateOf { photos.isEmpty } }
    val photosListError by remember { derivedStateOf { photos.loadState.error } }

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