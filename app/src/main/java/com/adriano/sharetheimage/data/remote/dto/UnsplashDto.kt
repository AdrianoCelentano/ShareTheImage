package com.adriano.sharetheimage.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    @SerialName("total") val total: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("results") val results: List<UnsplashPhotoDto>
)

@Serializable
data class UnsplashPhotoDto(
    @SerialName("id") val id: String,
    @SerialName("created_at") val createdAt: String? = null, // Make nullable if unsure
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("color") val color: String? = null,
    @SerialName("blur_hash") val blurHash: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("alt_description") val altDescription: String? = null,
    @SerialName("urls") val urls: UrlsDto,
    @SerialName("user") val user: UserDto,
    @SerialName("tags") val tags: List<TagDto>? = null // Detail often has tags, list might not
)

@Serializable
data class UrlsDto(
    @SerialName("raw") val raw: String,
    @SerialName("full") val full: String,
    @SerialName("regular") val regular: String,
    @SerialName("small") val small: String,
    @SerialName("thumb") val thumb: String
)

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("name") val name: String,
    @SerialName("bio") val bio: String? = null,
    @SerialName("profile_image") val profileImage: ProfileImageDto? = null
)

@Serializable
data class ProfileImageDto(
    @SerialName("small") val small: String,
    @SerialName("medium") val medium: String,
    @SerialName("large") val large: String
)

@Serializable
data class TagDto(
    @SerialName("title") val title: String
)
