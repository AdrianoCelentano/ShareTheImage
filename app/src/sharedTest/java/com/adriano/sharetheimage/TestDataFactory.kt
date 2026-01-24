package com.adriano.sharetheimage

import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.remote.dto.UnsplashPhotoDto
import com.adriano.sharetheimage.data.remote.dto.UrlsDto
import com.adriano.sharetheimage.data.remote.dto.UserDto
import com.adriano.sharetheimage.domain.model.Photo

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
    tags = "tag1,tag2"
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
    tags = listOf("tag1, tag2")
)

fun photo(): Photo = Photo(
    id = "1",
    description = "Test Description",
    altDescription = null,
    urlFull = "url_full",
    urlSmall = "url_small",
    blurHash = "blur_hash",
    userName = "User",
    userBio = "Bio",
    tags = listOf("tag1, tag2"),
    width = 100,
    height = 100
)
