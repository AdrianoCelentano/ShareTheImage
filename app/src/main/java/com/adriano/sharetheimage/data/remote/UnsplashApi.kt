package com.adriano.sharetheimage.data.remote

import com.adriano.sharetheimage.data.remote.dto.SearchResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class UnsplashApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun searchPhotos(query: String, page: Int, perPage: Int = 30): SearchResponseDto {
        return client.get("search/photos") {
            parameter("query", query)
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }
}
