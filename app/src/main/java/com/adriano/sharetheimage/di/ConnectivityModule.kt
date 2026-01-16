package com.adriano.sharetheimage.di

import com.adriano.sharetheimage.data.connectivity.ConnectivityManagerNetworkMonitor
import com.adriano.sharetheimage.data.connectivity.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ConnectivityModule {
    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}
