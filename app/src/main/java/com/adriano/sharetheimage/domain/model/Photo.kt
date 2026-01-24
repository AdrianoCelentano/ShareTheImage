package com.adriano.sharetheimage.domain.model

data class Photo(
    val id: String,
    val urlRegular: String,
    val urlFull: String,
    val urlSmall: String,
    val userName: String,
    val tags: List<String>,
    val width: Int,
    val height: Int,
    val blurHash: String? = null,
    val description: String? = null,
    val altDescription: String? = null,
    val userBio: String? = null,
)
