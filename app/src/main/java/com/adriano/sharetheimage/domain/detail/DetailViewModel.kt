package com.adriano.sharetheimage.domain.detail

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.sharetheimage.R
import com.adriano.sharetheimage.di.IoDispatcher
import com.adriano.sharetheimage.domain.detail.DetailUiState.Error
import com.adriano.sharetheimage.domain.detail.DetailUiState.Loading
import com.adriano.sharetheimage.domain.detail.DetailUiState.Success
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import com.adriano.sharetheimage.ui.shared.runSuspendCatching
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailViewModel.Companion.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted private val photoId: String,
    private val repo: PhotoRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val uiState: StateFlow<DetailUiState>
        field = MutableStateFlow<DetailUiState>(Loading)

    init {
        getPhoto(photoId)
    }

    private fun getPhoto(id: String) {
        viewModelScope.launch(ioDispatcher) {
            uiState.value = Loading
            runSuspendCatching { repo.getPhoto(id) }
                .onSuccess { photo ->
                    if (photo != null) uiState.value = Success(photo)
                    else uiState.value = Error(R.string.detail_loading_error)
                }
                .onFailure { uiState.value = Error(R.string.detail_loading_error) }
        }
    }

    companion object {

        @Composable
        fun create(photoId: String): DetailViewModel {
            return hiltViewModel<DetailViewModel, Factory>(
                creationCallback = { factory -> factory.create(photoId = photoId) }
            )
        }

        @AssistedFactory
        interface Factory {
            fun create(photoId: String): DetailViewModel
        }

    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val photo: Photo) : DetailUiState
    data class Error(val message: Int) : DetailUiState
}
