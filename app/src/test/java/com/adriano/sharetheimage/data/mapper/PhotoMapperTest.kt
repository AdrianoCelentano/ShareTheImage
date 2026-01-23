package com.adriano.sharetheimage.data.mapper

import com.adriano.sharetheimage.photoEntitiy
import com.adriano.sharetheimage.unsplashPhotoDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PhotoMapperTest {

    @Test
    fun `map dto to entity`() {
        // Given
        val dto = unsplashPhotoDto()

        // When
        val entity = dto.toEntity()

        // Then
        assertThat(entity.id).isEqualTo(dto.id)
        assertThat(entity.tags).isEqualTo("tag1, tag2")
    }

    @Test
    fun `map entity to domain`() {
        // Given
        val entity = photoEntitiy()

        // When
        val domain = entity.toDomain()

        // Then
        assertThat(entity.id).isEqualTo(domain.id)
        assertThat(domain.tags).containsExactly("tag1", "tag2")
    }
}
