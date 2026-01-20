package com.adriano.sharetheimage.data.repository

import androidx.paging.PagingData
import com.adriano.sharetheimage.data.local.AppDatabase
import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.photoEntitiy
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.domain.model.Photo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoRepositoryImplTest {

    private val api: UnsplashApi = mockk()
    private val dao: PhotoDao = mockk()
    private val database: AppDatabase = mockk()
    private val repository: PhotoRepositoryImpl = PhotoRepositoryImpl(api, dao, database)

    @Test
    fun `getPhoto returns mapped photo from dao`() = runTest {
        // Given
        val photoId = "1"
        val entity = photoEntitiy(photoId)
        coEvery { dao.getPhotoById(photoId) } returns entity

        // When
        val result = repository.getPhoto(photoId)

        // Then
        assertNotNull(result)
        assertEquals(photoId, result?.id)
        assertEquals(entity.description, result?.description)
    }

    @Test
    fun `getPhoto returns null when dao returns null`() = runTest {
        // Given
        val photoId = "non_existent"
        coEvery { dao.getPhotoById(photoId) } returns null

        // When
        val result = repository.getPhoto(photoId)

        // Then
        assertNull(result)
    }

    @Test
    fun `getSearchStream returns flow of paging data`() = runTest {
        // Given
        coEvery { dao.getPhotosByQuery(any()) } returns mockk()

        // When
        val result = repository.getSearchStream("query")

        // Then
        assertTrue(result is Flow<PagingData<Photo>>)
        assertNotNull(result)
    }
}