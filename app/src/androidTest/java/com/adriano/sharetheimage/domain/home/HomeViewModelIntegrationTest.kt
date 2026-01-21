package com.adriano.sharetheimage.domain.home

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adriano.sharetheimage.data.local.AppDatabase
import com.adriano.sharetheimage.data.local.RoomDatabaseWrapper
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.data.repository.PhotoRepositoryImpl
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var unsplashApi: UnsplashApi
    private lateinit var repository: PhotoRepositoryImpl
    private lateinit var viewModel: HomeViewModel

    private val fakeNetworkMonitor = object : NetworkMonitor {
        override val isOnline: Flow<Boolean> = MutableStateFlow(true)
    }

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        val mockEngine = MockEngine { request ->
            val page = request.url.parameters["page"]?.toInt() ?: 1
            val query = request.url.parameters["query"] ?: ""
            
            // Mock responses based on page number
            val responseContent = when (page) {
                1 -> generateSearchResponse(page, 20, "1", "2") // Page 1 has items 1 and 2
                2 -> generateSearchResponse(page, 20, "3", "4") // Page 2 has items 3 and 4
                else -> generateSearchResponse(page, 20) // Empty or other
            }

            respond(
                content = responseContent,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        unsplashApi = UnsplashApi(httpClient)
        val databaseWrapper = RoomDatabaseWrapper(database)
        
        repository = PhotoRepositoryImpl(
            api = unsplashApi,
            dao = database.photoDao(),
            database = databaseWrapper
        )

        // Setup ViewModel with initial query "Nature" (default)
        // But our test might want to search specifically.
        viewModel = HomeViewModel(
            savedStateHandle = SavedStateHandle().apply { set("Query", "cats") },
            repository = repository,
            networkMonitor = fakeNetworkMonitor
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun endToEndPagingTest() = runTest {
        // The ViewModel uses debounce(600ms), so we might need to advance time or rely on runTest virtual time.
        // However, this is an instrumented test, so `runTest` might not control the main looper perfectly unless using Orchestrator or similar.
        // Since we are collecting the flow, we just wait for emission.
        
        // Paging-Testing's asSnapshot will collect the flow. 
        // We simulate scrolling by requesting more items in the lambda block.
        
        val items = viewModel.photos.asSnapshot {
            // Scroll to the end to trigger the next page load
            scrollTo(index = 2) // We expect 2 items in page 1, so scrolling to index 2 (technically 3rd item, or just accessing it) triggers append.
            // Actually asSnapshot loads until refresh completes. To trigger append we need to scroll.
            // scrollTo(index) requests the item at that index. 
            // If we have 2 items (page 1), and we ask for index 2, it should trigger append if placeholders are enabled or if we are at boundary.
            // Let's scroll to the last item of page 1 to trigger page 2.
            scrollTo(index = 1) 
            
            // Wait for Append to happen? asSnapshot automatically waits for load state to settle? 
            // No, asSnapshot typically returns the list after the block executes and data settles.
            // We might need to scroll further. 
            
            // Let's verify Page 1 first.
        }
        
        // BUT wait, we want to test that scrolling DOES load more.
        // `asSnapshot` allows verification of the *final* state after the block.
        // If we want to verify Page 2 is loaded, we should scroll to an index that forces Page 2 load.
        
        val allItems = viewModel.photos.asSnapshot {
            // Load Page 1
            scrollTo(index = 1) 
            
            // It might take time for Mediator to fetch Page 2.
            // asSnapshot keeps collecting until the block finishes? 
            // The documentation says: "Runs the PagingData flow ... and performs the operations in [block] ... returns the list of data."
            
            // To ensure we trigger append, we need to scroll to the end of currently loaded data.
            // If page 1 has 2 items, indices 0 and 1.
            // Scrolling to 1 might trigger append if prefetch distance is high enough.
            // Default prefetch is usually pageSize (30 in repo). We have 2 items. So yes, it should trigger immediately.
            
            // We probably want to assert we have items from Page 2.
            // Let's assume we want 4 items total (Page 1 + Page 2).
        }

        assertEquals("Should have loaded items from Page 1 and Page 2", 4, allItems.size)
        assertEquals("Item 1 should be first", "1", allItems[0].id)
        assertEquals("Item 3 should be from Page 2", "3", allItems[2].id)
    }

    private fun generateSearchResponse(page: Int, totalPages: Int, vararg ids: String): String {
        val resultsJson = ids.joinToString(",") { id ->
            """
            {
              "id": "$id",
              "width": 100,
              "height": 100,
              "urls": {
                "raw": "raw",
                "full": "full",
                "regular": "regular",
                "small": "small",
                "thumb": "thumb"
              },
              "user": {
                "id": "u$id",
                "username": "user$id",
                "name": "User $id"
              }
            }
            """
        }
        return """
            {
              "total": 100,
              "total_pages": $totalPages,
              "results": [$resultsJson]
            }
        """.trimIndent()
    }
}
