package com.adriano.sharetheimage.fakes

import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.data.remote.mock.KtorMockEngine
import com.adriano.sharetheimage.data.remote.mock.MockConfig.Mode
import com.adriano.sharetheimage.di.NetworkModule.httpClient
import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun fakeUnsplashApi(
    mode: Mode = Mode.Success,
    networkMonitor: NetworkMonitor? = null
): UnsplashApi {
    val monitor = networkMonitor ?: object : NetworkMonitor {
        override val isOnline: Flow<Boolean> = flowOf(true)
    }
    val engine = KtorMockEngine.create(mode, monitor)
    val client = httpClient(engine)
    return UnsplashApi(client)
}
