@file:OptIn(ExperimentalCoroutinesApi::class)

package com.adriano.sharetheimage.domain.home

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adriano.sharetheimage.MainDispatcherRule
import com.adriano.sharetheimage.data.local.AppDatabase
import com.adriano.sharetheimage.data.local.RoomDatabaseWrapper
import com.adriano.sharetheimage.data.remote.mock.fakeUnsplashApi
import com.adriano.sharetheimage.data.repository.PhotoRepositoryImpl
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import com.adriano.sharetheimage.fakes.FakeNetworkMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelIntegrationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun endToEndPagingTest() = runTest {

        // Given
        val viewModel = homeViewModel()

        //When
        val allItems = viewModel.photos.asSnapshot {
            scrollTo(index = 2)
        }

        //Then
        assertThat(allItems).isNotNull
    }

    private fun homeViewModel(
    ): HomeViewModel {
        val networkMonitor = FakeNetworkMonitor()
        val repository = repository(networkMonitor)
        return HomeViewModel(
            savedStateHandle = SavedStateHandle(),
            repository = repository,
            networkMonitor = networkMonitor
        )
    }

    private fun repository(networkMonitor: NetworkMonitor): PhotoRepositoryImpl {
        return PhotoRepositoryImpl(
            api = fakeUnsplashApi(networkMonitor = networkMonitor),
            database = database()
        )
    }

    private fun database(): RoomDatabaseWrapper {
        val roomDb = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        return RoomDatabaseWrapper(roomDb)
    }

}

