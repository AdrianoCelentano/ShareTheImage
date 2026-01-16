package com.adriano.sharetheimage.domain.model

data class Photo(
    val id: String,
    val description: String?,
    val altDescription: String?,
    val urlRegular: String,
    val urlFull: String,
    val urlSmall: String,
    val blurHash: String?,
    val userName: String,
    val userBio: String?,
    val tags: List<String>,
    val width: Int,
    val height: Int,
)
