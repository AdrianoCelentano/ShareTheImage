package com.adriano.sharetheimage.data.repository

import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.data.remote.dto.UnsplashPhotoDto
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val api: UnsplashApi,
    private val dao: PhotoDao
) : PhotoRepository {

    override fun getPhotos(): Flow<List<Photo>> {
        return dao.getAllPhotos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPhoto(id: String): Flow<Photo?> {
        return dao.getPhotoById(id).map { it?.toDomain() }
    }

    override suspend fun searchPhotos(query: String) {
        val response = api.searchPhotos(query, 1) // Always page 1
        val entities = response.results.map { it.toEntity() }
        dao.clearAll()
        dao.insertAll(entities)
    }

    override suspend fun loadMore(query: String, page: Int) {
        val response = api.searchPhotos(query, page)
        val entities = response.results.map { it.toEntity() }
        dao.insertAll(entities)
    }

    override suspend fun refreshPhotoDetail(id: String) {
        val dto = api.getPhotoDetail(id)
        dao.insertAll(listOf(dto.toEntity()))
    }
}

fun UnsplashPhotoDto.toEntity(): PhotoEntity {
    return PhotoEntity(
        id = id,
        width = width,
        height = height,
        color = color,
        blurHash = blurHash,
        description = description,
        altDescription = altDescription,
        urlRegular = urls.regular,
        urlFull = urls.full,
        urlSmall = urls.small,
        userName = user.name,
        userUsername = user.username,
        userProfileImage = user.profileImage?.medium,
        userBio = user.bio,
        tags = tags?.joinToString(",") { it.title }
    )
}

fun PhotoEntity.toDomain(): Photo {
    return Photo(
        id = id,
        width = width,
        height = height,
        color = color,
        blurHash = blurHash,
        description = description,
        altDescription = altDescription,
        urlRegular = urlRegular,
        urlFull = urlFull,
        urlSmall = urlSmall,
        userName = userName,
        userUsername = userUsername,
        userProfileImage = userProfileImage,
        userBio = userBio,
        tags = tags?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    )
}
