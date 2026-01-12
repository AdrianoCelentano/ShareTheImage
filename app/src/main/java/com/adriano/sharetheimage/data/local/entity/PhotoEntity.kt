package com.adriano.sharetheimage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val width: Int,
    val height: Int,
    val color: String?,
    val blurHash: String?,
    val description: String?,
    val altDescription: String?,
    val urlRegular: String,
    val urlFull: String,
    val urlSmall: String,
    val userName: String,
    val userUsername: String,
    val userProfileImage: String?,
    val userBio: String?,
    val tags: String? // Comma separated list of tags
)
