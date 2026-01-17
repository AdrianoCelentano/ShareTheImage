package com.adriano.sharetheimage.di

import android.content.Context
import com.adriano.sharetheimage.BuildConfig
import com.adriano.sharetheimage.debug.MockConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(@ApplicationContext context: Context): HttpClient {
        val mode = MockConfig.getMode(context)

        val engine = if (mode == MockConfig.Mode.NONE) {
            OkHttp.create()
        } else {
            MockEngine { request ->
                handleMockRequest(mode, request)
            }
        }

        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            defaultRequest {
                url("https://api.unsplash.com/")
                header("Authorization", "Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
            }
        }
    }
}

private fun MockRequestHandleScope.handleMockRequest(mode: MockConfig.Mode, request: HttpRequestData): HttpResponseData {
    val responseHeaders = headersOf("Content-Type" to listOf("application/json"))
    
    return when (mode) {
        MockConfig.Mode.ERROR_403 -> {
            respond(
                content = """{"errors": ["Rate Limit Reached"]}""",
                status = HttpStatusCode.Forbidden,
                headers = responseHeaders
            )
        }
        MockConfig.Mode.EMPTY_LIST -> {
            respond(
                content = """{"results": [], "total": 0, "total_pages": 0}""",
                status = HttpStatusCode.OK,
                headers = responseHeaders
            )
        }
        else -> error("Unsupported mock mode: $mode")
    }
}
