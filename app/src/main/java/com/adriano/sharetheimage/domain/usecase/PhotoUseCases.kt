package com.adriano.sharetheimage.domain.usecase

import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotoDetailUseCase @Inject constructor(private val repo: PhotoRepository) {
    operator fun invoke(id: String): Photo? = repo.getPhoto(id)
}
