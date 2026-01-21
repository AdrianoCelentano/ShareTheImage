package com.adriano.sharetheimage.data.fakes

import androidx.paging.PagingSource
import com.adriano.sharetheimage.data.local.DatabaseWrapper
import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.local.dao.SearchQueryCrossRefDao
import com.adriano.sharetheimage.data.local.dao.SearchQueryRemoteKeyDao
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.local.entity.SearchQueryCrossRef
import com.adriano.sharetheimage.data.local.entity.SearchQueryRemoteKey

class FakeDatabaseWrapper : DatabaseWrapper {

    val fakePhotoDao = FakePhotoDao()
    val fakeRemoteKeyDao = FakeSearchQueryRemoteKeyDao()
    val fakeCrossRefDao = FakeSearchQueryCrossRefDao()

    override fun photoDao(): PhotoDao = fakePhotoDao
    override fun searchQueryRemoteKeyDao(): SearchQueryRemoteKeyDao = fakeRemoteKeyDao
    override fun searchQueryCrossRefDao(): SearchQueryCrossRefDao = fakeCrossRefDao

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return block()
    }
}

class FakePhotoDao : PhotoDao {
    private val photos = mutableListOf<PhotoEntity>()

    override suspend fun insertAll(photos: List<PhotoEntity>) {
        this.photos.addAll(photos)
    }

    override suspend fun clearAll() {
        photos.clear()
    }

    override suspend fun getPhotoById(id: String): PhotoEntity? {
        return photos.find { it.id == id }
    }

    override suspend fun getAllPhotos(): List<PhotoEntity> {
        return photos
    }

    override fun getPhotosByQuery(query: String): PagingSource<Int, PhotoEntity> {
        throw NotImplementedError("Not implemented for FakePhotoDao in this test scope")
    }
}

class FakeSearchQueryRemoteKeyDao : SearchQueryRemoteKeyDao {
    private val keys = mutableListOf<SearchQueryRemoteKey>()

    override suspend fun insertAll(remoteKeys: List<SearchQueryRemoteKey>) {
        keys.addAll(remoteKeys)
    }

    override suspend fun getRemoteKey(
        query: String,
        photoId: String
    ): SearchQueryRemoteKey? {
        return keys.find { it.searchQuery == query && it.id == photoId }
    }

    override suspend fun clearRemoteKeys(query: String) {
        keys.removeAll { it.searchQuery == query }
    }
}

class FakeSearchQueryCrossRefDao : SearchQueryCrossRefDao {
    private val crossRefs = mutableListOf<SearchQueryCrossRef>()

    override suspend fun insertAll(crossRefs: List<SearchQueryCrossRef>) {
        this.crossRefs.addAll(crossRefs)
    }

    override suspend fun clearCrossRefs(query: String) {
        crossRefs.removeAll { it.searchQuery == query }
    }
}
