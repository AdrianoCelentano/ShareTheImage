package com.adriano.sharetheimage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adriano.sharetheimage.data.local.entity.SearchQueryRemoteKey

@Dao
interface SearchQueryRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<SearchQueryRemoteKey>)

    @Query("SELECT * FROM search_query_remote_keys WHERE searchQuery = :query AND id = :photoId")
    suspend fun getRemoteKey(query: String, photoId: String): SearchQueryRemoteKey?

    @Query("DELETE FROM search_query_remote_keys WHERE searchQuery = :query")
    suspend fun clearRemoteKeys(query: String)
}
