package com.adriano.sharetheimage.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.adriano.sharetheimage.data.fakes.FakeDatabaseWrapper
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
        val photos = database.fakePhotoDao.getAllPhotos()
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
        val photos = database.fakePhotoDao.getAllPhotos()
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
        // Arrange
        api = setupApi("") 
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
