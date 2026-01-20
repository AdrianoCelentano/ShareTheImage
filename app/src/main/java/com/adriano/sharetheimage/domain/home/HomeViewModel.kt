package com.adriano.sharetheimage.domain.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PhotoRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val query: StateFlow<String> = savedStateHandle.getStateFlow(QueryKey, "Nature")

    val photos: Flow<PagingData<Photo>> = query
        .debounce(600L)
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { query -> repository.getSearchStream(query) }
        .cachedIn(viewModelScope)

    val isOffline: StateFlow<Boolean> = networkMonitor.isOnline
        .map { !it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun onQueryChange(newQuery: String) {
        savedStateHandle[QueryKey] = newQuery
    }
}

private const val QueryKey = "Query"
