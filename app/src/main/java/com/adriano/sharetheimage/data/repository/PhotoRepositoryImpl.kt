package com.adriano.sharetheimage.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.mapper.toDomain
import com.adriano.sharetheimage.data.paging.SearchRemoteMediator
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PhotoRepositoryImpl @Inject constructor(
    private val api: UnsplashApi,
    private val dao: PhotoDao,
    private val database: com.adriano.sharetheimage.data.local.AppDatabase
) : PhotoRepository {

    override fun getPhoto(id: String): Photo? {
        return dao.getPhotoById(id)?.toDomain()
    }

    override fun getSearchStream(query: String): Flow<PagingData<Photo>> {
        val pagingSourceFactory = { dao.getPhotosByQuery(query) }

        return Pager(
            config = androidx.paging.PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = SearchRemoteMediator(
                query = query,
                unsplashApi = api,
                database = database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { photos -> photos.toDomain() }
        }
    }
}

