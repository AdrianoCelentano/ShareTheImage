package com.adriano.sharetheimage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.local.entity.PhotoEntity

@Database(entities = [PhotoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}
