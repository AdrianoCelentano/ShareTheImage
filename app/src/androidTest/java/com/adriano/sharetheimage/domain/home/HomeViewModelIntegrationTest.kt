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
import com.adriano.sharetheimage.fakes.FakeNetworkMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelIntegrationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun endToEndPagingTest() = runTest {

        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

            val databaseWrapper = RoomDatabaseWrapper(database)
            val networkMonitor = FakeNetworkMonitor()
            val unsplashApi = fakeUnsplashApi(networkMonitor = networkMonitor)

            val repository = PhotoRepositoryImpl(
                api = unsplashApi,
                database = databaseWrapper
            )

            val viewModel = HomeViewModel(
                savedStateHandle = SavedStateHandle().apply { set("Query", "cats") },
                repository = repository,
                networkMonitor = networkMonitor
            )

            // Collect items from the ViewModel's PagingData
            val allItems = viewModel.photos.asSnapshot {
                // Accessing index 2 should trigger loading of page 2
                scrollTo(index = 2)
            }

            // Mock returns 2 pages of 2 items each -> 4 items total
            assertEquals("Should have loaded items from Page 1 and Page 2", 4, allItems.size)

            // Mock data verification (IDs are "1", "2" for page 1; "3", "4" for page 2)
            assertEquals("Item 1 should be first", "1", allItems[0].id)
            assertEquals("Item 2 should be second", "2", allItems[1].id)
            assertEquals("Item 3 should be from Page 2", "3", allItems[2].id)
            assertEquals("Item 4 should be fourth", "4", allItems[3].id)
    }

    @Test
    fun offlineModeTest() = runTest {
        // We don't need a DB for this test really, but ViewModel needs repo
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        try {
             val databaseWrapper = RoomDatabaseWrapper(database)
             val networkMonitor = FakeNetworkMonitor()
             val unsplashApi = fakeUnsplashApi(networkMonitor = networkMonitor)

             val repository = PhotoRepositoryImpl(
                 api = unsplashApi,
                 database = databaseWrapper
             )

            val viewModel = HomeViewModel(
                savedStateHandle = SavedStateHandle().apply { set("Query", "cats") },
                repository = repository,
                networkMonitor = networkMonitor
            )

            // Start online
            networkMonitor.isOnlineFlow.value = true

            val offlineStates = mutableListOf<Boolean>()

            // Collect in background using UnconfinedTestDispatcher for immediate updates
            // Use backgroundScope which is automatically cancelled at end of test
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.isOffline.collect { offlineStates.add(it) }
            }

            // Verify initial state
            // Run current tasks to allow collection to start
            runCurrent()

            assertEquals("Should start online (isOffline=false)", false, offlineStates.last())

            // Go offline
            networkMonitor.isOnlineFlow.value = false // Note: false means offline in Monitor, which maps to true in ViewModel.isOffline

            // Allow propagation
            runCurrent()

            assertEquals("Latest state should be offline", true, offlineStates.last())

        } finally {
            database.close()
        }
    }
}

