package com.adriano.sharetheimage.domain.home

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import app.cash.turbine.test
import com.adriano.sharetheimage.MainDispatcherRule
import com.adriano.sharetheimage.data.fakes.FakeNetworkMonitor
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import com.adriano.sharetheimage.photo
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `query starts with default value from SavedStateHandle`() = runTest {
        // When
        val viewModel = homeViewModel()

        // Then
        viewModel.query.test {
            assertThat("Nature").isEqualTo(awaitItem())
        }
    }

    @Test
    fun `query flow updates when onQueryChange is called`() = runTest {
        // Given
        val viewModel = homeViewModel()

        // When
        viewModel.onQueryChange("Cats")

        // Then
        viewModel.query.test {
            assertThat("Cats").isEqualTo(awaitItem())
        }
    }

    @Test
    fun `photos flow collects from repository based on default query`() = runTest {
        // When
        val viewModel = homeViewModel()

        // Then
        viewModel.photos.test {
            assertThat(awaitItem()).isNotNull
        }
    }

    @Test
    fun `photos flow updates when query changes`() = runTest {
        // Given
        val pagingData = mapOf(
            "Nature" to PagingData.empty(),
            "Dogs" to PagingData.from(listOf(photo()))
        )
        val repository = repository(pagingData)
        val viewModel = homeViewModel(repository)

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
        val networkMonitor = FakeNetworkMonitor(true)

        // When
        val viewModel = homeViewModel(networkMonitor = networkMonitor)

        // Then
        viewModel.isOffline.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `isOffline is true when network is offline`() = runTest {
        // Given
        val networkMonitor = FakeNetworkMonitor(false)

        // When
        val viewModel = homeViewModel(networkMonitor = networkMonitor)

        // Then
        viewModel.isOffline.test {
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun `network change switches isOffline state`() = runTest {
        // Given
        val networkMonitor = FakeNetworkMonitor(true)
        val viewModel = homeViewModel(networkMonitor =  networkMonitor)

        viewModel.isOffline.test {
            val initialValue = awaitItem()

            // When
            networkMonitor.isOnlineFlow.value = false

            // Then
            assertThat(initialValue).isFalse
            assertThat(awaitItem()).isTrue
        }
    }

}

private fun homeViewModel(
    repository: PhotoRepository = repository(),
    networkMonitor: NetworkMonitor = FakeNetworkMonitor()
): HomeViewModel {
    val savedStateHandle = SavedStateHandle()
    return HomeViewModel(savedStateHandle, repository, networkMonitor)
}

private fun repository(pagingData: Map<String, PagingData<Photo>> = mapOf("Nature" to PagingData.empty())): PhotoRepository {
    val repository: PhotoRepository = mockk()
    pagingData.forEach { query, data ->
        every { repository.getSearchStream(query) } returns flowOf(data)
    }
    return repository
}