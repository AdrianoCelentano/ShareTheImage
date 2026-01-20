package com.adriano.sharetheimage.data

import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.remote.dto.UnsplashPhotoDto
import com.adriano.sharetheimage.data.remote.dto.UrlsDto
import com.adriano.sharetheimage.data.remote.dto.UserDto

fun photoEntitiy(photoId: String = "1"): PhotoEntity = PhotoEntity(
    id = photoId,
    width = 100,
    height = 100,
    color = "#000000",
    blurHash = "blurhash",
    description = "desc",
    altDescription = "alt",
    urlRegular = "regular",
    urlFull = "full",
    urlSmall = "small",
    userName = "name",
    userUsername = "username",
    userProfileImage = "img",
    userBio = "bio",
    tags = "tag"
)

fun unsplashPhotoDto(): UnsplashPhotoDto = UnsplashPhotoDto(
    id = "1",
    width = 100,
    height = 200,
    color = "#FFFFFF",
    blurHash = "blurhash",
    description = "description",
    altDescription = "altDescription",
    urls = UrlsDto(
        regular = "regular",
        full = "full",
        small = "small",
        raw = "raw",
        thumb = "thumb"
    ),
    user = UserDto(
        id = "userId",
        name = "name",
        username = "username",
        profileImage = null,
        bio = "bio"
    ),
    tags = null
)
