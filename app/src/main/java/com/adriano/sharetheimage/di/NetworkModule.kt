package com.adriano.sharetheimage.di

import com.adriano.sharetheimage.BuildConfig
import com.adriano.sharetheimage.data.remote.installRateLimitHandler
import com.adriano.sharetheimage.data.remote.mock.KtorMockEngine
import com.adriano.sharetheimage.data.remote.mock.MockConfig
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        networkMonitor: NetworkMonitor
    ): HttpClient {
        val engine = engine(networkMonitor)
        return httpClient(engine)
    }

    private fun engine(networkMonitor: NetworkMonitor): HttpClientEngine {
        val startMode = MockConfig.mode
        val engine = if (startMode == MockConfig.Mode.None) OkHttp.create()
        else KtorMockEngine.create(startMode, networkMonitor)
        return engine
    }

    fun httpClient(engine: HttpClientEngine): HttpClient = HttpClient(engine) {
        installRateLimitHandler()
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        defaultRequest {
            url("https://api.unsplash.com/")
            header("Authorization", "Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
        }
    }
}
