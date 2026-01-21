package com.adriano.sharetheimage.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.adriano.sharetheimage.data.local.entity.PhotoEntity

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("DELETE FROM photos")
    suspend fun clearAll()

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: String): PhotoEntity?

    @Query("SELECT * FROM photos")
    suspend fun getAllPhotos(): List<PhotoEntity>

    @Transaction
    @Query("""
        SELECT photos.* FROM photos
        INNER JOIN search_query_cross_ref ON photos.id = search_query_cross_ref.photoId
        WHERE search_query_cross_ref.searchQuery = :query
        ORDER BY search_query_cross_ref.orderIndex ASC
    """)
    fun getPhotosByQuery(query: String): PagingSource<Int, PhotoEntity>
}
