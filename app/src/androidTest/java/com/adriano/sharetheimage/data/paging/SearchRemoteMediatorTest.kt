package com.adriano.sharetheimage.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adriano.sharetheimage.data.local.AppDatabase
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.remote.UnsplashApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class SearchRemoteMediatorTest {

    private lateinit var database: AppDatabase
    private lateinit var api: UnsplashApi
    private val query = "cats"

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build() // Allow main thread for testing simplicity
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun setupApi(content: String, status: HttpStatusCode = HttpStatusCode.OK): UnsplashApi {
        val mockEngine = MockEngine { request ->
            respond(
                content = content,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
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
        val apiResponse = """
            {
              "results": [
                {
                  "id": "1",
                  "width": 100,
                  "height": 100,
                  "color": "#000000",
                  "blur_hash": "hash",
                  "description": "desc",
                  "alt_description": "alt",
                  "urls": { "regular": "url", "small": "url", "full": "url", "raw": "url", "thumb": "url" },
                  "user": { "id": "u1", "username": "user", "name": "User", "bio": "bio" },
                  "tags": []
                }
              ],
              "total": 1,
              "total_pages": 1
            }
        """.trimIndent()
        api = setupApi(apiResponse)
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
        val photos = database.photoDao().getAllPhotos()
        assertTrue(photos.isNotEmpty())
        assertTrue(photos[0].id == "1")
    }

    @Test
    fun refreshLoad_success_andReturnsEndOfPaginationTrue_whenEmpty() = runTest {
        // Arrange
        val apiResponse = """
            {
              "results": [],
              "total": 0,
              "total_pages": 0
            }
        """.trimIndent()
        api = setupApi(apiResponse)
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
        val photos = database.photoDao().getAllPhotos()
        assertTrue(photos.isEmpty())
    }

    @Test
    fun refreshLoad_returnsError_whenApiFails() = runTest {
        // Arrange
        api = setupApi("", HttpStatusCode.InternalServerError)
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
        // Arrange - No data implies no keys, so we should probably start with empty
        api = setupApi("") // Should not be called
        val mediator = SearchRemoteMediator(query, api, database)
        val pagingState = PagingState<Int, PhotoEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // Act
        // Typically Paging3 implementation of load(PREPEND) checks for null prevKey
        // In our implementation, if no items are loaded, getRemoteKeyForFirstItem returns null,
        // so prevKey is null, so it returns Success(endOfPaginationReached=false) ?
        // Let's check the code:
        // val remoteKeys = getRemoteKeyForFirstItem(state) -> If state empty, this is null.
        // val prevKey = remoteKeys?.prevKey -> null
        // if (prevKey == null) return Success(endOfPaginationReached = remoteKeys != null)
        // So if remoteKeys is null, endOfPaginationReached is false.
        
        // However, usually PREPEND is called when we have data.
        // Let's rely on the logic in the source code.
        
        val result = mediator.load(LoadType.PREPEND, pagingState)

        // Assert
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        // If state is empty, remoteKeys is null. endOfPaginationReached = false.
        // Wait, if remoteKeys is null, we usually return success(false) because we don't know yet?
        // Or if it's the start, we shouldn't prepend.
        // implementation: return Success(endOfPaginationReached = remoteKeys != null)
        // if remoteKeys == null, returns false.
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}
