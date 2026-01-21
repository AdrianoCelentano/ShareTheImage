package com.adriano.sharetheimage.domain.home

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import app.cash.turbine.test
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import com.adriano.sharetheimage.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: PhotoRepository = mockk()
    private val networkMonitor: NetworkMonitor = mockk()
    
    private val defaultQuery = "Nature"

    @Test
    fun `query starts with default value from SavedStateHandle`() = runTest {
        // Arrange
        val savedStateHandle = SavedStateHandle()
        every { repository.getSearchStream(any()) } returns flowOf(PagingData.empty())
        every { networkMonitor.isOnline } returns flowOf(true)

        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Assert
        assertEquals(defaultQuery, viewModel.query.value)
    }

    @Test
    fun `query flow updates when onQueryChange is called`() = runTest {
        // Arrange
        val savedStateHandle = SavedStateHandle()
        every { repository.getSearchStream(any()) } returns flowOf(PagingData.empty())
        every { networkMonitor.isOnline } returns flowOf(true)
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Act
        viewModel.onQueryChange("Cats")

        // Assert
        viewModel.query.test {
            assertEquals("Cats", awaitItem())
        }
    }

    @Test
    fun `photos flow collects from repository based on query`() = runTest {
        // Arrange
        val savedStateHandle = SavedStateHandle()
        val pagingData = PagingData.empty<Photo>()
        
        // Mock the repository to verify it receives the correct query
        coEvery { repository.getSearchStream("Nature") } returns flowOf(pagingData)
        every { networkMonitor.isOnline } returns flowOf(true)

        // Act
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Assert
        viewModel.photos.test {
            val item = awaitItem()
            assertNotNull(item)
        }
    }

    @Test
    fun `photos flow updates when query changes`() = runTest {
        // Arrange
        val savedStateHandle = SavedStateHandle()
        val pagingData1 = PagingData.empty<Photo>()
        val pagingData2 = PagingData.empty<Photo>()

        coEvery { repository.getSearchStream("Nature") } returns flowOf(pagingData1)
        coEvery { repository.getSearchStream("Dogs") } returns flowOf(pagingData2)
        every { networkMonitor.isOnline } returns flowOf(true)

        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Act & Assert
        viewModel.photos.test {
            // First emission for default query "Nature"
            assertNotNull(awaitItem())

            // Change query
            viewModel.onQueryChange("Dogs")
            
            // Second emission for "Dogs"
            assertNotNull(awaitItem())
        }
    }

    @Test
    fun `isOffline is false when network is online`() = runTest {
        // Arrange
        val savedStateHandle = SavedStateHandle()
        every { networkMonitor.isOnline } returns flowOf(true)
        every { repository.getSearchStream(any()) } returns flowOf(PagingData.empty())
        
        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Assert
        viewModel.isOffline.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `isOffline is true when network is offline`() = runTest {
        // Arrange
        val savedStateHandle = SavedStateHandle()
        every { networkMonitor.isOnline } returns flowOf(false)
        every { repository.getSearchStream(any()) } returns flowOf(PagingData.empty())

        val viewModel = HomeViewModel(savedStateHandle, repository, networkMonitor)

        // Assert
        viewModel.isOffline.test {
            assertTrue(awaitItem())
        }
    }
}
