package com.adriano.sharetheimage.di

import android.content.Context
import androidx.room.Room
import com.adriano.sharetheimage.data.local.AppDatabase
import com.adriano.sharetheimage.data.local.DatabaseWrapper
import com.adriano.sharetheimage.data.local.RoomDatabaseWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "share_image_db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabaseWrapper(db: AppDatabase): DatabaseWrapper {
        return RoomDatabaseWrapper(db)
    }
}
