package com.adriano.sharetheimage.domain.detail

import com.adriano.sharetheimage.R
import com.adriano.sharetheimage.domain.detail.DetailUiState.Error
import com.adriano.sharetheimage.domain.detail.DetailUiState.Success
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import com.adriano.sharetheimage.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: PhotoRepository = mockk()
    
    private val testPhoto = Photo(
        id = "1",
        description = "Test Description",
        altDescription = null,
        urlRegular = "url_regular",
        urlFull = "url_full",
        urlSmall = "url_small",
        blurHash = "blur_hash",
        userName = "User",
        userBio = "Bio",
        tags = emptyList(),
        width = 100,
        height = 100
    )

    @Test
    fun `uiState is Success when repository returns photo`() = runTest {
        // Arrange
        val photoId = "1"
        coEvery { repository.getPhoto(photoId) } returns testPhoto

        // Act
        val viewModel = DetailViewModel(photoId, repository)

        // Assert
        assertEquals(Success(testPhoto), viewModel.uiState.value)
    }

    @Test
    fun `uiState is Error when repository returns null`() = runTest {
        // Arrange
        val photoId = "1"
        coEvery { repository.getPhoto(photoId) } returns null

        // Act
        val viewModel = DetailViewModel(photoId, repository)

        // Assert
        val expectedError = Error(R.string.detail_loading_error)
        assertEquals(expectedError, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Error when repository throws exception`() = runTest {
        // Arrange
        val photoId = "1"
        coEvery { repository.getPhoto(photoId) } throws IOException("Network error")

        // Act
        val viewModel = DetailViewModel(photoId, repository)

        // Assert
        val expectedError = Error(R.string.detail_loading_error)
        assertEquals(expectedError, viewModel.uiState.value)
    }
}
