package com.adriano.sharetheimage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("DELETE FROM photos")
    suspend fun clearAll()

    @Query("SELECT * FROM photos WHERE id = :id")
    fun getPhotoById(id: String): Flow<PhotoEntity?>
    
    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoByIdOneShot(id: String): PhotoEntity?
}
