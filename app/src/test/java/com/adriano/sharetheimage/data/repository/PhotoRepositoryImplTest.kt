package com.adriano.sharetheimage.data.repository

import com.adriano.sharetheimage.data.local.dao.PhotoDao
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.data.remote.dto.SearchResponseDto
import com.adriano.sharetheimage.data.remote.dto.UnsplashPhotoDto
import com.adriano.sharetheimage.data.remote.dto.UrlsDto
import com.adriano.sharetheimage.data.remote.dto.UserDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoRepositoryImplTest {

    private val api: UnsplashApi = mockk()
    private val dao: PhotoDao = mockk(relaxed = true)
    private val repository = PhotoRepositoryImpl(api, dao)

    @Test
    fun `searchPhotos fetches from api and saves to db`() = runTest {
        val query = "test"
        val mockResponse = SearchResponseDto(
            total = 1,
            totalPages = 1,
            results = listOf(
                UnsplashPhotoDto(
                    id = "1", width = 100, height = 100, urls = UrlsDto("","","","",""),
                    user = UserDto("u1", "user", "User Name")
                )
            )
        )
        coEvery { api.searchPhotos(query, 1) } returns mockResponse

        repository.searchPhotos(query)

        coVerify { dao.clearAll() }
        coVerify { dao.insertAll(any()) }
    }

    @Test
    fun `getPhotos returns flow from dao mapped to domain`() = runTest {
        val entity = PhotoEntity(
            id = "1", width = 100, height = 100, color = null, blurHash = null,
            description = null, altDescription = null, urlRegular = "reg",
            urlFull = "full", urlSmall = "small", userName = "user",
            userUsername = "username", userProfileImage = null, userBio = null, tags = null
        )
        coEvery { dao.getAllPhotos() } returns flowOf(listOf(entity))

        val result = repository.getPhotos().first()

        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }
}
