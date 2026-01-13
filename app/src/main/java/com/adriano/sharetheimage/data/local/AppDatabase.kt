package com.adriano.sharetheimage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.local.dao.SearchQueryCrossRefDao
import com.adriano.sharetheimage.data.local.dao.SearchQueryRemoteKeyDao
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.local.entity.SearchQueryCrossRef
import com.adriano.sharetheimage.data.local.entity.SearchQueryRemoteKey

@Database(
    entities = [
        PhotoEntity::class,
        SearchQueryRemoteKey::class,
        SearchQueryCrossRef::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun searchQueryRemoteKeyDao(): SearchQueryRemoteKeyDao
    abstract fun searchQueryCrossRefDao(): SearchQueryCrossRefDao
}
