package com.adriano.sharetheimage.domain.home

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import app.cash.turbine.test
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import com.adriano.sharetheimage.photo
import com.adriano.sharetheimage.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `query starts with default value from SavedStateHandle`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val repository = repository()
        val networkMonitor = networkMonitor()

        // When
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Then
        viewModel.query.test {
            assertEquals("Nature", awaitItem())
        }
    }

    @Test
    fun `query flow updates when onQueryChange is called`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val repository = repository()
        val networkMonitor = networkMonitor()
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // When
        viewModel.onQueryChange("Cats")

        // Then
        viewModel.query.test {
            assertEquals("Cats", awaitItem())
        }
    }

    @Test
    fun `photos flow collects from repository based on query`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val repository = repository()
        val networkMonitor = networkMonitor()

        // When
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Then
        viewModel.photos.test {
            val item = awaitItem()
            assertNotNull(item)
        }
    }

    @Test
    fun `photos flow updates when query changes`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val naturePagingData = PagingData.from(listOf(photo()))
        val repository = repository(naturePagingData)
        val dogsPagingData = PagingData.empty<Photo>()
        coEvery { repository.getSearchStream("Dogs") } returns flowOf(dogsPagingData)
        val networkMonitor = networkMonitor()
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        viewModel.photos.test {
            val naturePagingData = awaitItem()

            // When
            viewModel.onQueryChange("Dogs")

            // Then
            assertThat(awaitItem()).isNotEqualTo(naturePagingData)
        }
    }

    @Test
    fun `isOffline is false when network is online`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val networkMonitor = networkMonitor()
        val repository = repository()

        // When
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Then
        viewModel.isOffline.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `isOffline is true when network is offline`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val networkMonitor = networkMonitor(false)
        val repository = repository()

        // When
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Then
        viewModel.isOffline.test {
            assertThat(awaitItem()).isTrue()
        }
    }
}

private fun repository(pagingData: PagingData<Photo> = PagingData.empty()): PhotoRepository {
    val repository: PhotoRepository = mockk()
    every { repository.getSearchStream(any()) } returns flowOf(pagingData)
    return repository
}

private fun networkMonitor(isOnline: Boolean = true): NetworkMonitor {
    val networkMonitor: NetworkMonitor = mockk()
    every { networkMonitor.isOnline } returns flowOf(isOnline)
    return networkMonitor
}
