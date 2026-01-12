package com.adriano.sharetheimage.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.usecase.GetPhotoDetailUseCase
import com.adriano.sharetheimage.domain.usecase.RefreshPhotoDetailUseCase
import com.adriano.sharetheimage.ui.navigation.DetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val refreshPhotoDetailUseCase: RefreshPhotoDetailUseCase
) : ViewModel() {

    private val route = savedStateHandle.toRoute<DetailRoute>()
    private val photoId = route.photoId

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Observe DB
        viewModelScope.launch {
            getPhotoDetailUseCase(photoId).collect { photo ->
                _uiState.update { it.copy(photo = photo) }
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
             val currentPhoto = _uiState.value.photo
             _uiState.update { it.copy(isLoading = currentPhoto == null) }
             try {
                 refreshPhotoDetailUseCase(photoId)
             } catch (e: Exception) {
                 _uiState.update { it.copy(error = e.message) }
             } finally {
                 _uiState.update { it.copy(isLoading = false) }
             }
        }
    }
}

data class DetailUiState(
    val photo: Photo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
