package com.adriano.sharetheimage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adriano.sharetheimage.data.local.entity.SearchQueryCrossRef

@Dao
interface SearchQueryCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<SearchQueryCrossRef>)

    @Query("DELETE FROM search_query_cross_ref WHERE searchQuery = :query")
    suspend fun clearCrossRefs(query: String)
}
