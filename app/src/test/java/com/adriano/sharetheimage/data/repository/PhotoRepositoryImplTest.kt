package com.adriano.sharetheimage.data.repository

import com.adriano.sharetheimage.data.local.DatabaseWrapper
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.data.remote.mock.fakeUnsplashApi
import com.adriano.sharetheimage.fakes.FakeDatabaseWrapper
import com.adriano.sharetheimage.photoEntitiy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PhotoRepositoryImplTest {

    @Test
    fun `getPhoto returns null from dao`() = runTest {
        // Given
        val repository: PhotoRepositoryImpl = repository()

        // When
        val result = repository.getPhoto("1")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getPhoto returns mapped photo from dao`() = runTest {
        // Given
        val entity = photoEntitiy()
        val repository = repository(entity)

        // When
        val result = repository.getPhoto(entity.id)

        // Then
        assertThat(result).isNotNull
        assertThat(entity.id).isEqualTo(result?.id)
    }

    @Test
    fun `getSearchStream returns flow of paging data`() = runTest {
        // Given
        val repository = repository()

        // When
        val result = repository.getSearchStream("query")

        // Then
        assertThat(result).isInstanceOf(Flow::class.java)
    }
}

private suspend fun repository(entity: PhotoEntity? = null): PhotoRepositoryImpl {
    val api: UnsplashApi = fakeUnsplashApi()
    val database: DatabaseWrapper = FakeDatabaseWrapper()
    if (entity != null) database.photoDao().insertAll(listOf(entity))
    val repository: PhotoRepositoryImpl = PhotoRepositoryImpl(api, database)
    return repository
}