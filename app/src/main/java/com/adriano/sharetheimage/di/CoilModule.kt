package com.adriano.sharetheimage.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.10) // Aggressive caching
                    .build()
            }
            // Respect Cache-Control headers, but we can force it if needed.
            // For now, default behavior with disk cache should suffice for offline if standard headers are friendly.
            // Or we can add an interceptor to force cache.
            // The prompt says "aggressive DiskCache policy (L4 cache) and MemoryCache (L2 cache) to ensure images are available offline once viewed."
            // Standard config is usually enough if headers permit, but "available offline" usually implies we want to keep them.
            .respectCacheHeaders(false) // Ignore headers to force caching? Or maybe just rely on disk cache.
            // Let's stick to standard but large cache. 
            // "once viewed" -> it will be in cache.
            .build()
    }
}
