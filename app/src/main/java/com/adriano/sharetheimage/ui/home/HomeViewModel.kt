package com.adriano.sharetheimage.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.usecase.GetPhotosUseCase
import com.adriano.sharetheimage.domain.usecase.LoadMorePhotosUseCase
import com.adriano.sharetheimage.domain.usecase.SearchPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchPhotosUseCase: SearchPhotosUseCase,
    private val getPhotosUseCase: GetPhotosUseCase,
    private val loadMorePhotosUseCase: LoadMorePhotosUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState>
        field = MutableStateFlow(HomeUiState())
    private var currentPage = 1

    init {
        // Observe database
        viewModelScope.launch {
            getPhotosUseCase().collect { photos ->
                uiState.update { it.copy(photos = photos) }
            }
        }
        
        // Initial Load (Only if empty? Or always?)
        // Requirement: Offline State -> "If user opens app while in Airplane mode, see results from last successful session"
        // If I trigger "Nature" search every start, it will fail in Airplane mode and might show error.
        // If I check if DB is empty, then search?
        // Or if I just rely on what's in DB?
        // If DB is empty, search Nature.
        // If DB has data, do nothing? (let user refresh or search)
        // I'll add a check.
        viewModelScope.launch {
            // Need a way to check emptiness without collecting flow continuously here.
            // But flow collection is already running.
            // Simplified: Trigger search queries only manually or if explicitly requested.
            // But initial state "Nature" or "Architecture" implies a default seed.
            // I'll try to fetch "Nature" if it's a first run (DB empty).
            // For now, I'll just trigger search "Nature" and catch error.
            // If error, we just show what's in DB (which might be from last session).
            search("Nature", isInitial = true)
        }
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

data class HomeUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "Nature"
)
