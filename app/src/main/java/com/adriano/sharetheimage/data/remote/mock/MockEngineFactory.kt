package com.adriano.sharetheimage.data.remote.mock

import android.content.Context
import com.adriano.sharetheimage.data.remote.dto.SearchResponseDto
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.io.IOException

object MockEngineFactory {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    fun create(context: Context, networkMonitor: NetworkMonitor): MockEngine {
        return MockEngine { _ ->
            if (!networkMonitor.isOnline.first()) {
                throw IOException("No internet connection")
            }
            handleMockRequest(context, MockConfig.mode)
        }
    }

    private fun MockRequestHandleScope.handleMockRequest(
        context: Context,
        mode: MockConfig.Mode
    ): HttpResponseData {
        val contentTypeHeader = "Content-Type" to listOf("application/json")

        return when (mode) {
            MockConfig.Mode.ErrorRateLimit -> {
                val rateLimitHeader = "X-Ratelimit-Remaining" to listOf("0")
                respond(
                    content = """{"errors": ["Rate Limit Reached"]}""",
                    status = HttpStatusCode.Forbidden,
                    headers = headersOf(contentTypeHeader, rateLimitHeader)
                )
            }

            MockConfig.Mode.EmptyList -> {
                respond(
                    content = """{"results": [], "total": 0, "total_pages": 0}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(contentTypeHeader)
                )
            }

            MockConfig.Mode.Success -> {
                respond(
                    content = generateSuccessResponse(context),
                    status = HttpStatusCode.OK,
                    headers = headersOf(contentTypeHeader)
                )
            }

            else -> error("Unsupported or unexpected mock mode in MockEngine: $mode")
        }
    }

    private fun generateSuccessResponse(context: Context): String {
        return try {
            val jsonString =
                context.assets.open("mock_data.json").bufferedReader().use { it.readText() }
            // Load into Kotlin Object
            val responseDto = json.decodeFromString<SearchResponseDto>(jsonString)
            // Re-encode to string for the response (or manipulate the object here if needed)
            json.encodeToString(responseDto)
        } catch (e: Exception) {
            e.printStackTrace()
            """{"results": [], "total": 0, "total_pages": 0}"""
        }
    }
}
