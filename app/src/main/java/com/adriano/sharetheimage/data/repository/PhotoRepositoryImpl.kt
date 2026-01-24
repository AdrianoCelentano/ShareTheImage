package com.adriano.sharetheimage.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.adriano.sharetheimage.data.local.DatabaseWrapper
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
    private val database: DatabaseWrapper
) : PhotoRepository {

    override suspend fun getPhoto(id: String): Photo? {
        return database.photoDao().getPhotoById(id)?.toDomain()
    }

    override fun getSearchStream(query: String): Flow<PagingData<Photo>> {
        val pagingSourceFactory = { database.photoDao().getPhotosByQuery(query) }

        return Pager(
            config = PagingConfig(
                pageSize = 30,
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
