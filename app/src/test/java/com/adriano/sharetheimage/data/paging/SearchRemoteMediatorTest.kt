package com.adriano.sharetheimage.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.adriano.sharetheimage.data.fakes.FakeDatabaseWrapper
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.data.remote.mock.KtorMockEngine
import com.adriano.sharetheimage.data.remote.mock.MockConfig
import com.adriano.sharetheimage.data.remote.mock.fakeUnsplashApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediatorTest {

    private lateinit var database: FakeDatabaseWrapper
    private lateinit var api: UnsplashApi
    private val query = "cats"

    @Before
    fun setup() {
        database = FakeDatabaseWrapper()
    }

    private fun setupApi(type: KtorMockEngine.Type = KtorMockEngine.Type.Success): UnsplashApi {
        val mockEngine = KtorMockEngine.create(type, networkMonitor)
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        return UnsplashApi(client)
    }

    @Test
    fun refreshLoad_success_andReturnsEndOfPaginationFalse() = runTest {
        // Arrange
        api = fakeUnsplashApi(mode = MockConfig.Mode.Success, networkMonitor = mockk())
        val mediator = SearchRemoteMediator(query, api, database)
        val pagingState = PagingState<Int, PhotoEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // Act
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Assert
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        
        // Verify DB
        val photos = database.fakePhotoDao.getAllPhotos()
        assertTrue(photos.isNotEmpty())
        assertTrue(photos[0].id == "1")
    }

    @Test
    fun refreshLoad_success_andReturnsEndOfPaginationTrue_whenEmpty() = runTest {
        // Arrange
        api = fakeUnsplashApi(mode = MockConfig.Mode.Success, networkMonitor = mockk())
        val mediator = SearchRemoteMediator(query, api, database)
        val pagingState = PagingState<Int, PhotoEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // Act
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Assert
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        
        // Verify DB
        val photos = database.fakePhotoDao.getAllPhotos()
        assertTrue(photos.isEmpty())
    }

    @Test
    fun refreshLoad_returnsError_whenApiFails() = runTest {
        // Arrange
        api = fakeUnsplashApi(mode = MockConfig.Mode.Success, networkMonitor = mockk())
        val mediator = SearchRemoteMediator(query, api, database)
        val pagingState = PagingState<Int, PhotoEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // Act
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Assert
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun prependLoad_returnsSuccessAndEndOfPagination_whenNoPrevKey() = runTest {
        // Arrange
        api = fakeUnsplashApi(mode = MockConfig.Mode.Success, networkMonitor = mockk())
        val mediator = SearchRemoteMediator(query, api, database)
        val pagingState = PagingState<Int, PhotoEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // Act
        val result = mediator.load(LoadType.PREPEND, pagingState)

        // Assert
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}
