package com.adriano.sharetheimage.data.remote.mock

import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.data.remote.mock.MockConfig.Mode
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.io.IOException

object KtorMockEngine {

    fun create(
        mode: Mode = Mode.Success,
        networkMonitor: NetworkMonitor,
    ): MockEngine {
        return MockEngine { request ->
            if (!networkMonitor.isOnline.first()) throw IOException("No internet connection")

            return@MockEngine when (mode) {
                Mode.ErrorGeneral -> errorGeneralResponse()
                Mode.ErrorRateLimit -> errorRateLimitResponse()
                Mode.EmptyList -> emptyListResponse()
                else -> { defaultResponse(request) }
            }
        }
    }

    private fun MockRequestHandleScope.defaultResponse(request: HttpRequestData): HttpResponseData {
        val page = request.url.parameters["page"]?.toInt() ?: 1

        val responseContent = when (page) {
            1 -> generateSearchResponse(page, 20, "1", "2")
            2 -> generateSearchResponse(page, 20, "3", "4")
            else -> generateSearchResponse(page, 20)
        }

        return respond(
            content = responseContent,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    private fun MockRequestHandleScope.emptyListResponse(): HttpResponseData = respond(
        content = generateSearchResponse(1, 0),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )

    private fun MockRequestHandleScope.errorRateLimitResponse(): HttpResponseData = respond(
        content = """{"errors": ["Rate Limit Reached"]}""",
        status = HttpStatusCode.Forbidden,
        headers = headersOf(
            "Content-Type" to listOf("application/json"),
            "X-Ratelimit-Remaining" to listOf("0")
        )
    )

    private fun MockRequestHandleScope.errorGeneralResponse(): HttpResponseData = respond(
        content = "",
        status = HttpStatusCode.InternalServerError,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )

    private fun generateSearchResponse(page: Int, totalPages: Int, vararg ids: String): String {
        val resultsJson = ids.joinToString(",") { id ->
            """
            {
              "id": "$id",
              "width": 100,
              "height": 100,
              "color": "#000000",
              "blur_hash": "LEHLk~WB2yk8pyo0adR*.7kCMdnj",
              "description": "Description for $id",
              "urls": {
                "raw": "https://images.unsplash.com/photo-1768930663983-cb33425a7969?q=80&w=2000&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "full": "https://images.unsplash.com/photo-1768930663983-cb33425a7969?q=80&w=1080&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "regular": "https://images.unsplash.com/photo-1768930663983-cb33425a7969?q=80&w=1080&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "small": "https://images.unsplash.com/photo-1768930663983-cb33425a7969?q=80&w=400&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "thumb": "https://images.unsplash.com/photo-1768930663983-cb33425a7969?q=80&w=200&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
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
              "total": 100              "total": 100,
              "total_pages": $totalPages,
              "results": [$resultsJson]
            }
        """.trimIndent()
    }
}

fun fakeUnsplashApi(mode: Mode = Mode.Success, networkMonitor: NetworkMonitor): UnsplashApi {
    val client = HttpClient(KtorMockEngine.create(mode, networkMonitor)) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    return UnsplashApi(client)
}
