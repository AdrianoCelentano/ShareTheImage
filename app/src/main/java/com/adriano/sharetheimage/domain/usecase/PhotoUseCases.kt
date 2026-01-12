package com.adriano.sharetheimage.domain.usecase

import com.adriano.sharetheimage.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(private val repo: PhotoRepository) {
    operator fun invoke() = repo.getPhotos()
}

class SearchPhotosUseCase @Inject constructor(private val repo: PhotoRepository) {
    suspend operator fun invoke(query: String) = repo.searchPhotos(query)
}

class LoadMorePhotosUseCase @Inject constructor(private val repo: PhotoRepository) {
    suspend operator fun invoke(query: String, page: Int) = repo.loadMore(query, page)
}

class GetPhotoDetailUseCase @Inject constructor(private val repo: PhotoRepository) {
    operator fun invoke(id: String) = repo.getPhoto(id)
}

class RefreshPhotoDetailUseCase @Inject constructor(private val repo: PhotoRepository) {
    suspend operator fun invoke(id: String) = repo.refreshPhotoDetail(id)
}
