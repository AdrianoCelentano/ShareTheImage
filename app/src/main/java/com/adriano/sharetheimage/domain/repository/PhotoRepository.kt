package com.adriano.sharetheimage.domain.repository

import androidx.paging.PagingData
import com.adriano.sharetheimage.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhoto(id: String): Photo?
    fun getSearchStream(query: String): Flow<PagingData<Photo>>
}
