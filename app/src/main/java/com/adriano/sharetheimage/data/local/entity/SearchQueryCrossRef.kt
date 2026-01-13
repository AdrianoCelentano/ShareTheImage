package com.adriano.sharetheimage.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "search_query_cross_ref",
    primaryKeys = ["searchQuery", "photoId"],
    foreignKeys = [
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["photoId"])]
)
data class SearchQueryCrossRef(
    val searchQuery: String,
    val photoId: String,
    val orderIndex: Int
)
