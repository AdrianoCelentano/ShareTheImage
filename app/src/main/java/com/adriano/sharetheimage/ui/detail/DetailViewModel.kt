package com.adriano.sharetheimage.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.usecase.GetPhotoDetailUseCase
import com.adriano.sharetheimage.domain.usecase.RefreshPhotoDetailUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailViewModel.Companion.Factory::class)
class DetailViewModel @AssistedInject constructor(
    savedStateHandle: SavedStateHandle,
    @Assisted private val photoId: String,
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val refreshPhotoDetailUseCase: RefreshPhotoDetailUseCase
) : ViewModel() {

    val uiState: StateFlow<DetailUiState>
        field = MutableStateFlow(DetailUiState())

    init {
        // Observe DB
        viewModelScope.launch {
            getPhotoDetailUseCase(photoId).collect { photo ->
                uiState.update { it.copy(photo = photo) }
            }
        }

        // Refresh details
        refreshDetails()
    }

    private fun refreshDetails() {
        viewModelScope.launch {
            // Don't show full loading if we have cached data?
            // Maybe show a small indicator or just update silently.
            // But if we have no data, we should show loading? The DB flow will give null initially maybe?
            // My Repo returns Flow<Photo?>.
            val currentPhoto = uiState.value.photo
            uiState.update { it.copy(isLoading = currentPhoto == null) }
            try {
                refreshPhotoDetailUseCase(photoId)
            } catch (e: Exception) {
                uiState.update { it.copy(error = e.message) }
            } finally {
                uiState.update { it.copy(isLoading = false) }
            }
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

@Immutable
data class DetailUiState(
    val photo: Photo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
