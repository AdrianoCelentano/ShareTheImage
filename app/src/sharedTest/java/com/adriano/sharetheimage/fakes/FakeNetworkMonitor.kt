package com.adriano.sharetheimage.fakes

import com.adriano.sharetheimage.domain.connectivity.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor(initialValue: Boolean = true) : NetworkMonitor {
    val isOnlineFlow = MutableStateFlow(initialValue)
    override val isOnline: Flow<Boolean> = isOnlineFlow
}
