package com.adriano.sharetheimage.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PhotoRepository
) : ViewModel() {

    val query: StateFlow<String> = savedStateHandle.getStateFlow(QueryKey, "Nature")

    @OptIn(ExperimentalCoroutinesApi::class)
    val photos: Flow<PagingData<Photo>> = query.flatMapLatest { query ->
        repository.getSearchStream(query)
    }.cachedIn(viewModelScope)

    fun onQueryChange(newQuery: String) {
        savedStateHandle[QueryKey] = newQuery
    }
}

private const val QueryKey = "Query"
