package com.adriano.sharetheimage.domain.detail

import com.adriano.sharetheimage.domain.model.Photo

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val photo: Photo) : DetailUiState
    data class Error(val message: Int) : DetailUiState
}
