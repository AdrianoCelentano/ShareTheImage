package com.adriano.sharetheimage.data.mapper

import com.adriano.sharetheimage.data.photoEntitiy
import com.adriano.sharetheimage.data.unsplashPhotoDto
import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoMapperTest {

    @Test
    fun `map dto to entity`() {
        // Given
        val dto = unsplashPhotoDto()

        // When
        val entity = dto.toEntity()

        // Then
        assertEquals("1", entity.id)
        assertEquals(100, entity.width)
        assertEquals(200, entity.height)
        assertEquals("#FFFFFF", entity.color)
        assertEquals("blurhash", entity.blurHash)
        assertEquals("description", entity.description)
        assertEquals("altDescription", entity.altDescription)
        assertEquals("regular", entity.urlRegular)
        assertEquals("full", entity.urlFull)
        assertEquals("small", entity.urlSmall)
        assertEquals("name", entity.userName)
        assertEquals("username", entity.userUsername)
        assertEquals(null, entity.userProfileImage)
        assertEquals("bio", entity.userBio)
        assertEquals(null, entity.tags)
    }

    @Test
    fun `map entity to domain`() {
        // Given
        val entity = photoEntitiy()

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("1", domain.id)
        assertEquals(100, domain.width)
        assertEquals(100, domain.height)
        assertEquals("desc", domain.description)
        assertEquals("alt", domain.altDescription)
        assertEquals("regular", domain.urlRegular)
        assertEquals("full", domain.urlFull)
        assertEquals("small", domain.urlSmall)
        assertEquals("blurhash", domain.blurHash)
        assertEquals("name", domain.userName)
        assertEquals("bio", domain.userBio)
        assertEquals(listOf("tag"), domain.tags)
    }
}
