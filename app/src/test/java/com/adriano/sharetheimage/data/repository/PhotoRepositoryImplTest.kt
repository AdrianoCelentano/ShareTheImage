package com.adriano.sharetheimage.data.repository

import com.adriano.sharetheimage.data.local.DatabaseWrapper
import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.photoEntitiy
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PhotoRepositoryImplTest {

    private val api: UnsplashApi = mockk()
    private val dao: PhotoDao = mockk()
    private val database: DatabaseWrapper = mockk()
    private val repository: PhotoRepositoryImpl = PhotoRepositoryImpl(api, dao, database)

    @Test
    fun `getPhoto returns mapped photo from dao`() = runTest {
        // Given
        val entity = photoEntitiy()
        coEvery { dao.getPhotoById(entity.id) } returns entity

        // When
        val result = repository.getPhoto(entity.id)

        // Then
        assertThat(result).isNotNull
        assertThat(entity.id).isEqualTo(result?.id)
    }

    @Test
    fun `getPhoto returns null when dao returns null`() = runTest {
        // Given
        val photoId = "non_existent"
        coEvery { dao.getPhotoById(photoId) } returns null

        // When
        val result = repository.getPhoto(photoId)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getSearchStream returns flow of paging data`() = runTest {
        // Given
        coEvery { dao.getPhotosByQuery(any()) } returns mockk()

        // When
        val result = repository.getSearchStream("query")

        // Then
        assertThat(result).isInstanceOf(Flow::class.java)
    }
}