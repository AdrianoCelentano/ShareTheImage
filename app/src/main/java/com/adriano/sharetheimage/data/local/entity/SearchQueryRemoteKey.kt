package com.adriano.sharetheimage.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "search_query_remote_keys",
    primaryKeys = ["searchQuery", "id"]
)
data class SearchQueryRemoteKey(
    val searchQuery: String,
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
