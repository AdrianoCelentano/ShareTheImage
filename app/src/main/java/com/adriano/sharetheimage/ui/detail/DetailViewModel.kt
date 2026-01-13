package com.adriano.sharetheimage.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.usecase.GetPhotoDetailUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailViewModel.Companion.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted private val photoId: String,
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase
) : ViewModel() {

    val uiState: StateFlow<DetailUiState>
        field = MutableStateFlow(DetailUiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val photo = getPhotoDetailUseCase(photoId)
            uiState.update { it.copy(photo = photo) }
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
