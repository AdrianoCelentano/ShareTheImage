package com.adriano.sharetheimage.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.adriano.sharetheimage.data.remote.PagerFactory
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.usecase.GetPhotosUseCase
import com.adriano.sharetheimage.domain.usecase.LoadMorePhotosUseCase
import com.adriano.sharetheimage.domain.usecase.SearchPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchPhotosUseCase: SearchPhotosUseCase,
    private val getPhotosUseCase: GetPhotosUseCase,
    private val loadMorePhotosUseCase: LoadMorePhotosUseCase,
    pagerFactory: PagerFactory
) : ViewModel() {

    val photosPagingDataFlow = pagerFactory.create("Nature").flow.cachedIn(viewModelScope)

    val uiState: StateFlow<HomeUiState>
        field = MutableStateFlow(HomeUiState())
    private var currentPage = 1

    init {
        // Observe database
        getPhotosUseCase()
            .map { it.toPersistentList() }
            .onEach { photos -> uiState.update { it.copy(photos = photos) } }
            .launchIn(viewModelScope)

        search("Nature", isInitial = true)
    }

    fun search(query: String, isInitial: Boolean = false) {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true, searchQuery = query, error = null) }
            try {
                // If this is initial load and we are offline, we might fail.
                // If we fail, we shouldn't wipe the DB (Repository clearAll happens after network success? No, inside searchPhotos).
                // My repo impl: get response -> clear -> insert. So it's safe.
                searchPhotosUseCase(query)
                currentPage = 1
            } catch (e: Exception) {
                if (!isInitial) {
                    uiState.update { it.copy(error = e.localizedMessage ?: "Unknown Error") }
                }
                // If initial component failed (offline), we rely on DB which we are observing.
            } finally {
                uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadMore() {
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            // uiState.update { it.copy(isLoading = true) } // Don't block UI with full loading, maybe separate loadingMore flag
            try {
                val nextPage = currentPage + 1
                loadMorePhotosUseCase(uiState.value.searchQuery, nextPage)
                currentPage = nextPage
            } catch (e: Exception) {
                // Ignore pagination error or show snackbar
            }
        }
    }
}

@Immutable
data class HomeUiState(
    val photos: ImmutableList<Photo> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "Nature"
)
