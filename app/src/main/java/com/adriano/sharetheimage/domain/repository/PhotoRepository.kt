package com.adriano.sharetheimage.domain.repository

import com.adriano.sharetheimage.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotos(): Flow<List<Photo>>
    fun getPhoto(id: String): Flow<Photo?>
    suspend fun searchPhotos(query: String)
    suspend fun loadMore(query: String, page: Int)
    suspend fun refreshPhotoDetail(id: String)
}
