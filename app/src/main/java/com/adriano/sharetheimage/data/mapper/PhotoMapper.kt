package com.adriano.sharetheimage.data.mapper

import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.remote.dto.UnsplashPhotoDto
import com.adriano.sharetheimage.domain.model.Photo

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
        tags = tags.joinToString(",")
    )
}

fun PhotoEntity.toDomain(): Photo {
    return Photo(
        id = id,
        width = width,
        height = height,
        description = description,
        altDescription = altDescription,
        urlRegular = urlRegular,
        urlFull = urlFull,
        urlSmall = urlSmall,
        blurHash = blurHash,
        userName = userName,
        userBio = userBio,
        tags = tags?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    )
}
