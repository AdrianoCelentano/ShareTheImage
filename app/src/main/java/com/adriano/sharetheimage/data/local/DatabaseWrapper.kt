package com.adriano.sharetheimage.data.local

import androidx.room.withTransaction
import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.local.dao.SearchQueryCrossRefDao
import com.adriano.sharetheimage.data.local.dao.SearchQueryRemoteKeyDao

interface DatabaseWrapper {
    fun photoDao(): PhotoDao
    fun searchQueryRemoteKeyDao(): SearchQueryRemoteKeyDao
    fun searchQueryCrossRefDao(): SearchQueryCrossRefDao
    suspend fun <R> withTransaction(block: suspend () -> R): R
}

class RoomDatabaseWrapper(val database: AppDatabase) : DatabaseWrapper {
    override fun photoDao(): PhotoDao = database.photoDao()
    override fun searchQueryRemoteKeyDao(): SearchQueryRemoteKeyDao = database.searchQueryRemoteKeyDao()
    override fun searchQueryCrossRefDao(): SearchQueryCrossRefDao = database.searchQueryCrossRefDao()

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return database.withTransaction(block)
    }
}
