package com.adriano.sharetheimage.domain.detail

import app.cash.turbine.test
import com.adriano.sharetheimage.MainDispatcherRule
import com.adriano.sharetheimage.domain.detail.DetailUiState.Error
import com.adriano.sharetheimage.domain.detail.DetailUiState.Success
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import com.adriano.sharetheimage.photo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: PhotoRepository = mockk()

    @Test
    fun `uiState is Success when repository returns photo`() = runTest {
        // Given
        val photo = photo()
        coEvery { repository.getPhoto(photo.id) } returns photo

        // When
        val viewModel = DetailViewModel(photo.id, repository)

        // Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(Success(photo))
        }
    }

    @Test
    fun `uiState is Error when repository returns null`() = runTest {
        // Given
        val photo = photo()
        coEvery { repository.getPhoto(photo.id) } returns null

        // When
        val viewModel = DetailViewModel(photo.id, repository)

        // Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(Error::class.java)
        }
    }

    @Test
    fun `uiState is Error when repository throws exception`() = runTest {
        // Given
        val photo = photo()
        coEvery { repository.getPhoto(photo.id) } throws IOException("Network error")

        // When
        val viewModel = DetailViewModel(photo.id, repository)

        // Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(Error::class.java)
        }
    }
}